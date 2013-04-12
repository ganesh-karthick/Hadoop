/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;


import org.apache.log4j.Logger; 

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 *
 * @author gk
 */
 class XmlRecordWriter<K,V> extends RecordWriter<K,V> {
    private static final String wordStartTag = "<word>";
    private static final String wordStopTag = "</word>";
    private static final String fileNameStartTag = "<filename>";
    private static final String fileNameStopTag = "</filename>";
    private static final String locationStartTag = "<locations>";
    private static final String locationStopTag = "</locations>";
    private static final String countStartTag = "<count>";
    private static final String countStopTag = "</count>";
    private static final String bookStartTag = "<book>";
    private static final String bookStopTag = "</book>";
    private static final String titleStartTag = "<title>";
    private static final String titleStopTag = "</title>";
    private static final String authorStartTag = "<author>";
    private static final String authorStopTag = "</author>";
    private static final String releaseDatestartTag = "<releasedate>";
    private static final String releaseDateStopTag = "</releasedate>";
    private static final String languageStartTag = "<language>";
    private static final String languageStopTag = "</language>";
    String fileNamePath;
    static Logger logger=Logger.getLogger(XmlRecordWriter.class);
    boolean isSplitOutput;
    private DataOutputStream out;
    private DataOutputStream outb;
    static HashSet<Book> books = new HashSet<Book>(5,5);
    

    public XmlRecordWriter(DataOutputStream out,
            String fileNamePath, boolean isSplitOutput) throws IOException {
      this.out = out;
      this.isSplitOutput=isSplitOutput;
      this.fileNamePath = fileNamePath;
      out.writeBytes("<results>\n");
    }

    /**
     * Write the object to the byte stream, handling Text as a special case.
     *
     * @param o
     *          the object to print
     * @throws IOException
     *           if the write throws, we pass it on
     * OUTPUT SCHEMA - Single Output
         <results>
        <key> <--! Pattern: worddetails>
        <word> </word>
        <filename> </filename>
        <count>    </count>
        <locations> </locations>
        <book>
        <title> </title>
        <author> </author>
        <releaseDate> </releaseDate>
        <language> </language>
        </book>
        </key>
        </results>
     * 
     * OUTPUT SCHEMA - Split output
     * Hadoop file - list of words with title as link to book list
     * 
     *  <results>
        <key> <--! Pattern: worddetails>
        <word> </word>
        <filename> </filename>
        <count>    </count>
        <locations> </locations>
        <title> </title>
     * 
     * </key>
     * 
     * Book file ( in OS file system ) - list of files
     * <book>
        <title> </title>
        <author> </author>
        <releaseDate> </releaseDate>
        <language> </language>
        </book>
     * 
     * 
     * 
     */
    private void writeObject(Object o, boolean isKey) throws IOException {
      
      Text to; 
      
      if (o instanceof Text && isKey) {
        to = (Text) o;
        out.write(to.getBytes(), 0, to.getLength());
      } else {
            WordLocation wl = WordLocation.parseWordLocation(o.toString());
            
            out.writeBytes(wordStartTag);
            out.writeBytes(wl.getWord());
            out.writeBytes(wordStopTag);

            out.writeBytes("\n");

            out.writeBytes(fileNameStartTag);
            out.writeBytes(wl.getFileName());
            out.writeBytes(fileNameStopTag);

            out.writeBytes("\n");
            
            out.writeBytes(countStartTag);
            out.writeBytes(Integer.toString(wl.getSize()));
            out.writeBytes(countStopTag);

            out.writeBytes("\n");
            
            //set to true to get all locations
            out.writeBytes(locationStartTag);
            out.writeBytes(wl.getStringifiedLocations(true));
            out.writeBytes(locationStopTag);

            out.writeBytes("\n");
            
            out.writeBytes(titleStartTag);
            out.writeBytes(wl.getBook().getTitle());
            out.writeBytes(titleStopTag);

            out.writeBytes("\n");
            
             if(isSplitOutput) 
                addBook(wl.getBook());    
             else     
                writeBook(wl.getBook(), out, false);                
      }
    }
    
    private void writeBook(Book book, DataOutputStream outb, boolean withTitle) 
            throws IOException {
        
        outb.writeBytes(bookStartTag);
        outb.writeBytes("\n");

        if(withTitle) {

            outb.writeBytes(titleStartTag);
            outb.writeBytes(book.getTitle());
            outb.writeBytes(titleStopTag);

            outb.writeBytes("\n");
        }

        outb.writeBytes(authorStartTag);
        outb.writeBytes(book.getAuthor());
        outb.writeBytes(authorStopTag);

        outb.writeBytes("\n");

        outb.writeBytes(releaseDatestartTag);
        outb.writeBytes(book.getReleaseDate());
        outb.writeBytes(releaseDateStopTag);

        outb.writeBytes("\n");

        outb.writeBytes(languageStartTag);
        outb.writeBytes(book.getLanguage());
        outb.writeBytes(languageStopTag);

        outb.writeBytes("\n");

        outb.writeBytes(bookStopTag);
        
        outb.writeBytes("\n");
        
    }
    
    private boolean addBook(Book book){
        boolean isDuplicate = false;
        for(Book b : books)
            if(b.equals(book))
                isDuplicate = true;
        if(!isDuplicate)
            books.add(book);
        return isDuplicate;
    }


    private void writeKey(Object o, boolean closing) throws IOException {
      out.writeBytes("<");
      if (closing) {
        out.writeBytes("/");
      }
      writeObject(new Text("worddetails") , true);
      out.writeBytes(">");
      out.writeBytes("\n");
    }

    public synchronized void write(K key, V value) throws IOException {

      boolean nullKey = key == null || key instanceof NullWritable;
      boolean nullValue = value == null || value instanceof NullWritable;

      if (nullKey && nullValue) {
        return;
      }

      Object keyObj = key;


      if (nullKey) {
          keyObj = "value";
      }

      writeKey(keyObj, false);

      if (!nullValue) {
        writeObject(value, false);
      }

      writeKey(keyObj, true);
    }

    public synchronized void close(TaskAttemptContext context) throws IOException {
      Path fileb = new Path(fileNamePath);
      FileSystem fsb = fileb.getFileSystem(context.getConfiguration());
      FSDataOutputStream fileOutb = fsb.create(fileb, true);
      outb = new DataOutputStream(fileOutb);
      try {
        out.writeBytes("</results>\n");
        outb.writeBytes("<results>\n");
        for(Book b : books)
         writeBook(b,outb,true);
        outb.writeBytes("</results>\n");
        logger.info(" XMLFile for books present at " + fileNamePath );
        System.out.println(" XMLFile for books present at " + fileNamePath);
      } finally {
        // even if writeBytes() fails, make sure we close the stream
        out.close();
        outb.close();
      }
    }
  }
