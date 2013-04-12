package searchengine.webapp;
import java.util.HashSet;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query.FilterOperator;



public class QueryParser {
	static HashSet<String> operators = new HashSet<String>();
	static {
	operators.add("AND");
	operators.add("OR");
	operators.add("EQUAL");
	}
	
	private String query; 
	private String rawOperator;
	private static Logger logger = Logger.getLogger(
			QueryParser.class.getCanonicalName());
	
	public QueryParser( String query){
		this.query = query;
		this.rawOperator = "EQUAL";
	}
	
	public HashSet<String> getSearchStrings() {
		HashSet<String> words = new HashSet<String>(5,5);
		String tokens[] = query.split("\\s+");
		for(String word: tokens) 
			if(!operators.contains(word.toUpperCase()))
				words.add(word);
		
		return words;
		
	}
	
	public  FilterOperator getOperator() {
		String tokens[] = query.split("\\w+");
		for(String word: tokens) 
			if(operators.contains(word.toUpperCase())) {
				rawOperator = word.toUpperCase();
				return FilterOperator.IN;		
			}
				
		return FilterOperator.EQUAL;	
	}

	public String getRawOperator() {
		return rawOperator;
	}

	public void setRawOperator(String rawOperator) {
		this.rawOperator = rawOperator;
	}
	

}
