package searchengine.webapp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;


public class QueryExecutor {
	 private Query query;
	 private PreparedQuery preparedQuery;
	 private FilterOperator fop;
	 private HashSet<String> words;
	 private DatastoreService datastore;
	 String rawOperator;
	 private static Logger logger = Logger.getLogger(
				QueryExecutor.class.getCanonicalName());
	 
	 
	 public QueryExecutor(DatastoreService datastore, FilterOperator fop ,
			 String rawOperator, HashSet<String> words) {	 
		 this.datastore = datastore;
		 this.fop = fop;
		 this.words = words;	
		 this.rawOperator = rawOperator;
	 }
	 
	 public ArrayList<Entity> execute() {
		 	 
		 ArrayList<Entity> result = new ArrayList<Entity>();
		 if(words.size() == 0)
			 return result;
		 query = new Query("WordLocation");
		 query.addFilter("word", FilterOperator.IN, words);
		 query.addSort("count", SortDirection.DESCENDING);
		 preparedQuery = datastore.prepare(query);
		 result.addAll(preparedQuery.asList(FetchOptions.Builder.withDefaults()));
		 // Sort for multiple words in suggestions
		 Collections.sort(result , new EntityComparator());
		 

		 if(rawOperator.equals("AND")) {
			 for(String word:words) {
				 query = new Query("WordLocation");
				 query.addFilter("word", FilterOperator.EQUAL, word);
				 query.addSort("count", SortDirection.DESCENDING);
				 preparedQuery = datastore.prepare(query);
				 result.retainAll(preparedQuery.asList(FetchOptions.Builder.withDefaults()));
				// Sort for multiple words in suggestions
				 Collections.sort(result , new EntityComparator());
			 }	 
		 }
		 return result;	 
	 }
	 
	 public static ArrayList<Entity> findEntity(String word, String fileName) {
		 ArrayList<Entity> result = new ArrayList<Entity>();
		 if(word.length() == 0 || fileName.length() == 0)
			 return result;
		 Query query = new Query("WordLocation");
		 query.addFilter("word", FilterOperator.EQUAL, word);
		 query.addFilter("fileName", FilterOperator.EQUAL, fileName);
		 PreparedQuery preparedQuery = Util.getDatastoreServiceInstance().prepare(query);
		 result.addAll(preparedQuery.asList(FetchOptions.Builder.withDefaults()));
		 return result;
		 
	 }

}
