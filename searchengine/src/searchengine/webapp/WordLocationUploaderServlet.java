package searchengine.webapp;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;



import searchengine.WordLocation;



import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.apphosting.api.DeadlineExceededException;


public class WordLocationUploaderServlet extends HttpServlet {
	
	static long numberOfRecords;
	private static DatastoreService datastore =
            Util.getDatastoreServiceInstance();
	private static Logger logger = Logger.getLogger(
			WordLocationUploaderServlet.class.getCanonicalName());
	
	public void init() throws ServletException {
		numberOfRecords = Util.getCountOfEntities("WordLocation", null , null);		
	}
	
	public void deleteEntity(String word , String fileName) {
		ArrayList<Entity> wordList = QueryExecutor.findEntity(word, fileName);
		
		if(!wordList.isEmpty()){
			for( Entity e:wordList) {
				System.out.println("key " + KeyFactory.stringToKey((String)e.getProperty("key")));
				datastore.delete(e.getKey());
				numberOfRecords--;
			}
				
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		String wordlocation[] = new String[3];
		WordLocation wl = new WordLocation();
		String update = "false";
		boolean isUpdate = false;
		Entity word = null;
		Key key;
		try{
			wordlocation[0] = req.getParameter("wl1");
			wordlocation[1] = req.getParameter("wl2");
			wordlocation[2] = req.getParameter("wl3");
			update = req.getParameter("update");
			
			if(update != null)
				if(update.equalsIgnoreCase("true"))
					isUpdate = true;
						
			for(String w:wordlocation){		
				if(w == null || w.indexOf("WordLocation") == -1){
					out.write("Empty/Invalid parameter wl1 ");
					continue;
				}
				try{
					wl = WordLocation.parseWordLocation(w);
					if(wl.getWord().trim().length()==0 ||
							wl.getFileName().trim().length() == 0) {
						out.write("Empty/Invalid parameter wl1 "+wl);
						return;
					}
					
				}
				catch(NumberFormatException ex){
					out.write("Invalid parameter wordlocation "+ w);
					continue;
				}
				catch(NoSuchElementException ex){
					out.write("Missing parameter wordlocation " + w);
					continue;
				}
				System.out.println("Process "+wl.getWord()+" "+wl.getFileName());
				key = KeyFactory.createKey("WordLocation",wl.getWord()
						+"@"+wl.getFileName());
				if(isUpdate){
					deleteEntity(wl.getWord(),wl.getFileName());
					out.write("\n Deleting key , Count :" + Long.toString(numberOfRecords));
					if(word == null) //update_or_insert
						word = new Entity("WordLocation", key);
				}
				else{
					word = datastore.get(key);
					if(word == null) //update_or_insert
						word = new Entity("WordLocation", key);
				}
				word.setProperty("word", wl.getWord());
				word.setProperty("fileName", wl.getFileName());
				word.setProperty("count",new Long(wl.getCount()));
				//Text can handle more 500 chars
				word.setProperty("locations",new Text(wl.getStringifiedLocations(true)));
				word.setProperty("Book",(wl.getBook()).toString());
				word.setProperty("key", KeyFactory.keyToString(key));
				System.out.println("key " + KeyFactory.keyToString(key));
				datastore.put(word);
				numberOfRecords++;
				out.write("\n Success Count:"+ Long.toString(numberOfRecords));

			}			
					
		}
		catch(Exception ex){
			if( ex instanceof DeadlineExceededException)
				logger.warning("Servlet processing exceeded 1 min.");
			StringWriter sWriter = new StringWriter();
			ex.printStackTrace(new PrintWriter(sWriter));
			logger.warning(sWriter.getBuffer().toString()) ;
			ex.printStackTrace();
			
		}
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

}
