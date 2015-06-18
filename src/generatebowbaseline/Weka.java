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
        
        sHeader += "@attribute 'ncommas' real\n" +
                "@attribute 'npoints' real\n" +
                "@attribute 'ncolons' real\n" +
                "@attribute 'character-flooding' real\n" +
                "@attribute 'diff-word-ratio' real\n";
        
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
        
        weka += (double)((double)oFeatures.nuberOfCommas / (double)iTotal) + "," + 
                (double)((double)oFeatures.numberOfPoints / (double)iTotal) + "," + 
                (double)((double)oFeatures.numberOfColons / (double)iTotal) + "," +
                (double)oFeatures.numberOfCharacterFloodingWords + "," + 
                oFeatures.differentWordsRatio + ",";
        
        weka +=  classValue + "\n";
        
        return weka;
    }
}
