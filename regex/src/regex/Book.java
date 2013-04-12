/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * @author gk
 */
public class Book {
    private String title;
    private String author;
    private String releaseDate;
    private String language;
    static Logger logger = Logger.getLogger(Book.class);

    public Book() {
    }
    
    public Book(String author, String Date, String encoding, String title) {
        this.author = author;
        this.releaseDate = Date;
        this.language = encoding;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void writeFields(DataOutput out) throws IOException {
         out.write((this.toString()+"\n").getBytes());
    }
    
    @Override
    public String toString() {
        return "Book{" + "title=" + title + ", author=" + author + ", "
                + "releaseDate=" + releaseDate + ", language=" + language + '}';
    }

       
    public void readFields(DataInput in) throws IOException {
         String data = new String(in.readLine());
         try {
             String tokens[] = data.split("[=]|[\\{]|[\\}]|[,]");
             if(data.indexOf("Book") != -1 || tokens.length > 0) {
               for( int i = 0; i < tokens.length ; i++) {
                   if(tokens[i].indexOf("title") != -1)
                       title = tokens[i+1];
                   else if(tokens[i].indexOf("author") != -1)
                       author = tokens[i+1];
                   else if(tokens[i].indexOf("releaseDate") != -1)
                       releaseDate=tokens[i+1];
                   else if(tokens[i].indexOf("language") != -1)
                       language=tokens[i+1];    
               }
             }
             else{
                     logger.warn("Book:Invalid Record read " + data);
             }
           }
         catch(Exception ex) {
             logger.error("Book:Error Record read " + data);
             ex.printStackTrace();
         }
             
     }
     public static Book parseBook(String data) throws IOException {
         
         Book book = new Book();
         try {
             String tokens[] = data.split("[=]|[\\{]|[\\}]|[,]");
             if(data.indexOf("Book") != -1 || tokens.length > 0) {
               for( int i = 0; i < tokens.length ; i++) {
                   if(tokens[i].indexOf("title") != -1)
                       book.setTitle(tokens[i+1]);
                   else if(tokens[i].indexOf("author") != -1)
                       book.setAuthor(tokens[i+1]);
                   else if(tokens[i].indexOf("releaseDate") != -1)
                      book.setReleaseDate(tokens[i+1]);
                   else if(tokens[i].indexOf("language") != -1)
                       book.setLanguage(tokens[i+1]);    
               }
             }
             else{
                     logger.warn("Book:Invalid Record read " + data);
             }
           }
         catch(Exception ex) {
             logger.error("Book:Error Record read " + data);
             ex.printStackTrace();
         }
         
         return book;
             
     }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Book other = (Book) obj;
        if ((this.title == null) ? (other.title != null) : 
                !this.title.equals(other.title)) {
            return false;
        }
        if ((this.author == null) ? (other.author != null) :
                !this.author.equals(other.author)) {
            return false;
        }
        if ((this.releaseDate == null) ? (other.releaseDate != null) :
                !this.releaseDate.equals(other.releaseDate)) {
            return false;
        }
        if ((this.language == null) ? (other.language != null) :
                !this.language.equals(other.language)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 79 * hash + (this.author != null ? this.author.hashCode() : 0);
        hash = 79 * hash + 
                (this.releaseDate != null ? this.releaseDate.hashCode() : 0);
        hash = 79 * hash + 
                (this.language != null ? this.language.hashCode() : 0);
        return hash;
    }
     
       
}
