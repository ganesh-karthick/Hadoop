/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;
import java.util.StringTokenizer;

/**
 *
 * @author gk
 */
public class Parser {
    private String title;
    private String author;
    private String releaseDate;
    private String language;
    private StringTokenizer st;
    private String parseWordStart;
    private  String parseWordStop; 
    private boolean toParse;

    public Parser() {
        this.title = "Title";
        this.author = "Author";
        this.releaseDate = "Release Date";
        this.language = "Language";
        this.parseWordStart  = "START OF THIS PROJECT GUTENBERG EBOOK";
        this.parseWordStop  = "END OF THIS PROJECT GUTENBERG EBOOK"; 
        this.toParse = false;
    }
    

    public Book getBook(String line,Book b) {

        String tokens[] = line.split(":");
        if(tokens.length != 2)
            return b;
        for(int i = 0; i < tokens.length - 1 ; i++) {
           if(line.indexOf(title)!=-1)
               b.setTitle(tokens[i+1].trim());
           else if(line.indexOf(author)!=-1)
               b.setAuthor(tokens[i+1].trim());
           else if(line.indexOf(releaseDate)!=-1)
               b.setReleaseDate(tokens[i+1].trim().trim());
           else if(line.indexOf(language)!=-1)
               b.setLanguage(tokens[i+1].trim().trim());          
        }
        return b; 
    }

    public String getParseWordStart() {
        return parseWordStart;
    }

    public void setParseWordStart(String parseWordStart) {
        this.parseWordStart = parseWordStart;
    }

    public String getParseWordStop() {
        return parseWordStop;
    }

    public void setParseWordStop(String parseWordStop) {
        this.parseWordStop = parseWordStop;
    }

    public boolean needtoParse(String line)
    {
        if(line.indexOf(parseWordStart)!=-1)
            toParse = true;
        else if(line.indexOf(parseWordStop)!=-1)
            toParse = false;
          
        return toParse;
    }

}
