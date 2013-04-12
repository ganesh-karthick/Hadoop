package searchengine.webapp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

import java.util.logging.Logger;




public class EntityComparator implements Comparator<Entity> {
	
	private static Logger logger = Logger.getLogger(
			EntityComparator.class.getCanonicalName());
	
	
	public int compare(Entity e1 , Entity e2) {
		
		try{
				
		Text s1 = (Text)e1.getProperty("locations");
		Text s2 = (Text)e2.getProperty("locations");
		String sa[] = s1.getValue().split(":");
		String sb[] = s2.getValue().split(":");
		if(sa.length > sb.length)
			return -1;
		else
			return 1;
				
		}
		catch(Exception e){
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			logger.warning(sWriter.getBuffer().toString()) ;
			e.printStackTrace();
		}
		return 0;
		
	}

}
