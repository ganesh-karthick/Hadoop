/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import java.io.IOException;

public abstract class  XmlReader extends DefaultHandler
{
    SAXParserFactory factory;
    SAXParser saxParser;
    String filePath;
    volatile boolean  isEndOfDocument = false;
        
    abstract public void startElement(String uri, String localName,
    	            String qName, Attributes attributes) throws SAXException;
    
    abstract public void endElement(String uri, String localName,
    	                String qName) throws SAXException;
    abstract public void characters(char ch[], int start, int length)
            throws SAXException;
    
    public void parse() throws SAXException
    {
        try{
            System.out.println(filePath);
            saxParser.parse(filePath,this);
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