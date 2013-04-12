/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;
import java.util.HashSet;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.net.URLEncoder;

/**
 *
 * @author gk
 */
public class Regex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        String dummy = "Book{title=The Narrative of the Life of Frederick Douglass, author=Frederick Douglass, releaseDate=January 10, 2006 [EBook #23], language=English}}";
//        String tokens[] = dummy.split("[=]|[\\{]|[\\}]|[,]");
//        for( int i=0;i<tokens.length;i++)
//        {
//            System.out.println(Integer.toString(i)+ " "+tokens[i]);
//        }
        /*
         * 
         * upload files : Wordlocation
         * 
         */
        try {
        XmlBookReader br = new XmlBookReader("C:\\Users\\gk\\Documents"
                + "\\NetBeansProjects\\SearchEngine\\Data\\books.xml");
        XmlWordLocationReader wlr = new XmlWordLocationReader("C:\\Users"
                + "\\gk\\Documents"
                + "\\NetBeansProjects\\SearchEngine\\Data\\results.xml");
        XmlVersionReader vr = new XmlVersionReader("C:\\Users"
                + "\\gk\\Documents"
                + "\\NetBeansProjects\\SearchEngine\\Data\\version.xml");
        wlr.parse();
        br.parse();
        vr.parse();
        
        HashSet<WordLocation> wlset = wlr.getWordLocations();
        HashSet<Book> bset = br.getBooks();
        HashSet<WordLocation> skippedWords = new HashSet<WordLocation>(5,5);

        long count = 0;
        String result= " ";
        
//        for(Book b:bset)
//            System.out.println(b);
        
       String url = "http://guttenberginstantsearch.appspot.com/fileupload";
        String charset = "UTF-8";
        String update = "true";

// ...

     WordLocation warray[] = new WordLocation[3];
        
       for(WordLocation wl : wlset) {
           
           if(count%3 ==0)
               warray[0]=wl;
           if(count%3 == 1)
               warray[1]=wl;
           if(count%3 == 2) {
               warray[2]=wl;
           //System.out.println("Uploading :\n" +warray[0] + "\n"+ warray[1]);
           for(WordLocation w: warray)
            for(Book b: bset) {
             if(w.getBook().getTitle().equals(b.getTitle()))
                 w.setBook(b);
             }
            System.out.println("Uploading :\n" +warray[0] + "\n"+ warray[1] + "\n" 
                    + warray[2] + " update "+ update);
            String query = String.format("wl1=%s&wl2=%s&wl3=%s&update=%s", 
             URLEncoder.encode(warray[0].toString(), charset), 
             URLEncoder.encode(warray[1].toString(), charset),
             URLEncoder.encode(warray[2].toString(), charset),
             URLEncoder.encode(update, charset));
            if(url.length()+ query.length() > 2048){
                System.out.println("Breaking and Sending..");
                for(WordLocation w:warray) {
                    
                    query = String.format("wl1=%s&update=%s", 
                    URLEncoder.encode(w.toString(), charset),
                    URLEncoder.encode(update, charset));
                     if(url.length()+ query.length() > 2048) {
                         skippedWords.add(w);
                         continue;
                     }
                    result=HTTPRequestPoster.sendGetRequest(url,query);
                    System.out.println("Result: "+ result);
                }
                    
            }
            else{
                result=HTTPRequestPoster.sendGetRequest(url,query);
                System.out.println("Result: "+ result);
            }
               
           }
           if( count == 3){
               System.out.println("Verify logs on Server side for any error , \\n "
                       + "Please hit enter to proceed further?");
               Scanner in = new Scanner(System.in);
               in.nextLine();
           }
            count++;   
            float percentageCompleted = (float)count*100/(float)wlset.size();
            System.out.println("Percentage completed "+ percentageCompleted + 
                    " Result: "+ result);
       }
       for(WordLocation w: skippedWords)
           System.out.println("Skipped Words :\n"+w);
       
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
