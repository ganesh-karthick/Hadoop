/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;
import java.util.*;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;


/**
 *
 * @author gk
 */
public class SearchEngineReducer extends Reducer<Text, WordLocation, Text,
        WordLocation> {
    
    StringBuilder sb=new StringBuilder();
    static Logger logger=Logger.getLogger(SearchEngineReducer.class);
    WordLocation wl = new WordLocation();
    
    
    protected void reduce(Text key, Iterable<WordLocation> values, Context cont){
        
        String data[] = key.toString().split("@");
        WordLocation temp = null;      
        wl.clearLocations();
        
        if(data.length != 0) {
            wl.setWord(data[0]);
            wl.setFileName(data[1]);
        }
        Iterator<WordLocation> itr=values.iterator();
        try{
            while(itr.hasNext()) {
                temp = (WordLocation) itr.next();
                wl.addLocations(temp.getLocations()); 
                wl.setCount(wl.getSize());
            } 
            if(temp != null)
             wl.setBook(temp.getBook());
            logger.info(" REDUCER KEY "+key.toString() + " WORD LOCATION "+wl.toString());
            cont.write(key,wl);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
    }
    
}
