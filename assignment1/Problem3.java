/* Student Name: Vincent Nguyen, Lab Section: 18695 */
package assignment1; 

import edu.stanford.nlp.tagger.maxent.MaxentTagger; 

public class Problem3 { 
    private static final String path = "models/english-left3words-distsim.tagger"; 
    public static String tagSentence(MaxentTagger tagger, String sentence) { 
        return tagger.tagString(sentence); 
    } 
    public static void main(String[] args) throws Exception { 
        MaxentTagger tagger = new MaxentTagger(path); 
        java.util.Scanner scanner = new java.util.Scanner(System.in); 
        String s = scanner.nextLine(); 
        System.out.println(tagSentence(tagger, s)); 
    } 
}