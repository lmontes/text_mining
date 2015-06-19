package generatebowbaseline;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author kico
 */
public class Weka {
    
    public static String HeaderToWeka(ArrayList<String> BOW, int iNTerms, String classValue)
    {
        String sHeader =  "@relation 'BOW'\n" ;

        for (int i=0;i<BOW.size();i++) {
            String sTerm = BOW.get(i);
            sHeader += "@attribute 'term-" + sTerm.replaceAll("'", "quote") + "' real\n";
            
            if (i>=iNTerms) {
                break;
            }
        }
        
        sHeader += "@attribute 'ratio-commas' real\n" +
                "@attribute 'ratio-points' real\n" +
                "@attribute 'ratio-colons' real\n" +
                "@attribute 'ratio-character-flooding' real\n" +
                "@attribute 'ratio-diff-word' real\n" + 
                "@attribute 'ratio-emoticon' real\n" +  
                "@attribute 'ratio-emphasis' real\n";
        
        sHeader += "@attribute 'class' {" + classValue + "}\n" +
        "@data\n";
        return sHeader;
    }
    
    public static String FeaturesToWeka(ArrayList<String> BOW, Hashtable<String, Integer>oDoc, Features oFeatures, int iN, String classValue)    {
        String weka = "";
        int iTotal = oDoc.size();
        for (int i=0;i<BOW.size();i++) {
            String sTerm = BOW.get(i);
            double freq = 0;
            if (oDoc.containsKey(sTerm)) {
                freq = (double)((double)oDoc.get(sTerm) / (double)iTotal);
            }
            
            weka += freq + ",";
            
            if (i>=iN) {
                break;
            }
        }
        
        weka += (double)((double)oFeatures.nuberOfCommas / (double)oFeatures.totalWords) + "," + 
                (double)((double)oFeatures.numberOfPoints / (double)oFeatures.totalWords) + "," + 
                (double)((double)oFeatures.numberOfColons / (double)oFeatures.totalWords) + "," +
                (double)((double)oFeatures.numberOfCharacterFloodingWords / (double)oFeatures.totalWords) + "," + 
                (double)((double)iTotal / (double)oFeatures.totalWords) + "," +
                (double)((double)oFeatures.numberOfEmoticons / (double)oFeatures.totalWords)+ "," +
                (double)((double)oFeatures.numberOfEmphasisTags / (double)oFeatures.totalWords)+ ",";
        
        weka +=  classValue + "\n";
        
        return weka;
    }
}
