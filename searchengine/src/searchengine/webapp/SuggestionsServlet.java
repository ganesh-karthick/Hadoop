package searchengine.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.mail.internet.NewsAddress;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import searchengine.XmlBookReader;
import searchengine.XmlVersionReader;
import searchengine.XmlWordLocationReader;
import searchengine.SuggestionsCache;


@SuppressWarnings("serial")
public class SuggestionsServlet extends HttpServlet {
	XmlParser pr;
	SuggestionsCache sc;
	HashSet<String> uniqueWords;
	private static Logger logger = Logger.getLogger(
			SuggestionsServlet.class.getCanonicalName());
	
	public void init() {
		try {
			pr = XmlParser.getInstance();
			if(!pr.hasReadersSet())
				pr.setReaders(new XmlWordLocationReader(getServletContext().getResourceAsStream("/WEB-INF/Data/results.xml"))
				, new XmlBookReader(getServletContext().getResourceAsStream("/WEB-INF/Data/books.xml"))
				, new XmlVersionReader(getServletContext().getResourceAsStream("/WEB-INF/Data/version.xml")));
			sc = new SuggestionsCache();
			pr.getWordReader().setSuggestionsCache(sc);
			pr.parseWordLocation();
			uniqueWords = new HashSet<String>(5,5);
			uniqueWords = pr.getWordReader().getUniqueWords();			
		}
		catch(Exception ex){
			if(ex instanceof NullPointerException)
				logger.warning(" Index files are missing");
			StringWriter sWriter = new StringWriter();
			ex.printStackTrace(new PrintWriter(sWriter));
			logger.warning(sWriter.getBuffer().toString()) ;
			ex.printStackTrace();
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		int suggestedWordCount = 0;
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		String typedWord  = req.getParameter("typedword");
		StringBuilder sb = new StringBuilder();
		for(String word : uniqueWords)
			if(word.indexOf(typedWord) != -1) {
				suggestedWordCount++;
				sb.append(word+":");
				if(suggestedWordCount == 5)
					break;
			}
		//handle no suggestions
		if(sb.length()>1)
			out.println(new String(sb.substring(0,sb.length()-1)));
		else
			out.println(" ");
					
	}
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

}
