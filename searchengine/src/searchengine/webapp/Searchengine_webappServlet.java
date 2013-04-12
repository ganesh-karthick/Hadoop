package searchengine.webapp;


import java.io.PrintWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import searchengine.WordLocation;
import searchengine.XmlBookReader;
import searchengine.XmlWordLocationReader;
import searchengine.XmlVersionReader;
import searchengine.Book;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.apphosting.api.DeadlineExceededException;


@SuppressWarnings("serial")
public class Searchengine_webappServlet extends HttpServlet {
	
	XmlParser pr;
	HashSet<Book> bset;
	HashSet<WordLocation> wlset;
	String results[];
	boolean isFirstTime;
	private static String versionType = "version";
	private static DatastoreService datastore =
            Util.getDatastoreServiceInstance();
	private static Logger logger = Logger.getLogger(
			Searchengine_webappServlet.class.getCanonicalName());
	
	int cacheCount = 100;
	
	public void init() throws ServletException {
		try {
			String datastoreVersion;
			isFirstTime = false;
			pr = XmlParser.getInstance();
			if(!pr.hasReadersSet())
				pr.setReaders(new XmlWordLocationReader(getServletContext().getResourceAsStream("/WEB-INF/Data/results.xml"))
				, new XmlBookReader(getServletContext().getResourceAsStream("/WEB-INF/Data/books.xml"))
				, new XmlVersionReader(getServletContext().getResourceAsStream("/WEB-INF/Data/version.xml")));
			pr.parseVersion();
			datastoreVersion = getDatastoreVersion();
			logger.info(" DataStoreVersion " + datastoreVersion + " InputFileVersion "+
					pr.getVersionReader().getVersion());
			System.out.println(" DataStoreVersion " + datastoreVersion + " InputFileVersion "+
					pr.getVersionReader().getVersion());
			
			if(datastoreVersion == null ||
					Float.parseFloat(datastoreVersion) < pr.getVersionReader().getVersion() ) {
				pr.parseBook();
				bset = pr.getBookReader().getBooks();
				pr.parseWordLocation();
				wlset = pr.getWordReader().getWordLocations();
				if(datastoreVersion == null)
					isFirstTime = true;
				initDataStore(wlset, bset , isFirstTime, Float.toString(pr.getVersionReader().getVersion()));
			}
		}
		catch(Exception ex) {
			if(ex instanceof NullPointerException)
				logger.warning(" Index files are missing");
			if( ex instanceof DeadlineExceededException)
				logger.warning("Servlet processing exceeded 1 min.");
			StringWriter sWriter = new StringWriter();
			ex.printStackTrace(new PrintWriter(sWriter));
			logger.warning(sWriter.getBuffer().toString()) ;		
		}
			
	}
	
	public String getDatastoreVersion() {
		try {
			Key key = KeyFactory.createKey(versionType,versionType);
			Iterable<Entity> e = Util.listChildren("version", key);
			if(e == null)
				return null;
			else 
				return (String)e.iterator().next().getProperty("versionNumber");
		}
		catch(NoSuchElementException ex){
			System.out.println(" Version does not exist.");
			logger.warning("Version does not exist.");
		}
		return null;
	}
	
	public void createNewVersion(boolean isFirstTime , String inputFileVersion){
		Key key = KeyFactory.createKey(versionType,versionType);
		Entity version = new Entity("version",key);
		version.setProperty("versionNumber", inputFileVersion);		
		Util.persistEntity(version,false);
	}
	
	/**
	 * @param wlset
	 * @param bset
	 * @param isFirstTime
	 * @param inputFileVersion
	 */
	public void initDataStore(HashSet<WordLocation> wlset, HashSet<Book> bset ,
			boolean isFirstTime , String inputFileVersion) {
		Key key;
		clearDataStore();
		createNewVersion(isFirstTime, inputFileVersion);
		logger.info("Loading Book " + bset.size());
		System.out.println("Loading Book " + bset.size());
		for(Book b:bset) {
			key = KeyFactory.createKey("Book", b.getTitle()+"@"+b.getAuthor());
			Entity book = new Entity("Book", key);
			book.setProperty("title", b.getTitle());
			book.setProperty("author", b.getAuthor());
			book.setProperty("releaseDate",b.getReleaseDate());
			book.setProperty("language", b.getLanguage());
			Util.persistEntity(book,true);		
		}
		logger.info("Loading WordLocation " + wlset.size());
		System.out.println("loading WordLocation " + wlset.size());
		//Use testdriver program to load large dataset
		/*for( WordLocation wl : wlset) {
			key = KeyFactory.createKey("WordLocation",
					wl.getWord()+"@"+wl.getFileName());
			Entity word = new Entity("WordLocation",key);
			word.setProperty("word", wl.getWord());
			word.setProperty("fileName", wl.getFileName());
			word.setProperty("count", new Long(wl.getCount()));
			//Text can handle more 500 chars
			word.setProperty("locations",new Text(wl.getStringifiedLocations(true)));
			word.setProperty("Book",getBook(wl.getBook()).toString());
			datastore.put(word);
	
		} */
		
	}
	public void clearDataStore() {
		Iterable<Entity> entities = Util.getAllItems("WordLocation");
		for( Entity e: entities)
			datastore.delete(e.getKey());//Util.deleteEntity(e.getKey());
		entities = Util.getAllItems("Book");
		for( Entity e: entities)
			Util.deleteEntity(e.getKey());
		entities = Util.getAllItems("version");
		for( Entity e: entities)
			Util.deleteEntity(e.getKey());
		logger.info("Clearing DataStore");
		System.out.println("Clearing DataStore");
		
	}
	
	public Book getBook(Book bval) {
		Book book = new Book();
		for (Book b:bset)
			if(b.getTitle().equalsIgnoreCase(bval.getTitle()))
				return b;
		return book;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		WordLocation wl = new WordLocation();
		int resultId = 1;
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<h3>Results</h3>" +
		       "<body bgcolor=#FFFFCC>");
		
		try {
				String searchString = req.getParameter("query");
				System.out.println("query "+ searchString);
				logger.info("query "+ searchString);
				if(searchString.trim().length() == 0){
					out.println("No search results found");
					return;
				}
				QueryParser qp = new QueryParser(searchString);
				QueryExecutor qe = new QueryExecutor(datastore, qp.getOperator(),
						qp.getRawOperator(),qp.getSearchStrings());		
				ArrayList<Entity> entities = qe.execute();
				if(entities.size() == 0) {
					out.println("No search results found");
					return;
				}
				for( Entity e: entities) {
					wl.setWord((String)e.getProperty("word"));
					wl.setFileName((String)e.getProperty("fileName"));
					Text locations = (Text)e.getProperty("locations");
					wl.setLocations(WordLocation.parseLocation(locations.getValue()));
					Long count = (Long) e.getProperty("count");
					wl.setCount(count.intValue());
					wl.setBook((Book.parseBook((String)e.getProperty("Book"))));
					out.println("<div id = result" + resultId +">");
					out.println("<a  target=\"_blank\" href=\"http://guttenberginstantsearch.appspot.com/file.jsp?word="+
					wl.getWord()+"&fileName="+wl.getFileName()+"&index="+(long)count/2
					+"\" target=\"_blank\"" +" >");
					out.println(wl.toHTMLString());
					out.println("</a>");
					out.println("</div><br/><br/>");		
					resultId++;
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			logger.warning(sWriter.getBuffer().toString()) ;
			e.printStackTrace();
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}
	
}
