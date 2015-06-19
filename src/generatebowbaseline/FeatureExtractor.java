package generatebowbaseline;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;


public class FeatureExtractor {
    
    public FeatureExtractor(String rawText) {
        this.rawText = rawText;
        this.bagOfWords = new Hashtable<String, Integer>();
        
        this.totalWords = 0;
        this.numberOfPoints = 0;
        this.numberOfCommas = 0;
        this.numberOfColons = 0;
        this.numberOfcharacterFloodingWords = 0;
    }
    
    public Features getFeatures() {
        Features f = new Features();
        
        f.numberOfPoints = numberOfPoints;
        f.nuberOfCommas = numberOfCommas;
        f.numberOfColons = numberOfColons;
        f.numberOfCharacterFloodingWords = numberOfcharacterFloodingWords;
        f.totalWords = totalWords;
        
        return f;
    }
    
    public Hashtable<String, Integer> getBagOfWords() {
        return bagOfWords;
    }
    
    public void processText()
    {
        try {
            
            // Parse the HTML in order to get the text
            Html2Text html2text = new Html2Text();
            Reader in = new StringReader(rawText);
            html2text.parse(in);
            text = html2text.getText();
            
            numberOfEmoticons = html2text.getNumberOfEmoticons();
            numberOfEmphasisTags = html2text.getNumberOfEmphasisTags();
            
            // TODO implement something in Html2Text to 
            // obtain emoticons, emphasis tags, etc.
            
            // Count possible interesting symbols
            // TODO !, ?, etc
            for (int i = 0; i < text.length(); ++i) {
                if (text.charAt(i) == ',')
                    numberOfCommas++;
                if (text.charAt(i) == '.')
                    numberOfPoints++;
                if (text.charAt(i) == ':')
                    numberOfColons++;
            }
            
            
            // Obtain and process all the words
            ArrayList<String> words = getTokens(text);

            for (int t = 0; t < words.size(); ++t) {
                String w = words.get(t);
                int iFreq = 0;
                if (bagOfWords.containsKey(w))
                    iFreq = bagOfWords.get(w);
                bagOfWords.put(w, ++iFreq);
                
                if(hasCharacterFlooding(w))
                    ++numberOfcharacterFloodingWords;
                
                ++totalWords;
            }
            
            
            
            
           
            
        
        } catch (IOException ex) {
            text = "";
        }
    }
    
    // Detects character flooding in a word (Only vowels)
    public static boolean hasCharacterFlooding(String text) {
        int consecutive = isVowel(text.charAt(0)) ? 1 : 0;
        
        for(int i = 1; i < text.length(); ++i) {
                if(isVowel(text.charAt(i)) && text.charAt(i) == text.charAt(i-1))
                    ++consecutive;
                else if(isVowel(text.charAt(i)))
                    consecutive = 1;
                else
                    consecutive = 0;
                
                if(consecutive > 2)
                    return true;
        }
        
        return false;
    }
    
    private static boolean isVowel(char c) {
        return "AEIOUaeiou".indexOf(c) != -1;
    }
    
    public static ArrayList<String> getTokens(String text) throws IOException {
        return getTokens(new SpanishAnalyzer(new String[0]), "myfield", text);
    }
    
    public static ArrayList<String> getTokens(Analyzer analyzer, String field, String text) throws IOException {
        return getTokens(analyzer.tokenStream(field,  new StringReader(text)));
    }

    public static ArrayList<String> getTokens(TokenStream stream) throws IOException {
        ArrayList<String> oTokens = new ArrayList<String>();
        TermAttribute term = stream.addAttribute(TermAttribute.class);
        while(stream.incrementToken()) {
            oTokens.add(term.term());
        }
        return oTokens;
    }
    
    public static ArrayList<String> getTokens(Analyzer analyzer, String text) throws IOException {
        return getTokens(analyzer.tokenStream("myfield", new StringReader(text)));
    }
    
    
    private String rawText;
    private String text;
    private Hashtable<String, Integer> bagOfWords;
    private int numberOfcharacterFloodingWords;
    int totalWords;
    int numberOfPoints;
    int numberOfCommas;
    int numberOfColons;
    int numberOfEmoticons;
    int numberOfEmphasisTags;
}
