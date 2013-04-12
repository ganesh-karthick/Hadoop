/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.util.HashSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.hadoop.io.WritableComparable;

/**
 *
 * @author gk
 */
public class WordLocation extends Object implements WritableComparable{
    
    private String word;
    private String fileName;
    private Book book;
    private HashSet<String> locations;
    private int count;
    static Logger logger = Logger.getLogger(WordLocation.class);

    public WordLocation() {
        word = new String();
        locations = new HashSet<String>(5,5);
        book = new Book();
        this.count = 0;
    }
    

    public WordLocation(String word, HashSet<String> locations, Book b) {
        this.word = word;
        this.locations = locations;
        this.book = b;
        this.count = 0;
    }

    public void removeLocations(String offset) {
        this.locations.remove(offset);
    }

    public void addLocation(String offset) {
        this.locations.add(offset);
    }
    public void addLocations(HashSet<String> offsets) {
        this.locations.addAll(offsets);
    }
    public String getWord() {
        return word;
    }

    public HashSet<String> getLocations() {
        return locations;
    }

    public void setWord(String word) {
        this.word = word;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book b) {
        this.book = b;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
    
    public int getSize() {
        return locations.size();
    }

    public void setLocations(HashSet<String> locations) {
        this.locations = locations;
    }
    
    
    public String getStringifiedLocations(boolean getAll) {
        StringBuilder sb =  new StringBuilder(":");
        final int threshold = 3;
        int count = 0;
        
        for(String offset : locations) {
            sb.append(offset);
            sb.append(":");
            count++;
            if(!getAll && count > threshold)
                break;
        }
        return sb.toString();
    }
    
    public void clearLocations()
    {
        locations.clear();
    }

    @Override
    public String toString() {
        return "WordLocation{" + "word=" + word + ", fileName=" + fileName + 
                ", count=" + Integer.toString(getSize()) + ", locations="
                + getStringifiedLocations(true) + "\n" + book + '}';
    }


    
    public void write(DataOutput out) throws IOException {
        
        out.write((this.toString()+"\n").getBytes());
    }
    
    public void readFields(DataInput in) throws IOException {
        
        String data = new String(in.readLine());
        String tokens[] = data.split("[=]|[\\{]|[\\}]|[,]");
        try {
            if(data.indexOf("WordLocation") != -1 || tokens.length > 0) {
                for( int  i = 0; i<tokens.length; i++ ) {
                    if(tokens[i].indexOf("word")!=-1)
                     word = tokens[i+1].trim();
                    else if(tokens[i].indexOf("fileName")!=-1)
                     fileName = tokens[i+1].trim();
                    else if(tokens[i].indexOf("location")!=-1)
                      locations = parseLocation(tokens[i+1]);
                }
                book.readFields(in);
            }
            else {
                 logger.warn("WordLocation:Invalid Record read " + data);
            }
        }
        catch(Exception ex)
        {
            logger.error("WordLocation: Error Record read " + data);
            ex.printStackTrace();
        }
        
    }

    public int compareTo(Object o) {
         WordLocation wl = (WordLocation) o;
         String thisValue = this.getWord()+"@"+this.getFileName();
         String thatValue = wl.getWord()+"@"+wl.getFileName();
         return thisValue.compareTo(thatValue);
    }
    
    public static  HashSet<String> parseLocation(String data) throws IOException {
        
        String offsets[] = data.split(":");
        if(offsets.length == 0) {
             logger.warn("Location:Invalid Record read " + data);
             return null;           
        }
        
        HashSet<String> locations = new HashSet<String>(5,5);
        for(int i = 1; i< offsets.length; i++)
             locations.add(offsets[i].trim());
        return locations;

    }
    
    public static WordLocation parseWordLocation(String obj) {
        WordLocation wl = new WordLocation();
        String data[] = obj.split("\n");
        if( data.length != 2)
            return null;
        String tokens[] = data[0].split("[=]|[\\{]|[\\}]|[,]");
        try {
            if(data[0].indexOf("WordLocation") != -1 || tokens.length > 0) {
                for( int  i = 0; i<tokens.length; i++ ) {
                    if(tokens[i].indexOf("word")!=-1)
                     wl.setWord(tokens[i+1].trim());
                    else if(tokens[i].indexOf("fileName")!=-1)
                     wl.setFileName(tokens[i+1].trim());
                    else if(tokens[i].indexOf("location")!=-1)
                      wl.setLocations(parseLocation(tokens[i+1]));
                }
                wl.setBook(Book.parseBook(data[1]));
                wl.setCount(wl.getLocations().size());
            }
            else {
                 logger.warn("WordLocation:Invalid Record read " + data);
            }
        }
        catch(Exception ex)
        {
            logger.error("WordLocation: Error Record read " + data);
            ex.printStackTrace();
        }
        return wl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WordLocation other = (WordLocation) obj;
        if ((this.word == null) ? (other.word != null) :
                !this.word.equals(other.word)) {
            return false;
        }
        if ((this.fileName == null) ? (other.fileName != null) :
                !this.fileName.equals(other.fileName)) {
            return false;
        }
        if (this.book != other.book && (this.book == null ||
                !this.book.equals(other.book))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.word != null ? this.word.hashCode() : 0);
        hash = 17 * hash + (this.fileName != null ?
                this.fileName.hashCode() : 0);
        hash = 17 * hash + (this.book != null ? this.book.hashCode() : 0);
        hash = 17 * hash + (this.locations != null ?
                this.locations.hashCode() : 0);
        hash = 17 * hash + this.count;
        return hash;
    }
    
    public String toHTMLString() {
        String wl = "<b>"+word+"</b> found at locations <b>"+
                getStringifiedLocations(true)+"</b> in file <b>"+
                fileName+"</b> with number of occurrences <b>"+locations.size()
                +"</b><br/>"+book.toHTMLString();
        return wl;
    }
       
    
}
