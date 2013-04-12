/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.HashSet;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Date;
import java.util.Collections;
/**
 *
 * @author gk
 */

/* ** SAMPLE SCHEMA **
* <worddetails>
* <word>abandon</word>
* <filename>35.txt</filename>
* <count>—</count>
* <locations>:47317:18540:16807:12474:</locations>
* <title>The Time Machine</title>
* </worddetails>
* <worddetails>
 */


public class XmlWordLocationReader extends XmlReader {
    
    boolean isWordDetails;
    boolean isWord;
    boolean isfileName;
    boolean isCount;
    boolean isLocations;
    boolean isTitle;
    WordLocation wl;
    HashSet<WordLocation> wlset;
    SuggestionsCache sc;


    public XmlWordLocationReader(String filePath) 
            throws ParserConfigurationException ,SAXException {
        
        factory =  SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();
        this.filePath = filePath;
        wl = new WordLocation();
        wlset =  new HashSet<WordLocation>(5,5);   
        sc = new SuggestionsCache();
    }
    
    public XmlWordLocationReader(InputStream is) 
            throws ParserConfigurationException ,SAXException {
        
        factory =  SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();
        if(is == null)
            throw new NullPointerException(" InputStream is Null ");

        wl = new WordLocation();
        wlset =  new HashSet<WordLocation>(5,5);
        sc = new SuggestionsCache();
    }
    
    public void startElement(String uri, String localName,
    	            String qName, Attributes attributes) throws SAXException {
        	if (qName.equalsIgnoreCase("WordDetails")) {
			isWordDetails = true;
		}
                 if (qName.equalsIgnoreCase("word")) {
			isWord = true;
		}
		if (qName.equalsIgnoreCase("fileName")) {
			isfileName = true;
		}
 
		if (qName.equalsIgnoreCase("Count")) {
			isCount = true;
		}
 
		if (qName.equalsIgnoreCase("Locations")) {
			isLocations = true;
		}
                if (qName.equalsIgnoreCase("Title")) {
			isTitle = true;
		}
        
    }

    
    public void endElement(String uri, String localName,
    	                String qName) throws SAXException {
        if(qName.equalsIgnoreCase("worddetails")) {
            wlset.add(wl);
            wl = new WordLocation();
            isWordDetails = false;
        }
    }
    
    
    public void characters(char ch[], int start, int length)
            throws SAXException {
        try {
            if(isWordDetails) {
                wl.setWord(new String(ch,start,length));
                isWordDetails = false;
            }
            if(isWord) {
                wl.setWord(new String(ch,start,length));
                isWord = false;
            }
            if(isfileName) {
                wl.setFileName(new String(ch,start,length));
                isfileName = false;
            }
            if(isCount) {
                wl.setCount(Integer.parseInt(new String(ch,start,length)));
                isCount = false;
            }
            if(isTitle) {
                wl.getBook().setTitle(new String(ch,start,length));
                isTitle = false;
            }
            if(isLocations) {  
                 wl.setLocations(WordLocation.parseLocation(new String(ch,start,length)));
                isLocations = false;
            }
           
        }
        catch(Exception ex) {
            ex.printStackTrace();
            isEndOfDocument = true;
        }
    }
    
    public HashSet<WordLocation> getWordLocations() {
        
        return wlset;
    }
    
    public HashSet<String> getUniqueWords() {

        return sc.getUniqueWords();
    }

    public SuggestionsCache getSuggestionsCache() {
        return sc;
    }

    public void setSuggestionsCache(SuggestionsCache sc) {
        this.sc = sc;
    }
        
    public void endDocument(){
        ArrayList<WordLocation> wllist = new ArrayList(wlset);
        Date d = new Date();
        //Shuffle such that we dont pick all words from one book
        Collections.shuffle(wllist, new Random(d.toString().hashCode()));
        for(WordLocation w : wllist)
            sc.addWord(w.getWord());           
    }
}