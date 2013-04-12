package searchengine.webapp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;


import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;


import searchengine.WordLocation;

@SuppressWarnings("serial")
public class FileContentServerServlet extends HttpServlet {
	
	final int noOfbytesTodisplay = 512;
	byte fileContent[] = new byte[noOfbytesTodisplay];
	private static Logger logger = Logger.getLogger(
				FileContentServerServlet.class.getCanonicalName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String fileName = req.getParameter("fileName");
		String word = req.getParameter("word");
		long offset = 0;
		Entity e = null;
		int positionIndex = 0 , bytesRead = 0;
		try {
			try {
				positionIndex = Integer.parseInt(req.getParameter("index"));
			}
			catch(NumberFormatException ex){
				ex.printStackTrace();
				positionIndex = 0;
			}
			logger.info("filename "+ fileName
					+ " word "+word+" positionIndex "+positionIndex);
			System.out.println("filename "+ fileName 
					+ " word "+word+" positionIndex "+positionIndex);
			resp.setContentType("text/html");
			PrintWriter out = resp.getWriter();
			InputStream is = 
					getServletContext().getResourceAsStream("/WEB-INF/Data/guttenberg/"+fileName);
			if( is == null) {
				out.write("File Not found at Server .");
				return;
			}		
			Key key = KeyFactory.createKey("WordLocation", word+"@"+fileName);
			ArrayList<Entity> children = QueryExecutor.findEntity(word, fileName);
			if( children == null) {
				out.write("Stale Entry or Entry Missing, Try after sometime.");
				return;
			}
			for(Entity child:children) {
				System.out.println(" check : " + (String)child.getProperty("word") +" "+
						(String)child.getProperty("fileName"));
				if(word.equals((String)child.getProperty("word")) && 
						fileName.equals((String)child.getProperty("fileName"))) {
					e = child;
					break;
				}
				
			}
				
					
			if(e == null) {
				out.write("Word not found.");
				return;	
			}
			Text loc = (Text) e.getProperty("locations");
			System.out.println("el "+ loc.getValue());
			Object locations[] 
			= WordLocation.parseLocation(loc.getValue()).toArray();
			DataInputStream in = new DataInputStream(is);
			if(positionIndex < 0 || positionIndex >= locations.length 
					|| locations.length == 0){
				out.write("No more occurrences of the word.");
				return;
			}
			System.out.println("eloc "+locations[positionIndex].toString()+
					" len "+locations.length);
			// Handle file size smaller than initial offset
			offset = Math.max(Long.parseLong(locations[positionIndex].toString())-1,
					         ((Long.parseLong(locations[positionIndex].toString()) 
							- (noOfbytesTodisplay/2))));
			// Move the file pointer to 512 bytes prior to word
			System.out.println("offset "+offset);
			while(in.read() != -1 && offset > 0)
				offset--;
			bytesRead = in.read(fileContent);
			if(bytesRead <= 0) {
				out.write("No more content to Read or Read failed.");
				return;
			}
			out.write(new String(fileContent,0,bytesRead));
		} catch (Exception ex) {
			// TODO Auto-generated catch block
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
