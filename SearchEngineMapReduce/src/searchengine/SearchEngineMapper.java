/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger; 
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;


/**
 *
 * @author gk
 */
public class SearchEngineMapper extends Mapper<LongWritable, Text, Text, WordLocation> {
    
    String byteOffset;
    String fileName;
    String value;
    Text word=new Text();
    FileSplit fs;
    WordLocation wl = new WordLocation();
    Book book = new Book();
    Parser p = new Parser();
    private static Set<String> googleStopwords;
     
    static Logger logger=Logger.getLogger(SearchEngineMapper.class);

    static {
        googleStopwords = new HashSet<String>();
        googleStopwords.add("I"); googleStopwords.add("a"); 
        googleStopwords.add("about"); googleStopwords.add("an"); 
        googleStopwords.add("are"); googleStopwords.add("as");
        googleStopwords.add("at"); googleStopwords.add("be"); 
        googleStopwords.add("by"); googleStopwords.add("com"); 
        googleStopwords.add("de"); googleStopwords.add("en");
        googleStopwords.add("for"); googleStopwords.add("from"); 
        googleStopwords.add("how"); googleStopwords.add("in"); 
        googleStopwords.add("is"); googleStopwords.add("it");
        googleStopwords.add("la"); googleStopwords.add("of"); 
        googleStopwords.add("on"); googleStopwords.add("or"); 
        googleStopwords.add("that"); googleStopwords.add("the");
        googleStopwords.add("this"); googleStopwords.add("to"); 
        googleStopwords.add("was"); googleStopwords.add("what"); 
        googleStopwords.add("when"); googleStopwords.add("where");
        googleStopwords.add("who"); googleStopwords.add("will"); 
        googleStopwords.add("with"); googleStopwords.add("and"); 
        googleStopwords.add("the"); googleStopwords.add("www");
        
    }
    
    public void map(LongWritable key, Text values,Context cont) {

        book = p.getBook(values.toString(),book);
        
        if(!p.needtoParse(values.toString()))
            return;
        /** For the parsing lines **/
        
        if(values.toString().indexOf(p.getParseWordStart()) != -1 || 
               values.toString().indexOf(p.getParseWordStop()) != -1 )
            return;

        
        Pattern p = Pattern.compile("\\w+");
        Matcher m = p.matcher(values.toString());
        

        try {
                while(m.find()) {
                    value= m.group().toLowerCase();
                        if (!Character.isLetter(value.charAt(0)) 
                                || Character.isDigit(value.charAt(0))
                                || googleStopwords.contains(value)
                                || value.contains("_")) {
                            continue;
                        }
                    fs=(FileSplit)cont.getInputSplit();
                    fileName=fs.getPath().getName();
                    byteOffset=key.toString();
                    wl.clearLocations();
                    word.set(value+"@"+fileName);
                    wl.setWord(value);
                    wl.setBook(book);
                    wl.setFileName(fileName);
                    wl.addLocation(byteOffset);
                    wl.setCount(wl.getSize());
                    cont.write(word,wl);
                    logger.info("MAPPER KEY "+word.toString() + 
                            " WORD LOCATION "+wl.toString());
                }
        }
        catch(Exception ex)
        {
         ex.printStackTrace();
        }
        
        
    }
    
}
