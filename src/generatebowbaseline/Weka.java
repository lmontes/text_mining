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
                //"@attribute 'ratio-emoticon' real\n" +  
                "@attribute 'ratio-emphasis' real\n" +
                "@attribute 'ratio-accents' real\n" +
                "@attribute 'exclamation-flooding' real\n";
        
        sHeader += "@attribute 'class' {" + classValue + "}\n" +
        "@data\n";
        return sHeader;
    }
    
    public static String FeaturesToWeka(ArrayList<String> BOW, Hashtable<String, Integer>oDoc, Features oFeatures, int iN, String classValue)    {
        StringBuilder builder = new StringBuilder();
        int iTotal = oDoc.size();
        for (int i=0;i<BOW.size();i++) {
            String sTerm = BOW.get(i);
            double freq = 0;
            if (oDoc.containsKey(sTerm)) {
                freq = (double)((double)oDoc.get(sTerm) / (double)iTotal);
            }
 
            builder.append(freq);
            builder.append(',');
            
            if (i>=iN) {
                break;
            }
        }
        
        builder.append((double)((double)oFeatures.nuberOfCommas / (double)oFeatures.totalWords));
        builder.append(',');
        builder.append((double)((double)oFeatures.numberOfPoints / (double)oFeatures.totalWords));
        builder.append(',');
        builder.append((double)((double)oFeatures.numberOfColons / (double)oFeatures.totalWords));
        builder.append(',');
        builder.append((double)((double)oFeatures.numberOfCharacterFloodingWords / (double)oFeatures.totalWords));
        builder.append(',');
        builder.append((double)((double)iTotal / (double)oFeatures.totalWords));
        builder.append(',');
        builder.append((double)((double)oFeatures.numberOfEmphasisTags / (double)oFeatures.totalWords));
        builder.append(',');
        builder.append((double)((double)oFeatures.numberOfAccents / (double)iTotal));
        builder.append(',');
        builder.append((double)oFeatures.hasExclamationFlooding);
        builder.append(',');
        
        builder.append(classValue);
        builder.append('\n');
        
        return builder.toString();
    }
}
