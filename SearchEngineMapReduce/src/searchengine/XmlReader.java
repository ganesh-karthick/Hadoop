/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

/**
 *
 * @author gk
 */
 

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;
import java.io.InputStream;

public abstract class  XmlReader extends DefaultHandler
{
    SAXParserFactory factory;
    SAXParser saxParser;
    String filePath;
    InputStream is;
    volatile boolean  isEndOfDocument = false;
    static Logger logger=Logger.getLogger(XmlReader.class);
        
    abstract public void startElement(String uri, String localName,
    	            String qName, Attributes attributes) throws SAXException;
    
    abstract public void endElement(String uri, String localName,
    	                String qName) throws SAXException;
    abstract public void characters(char ch[], int start, int length)
            throws SAXException;
    
    public void parse() throws SAXException
    {
        try{
            logger.info("Parsing " + filePath);
            saxParser.parse(is,this);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            isEndOfDocument = true;
        }
        
    }
    
    public void endDocument() throws SAXException {
        isEndOfDocument = true;
    }
     
}