package searchengine.webapp;



import org.xml.sax.SAXException;

import searchengine.XmlBookReader;
import searchengine.XmlVersionReader;
import searchengine.XmlWordLocationReader;

public class XmlParser {
	private XmlWordLocationReader wordReader;
	private XmlBookReader bookReader;
	private XmlVersionReader versionReader;

	private volatile boolean isParsedBook = false;
	private volatile boolean isParsedWord = false;
	private volatile boolean isParsedVersion = false;
	private volatile boolean isReadersSet = false;
	
	private XmlParser() { }
 
    private static class XmlParserHolder { 
        public static final XmlParser instance = new XmlParser();
    }

    public static XmlParser getInstance() {
        return XmlParserHolder.instance;
    }
	
    public synchronized void  setReaders(XmlWordLocationReader wordReader, XmlBookReader bookReader,
    		XmlVersionReader versionReader) {
    	if(!isReadersSet) {
	    	this.bookReader = bookReader;
	    	this.wordReader = wordReader;
	    	this.versionReader = versionReader;
    	}
    	isReadersSet = true;
    }
    public synchronized  void parseBook() throws SAXException {
    	if(!isParsedBook)
    		bookReader.parse();
    	isParsedBook = true;
    }
    public synchronized void parseWordLocation() throws SAXException{
    	if(!isParsedWord)
    		wordReader.parse();
    	isParsedWord = true;
    }
    public synchronized void parseVersion() throws SAXException{
    	if(!isParsedVersion)
    		versionReader.parse();
    	isParsedVersion = true;
    }
    public synchronized void parseAll() throws SAXException{
    	parseVersion();
    	parseBook();
    	parseWordLocation();
    }

	public XmlVersionReader getVersionReader() {
		return versionReader;
	}

	public boolean hasParsedWord() {
		return isParsedWord;
	}

	public boolean hasParsedVersion() {
		return isParsedVersion;
	}
	
	public boolean hasParsedBook() {
		return isParsedBook;
	}

	public XmlWordLocationReader getWordReader() {
		return wordReader;
	}

	public XmlBookReader getBookReader() {
		return bookReader;
	}

	public boolean hasReadersSet() {
		return isReadersSet;
	}

	public void setReadersSet(boolean isReadersSet) {
		this.isReadersSet = isReadersSet;
	}
	
	
    
}

