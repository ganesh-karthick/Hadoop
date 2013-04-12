/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.InputStream;

/**
 *
 * @author gk
 */
public class XmlVersionReader extends XmlReader {
    float version;
    boolean isVersion;

    public XmlVersionReader(String filePath)
    throws ParserConfigurationException ,SAXException {
        factory =  SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();
        this.filePath = filePath;
        version = (float) 1.0;
    }
    
    public XmlVersionReader(InputStream is)
    throws ParserConfigurationException ,SAXException {
        factory =  SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();
        this.is = is;
        if(is == null)
            throw new NullPointerException(" InputStream is Null ");
        version = (float) 1.0;
    }
    
    public void startElement(String uri, String localName,
                String qName, Attributes attributes) throws SAXException {
                    
        if (qName.equalsIgnoreCase("version")) {
                isVersion = true;
        }
    }
    public void characters(char ch[], int start, int length)
        throws SAXException {
        try {
            if(isVersion) {
                version = Float.parseFloat(new String(ch,start,length).trim());
            }      
        }
        catch(Exception ex) {
            ex.printStackTrace();
            isEndOfDocument = true;
        }
    }

    public void endElement(String uri, String localName,
                    String qName) throws SAXException {
         if(qName.equalsIgnoreCase("version")) {
            isVersion = false;
        }

    }

    public float getVersion() {
        return version;
    }
 
}
