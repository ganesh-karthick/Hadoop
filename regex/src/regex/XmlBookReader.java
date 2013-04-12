/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author gk
 */

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.HashSet;
/**
 *
 * @author gk
 */

/* ** SAMPLE SCHEMA **
<book>
<title>The Narrative of the Life of Frederick Douglass</title>
<author>Frederick Douglass</author>
<releasedate>January 10</releasedate>
<language>English</language>
</book>
 */
public class XmlBookReader extends XmlReader {
    
    boolean isBook;
    boolean isTitle;
    boolean isAuthor;
    boolean isReleaseDate;
    boolean isLanguage;
    Book book;
    HashSet<Book> bookset;

    public XmlBookReader(String filePath) 
            throws ParserConfigurationException ,SAXException {
        
        factory =  SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();
        this.filePath = filePath;
        book = new Book();
        bookset =  new HashSet<Book>(5,5);   
    }
    
    public void startElement(String uri, String localName,
    	            String qName, Attributes attributes) throws SAXException {
                    
                if (qName.equalsIgnoreCase("isBook")) {
			isBook = true;
		}

                if (qName.equalsIgnoreCase("author")) {
			isAuthor = true;
		}
		if (qName.equalsIgnoreCase("ReleaseDate")) {
			isReleaseDate = true;
		}
 
		if (qName.equalsIgnoreCase("Language")) {
			isLanguage = true;
		}
 
                if (qName.equalsIgnoreCase("Title")) {
			isTitle = true;
		}
        
    }
    
    public void endElement(String uri, String localName,
    	                String qName) throws SAXException {
        if(qName.equalsIgnoreCase("book")) {
            bookset.add(book);
            book = new Book();
            isBook = false;
        }
        
   
    }
    
    public void characters(char ch[], int start, int length)
            throws SAXException {
        try {
            if(isBook) {
                book.setAuthor(new String(ch,start,length));
            }
            if(isAuthor) {
                book.setAuthor(new String(ch,start,length));
                isAuthor = false;
            }
            if(isTitle) {
                book.setTitle(new String(ch,start,length));  
                isTitle = false;
            }
            if(isReleaseDate) {
                book.setReleaseDate(new String(ch,start,length));
                isReleaseDate = false;
            }
            if(isLanguage) {  
                book.setLanguage(new String(ch,start,length));
                isLanguage = false;
            }
           
        }
        catch(Exception ex) {
            ex.printStackTrace();
            isEndOfDocument = true;
        }
    }
    

    public HashSet<Book> getBooks() {
        while(!isEndOfDocument){}
        return bookset;
    }
       
    
}
