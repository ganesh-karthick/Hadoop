/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;
import java.util.HashSet;

/**
 * Purpose pick words from list such that words that start with letter is
 * distributed among letter set.
 * @author gk
 */
public class SuggestionsCache {
    private int noOfUniqueWords ;
    private int wordsWithStartLetter;
    final int MAX_UNIQUE_WORDS = 512;
    final int ASCII_CODE_LIMIT = 256;
    final int MAX_WORDS_PER_START_LETTER = 15;
    private HashSet<String> uniqueWords;
    private int charArray[];

    public SuggestionsCache() {
        this.noOfUniqueWords = MAX_UNIQUE_WORDS;
        uniqueWords = new HashSet<String>(5,5);
        wordsWithStartLetter = MAX_WORDS_PER_START_LETTER;
        charArray = new int [ASCII_CODE_LIMIT];
    }
    
    

    public SuggestionsCache(int noOfUniqueWords, int wordsWithStartLetter) {
        if(noOfUniqueWords <= MAX_UNIQUE_WORDS && noOfUniqueWords > 0)
            this.noOfUniqueWords = noOfUniqueWords;
        else
            this.noOfUniqueWords = MAX_UNIQUE_WORDS;
        if(wordsWithStartLetter <= MAX_WORDS_PER_START_LETTER &&
                wordsWithStartLetter > 0)
            this.wordsWithStartLetter = wordsWithStartLetter;
        else
            this.wordsWithStartLetter = MAX_WORDS_PER_START_LETTER;
        uniqueWords = new HashSet<String>(5,5);
        charArray = new int [ASCII_CODE_LIMIT];
    }
    
    
    public int[] getCharArray() {
        return charArray;
    }

    public void setCharArray(int[] charArray) {
        this.charArray = charArray;
    }

    public int getNoOfUniqueWords() {
        return noOfUniqueWords;
    }

    public void setNoOfUniqueWords(int noOfUniqueWords) {
         if(noOfUniqueWords <= MAX_UNIQUE_WORDS && noOfUniqueWords > 0)
            this.noOfUniqueWords = noOfUniqueWords;
        else
            this.noOfUniqueWords = MAX_UNIQUE_WORDS;
    }

    public HashSet<String> getUniqueWords() {
        return uniqueWords;
    }

    public void setUniqueWords(HashSet<String> uniqueWords) {
        this.uniqueWords = uniqueWords;
    }
    
    public void addWord(String word) {
        if(word.length() != 0) {
            int chCode = (int) word.charAt(0);
            if(chCode > -1 && chCode < ASCII_CODE_LIMIT) {
             if(charArray[chCode] <= MAX_WORDS_PER_START_LETTER) {
                 uniqueWords.add(word);
                 charArray[chCode]++;
             }                
             else{
                 if(uniqueWords.size() < MAX_UNIQUE_WORDS){
                     uniqueWords.add(word);
                     charArray[chCode]++;
                 }       
             }           
            }               
         }
    }

    public int getWordsWithStartLetter() {
        return wordsWithStartLetter;
    }

    public void setWordsWithStartLetter(int wordsWithStartLetter) {
        if(wordsWithStartLetter <= MAX_WORDS_PER_START_LETTER &&
                wordsWithStartLetter > 0)
            this.wordsWithStartLetter = wordsWithStartLetter;
        else
            this.wordsWithStartLetter = MAX_WORDS_PER_START_LETTER;
    }
    
    
}
