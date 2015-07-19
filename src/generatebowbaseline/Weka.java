package generatebowbaseline;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author kico
 */
public class Weka {

    public static String HeaderToWeka(ArrayList<String> BOW, int nTerms, String classValue) {
        String sHeader = "@relation 'BOW'\n";

        for (int i = 0; i < BOW.size(); i++) {
            String sTerm = BOW.get(i);
            sHeader += "@attribute 'term-" + sTerm.replaceAll("'", "quote") + "' real\n";

            if (i >= nTerms) {
                break;
            }
        }
         
        sHeader += "@attribute 'ratio-commas' real\n"
                + "@attribute 'ratio-points' real\n"
                + "@attribute 'ratio-character-flooding' real\n"
                + "@attribute 'ratio-diff-word' real\n"
                + "@attribute 'ratio-emoticon' real\n"
                + "@attribute 'ratio-emphasis' real\n"
                + "@attribute 'ratio-laugh' real\n"
                + "@attribute 'ratio-numbers' real\n"
                + "@attribute 'ratio-accents' real\n"
                + "@attribute 'exclamation-flooding' real\n"
                + "@attribute 'email' real\n"
                + "@attribute 'uses-at' real\n"
                + "@attribute 'url' real\n";

        sHeader += "@attribute 'class' {" + classValue + "}\n"
                + "@data\n";
        return sHeader;
    }

    public static String FeaturesToWeka(ArrayList<String> BOW, Hashtable<String, Integer> oDoc, Features oFeatures, int nTerms, String classValue) {
        StringBuilder builder = new StringBuilder();
    
        for (int i = 0; i < BOW.size(); i++) {
            String sTerm = BOW.get(i);
            double freq = 0;
            if (oDoc.containsKey(sTerm))
                freq = 1;

            builder.append(freq);
            builder.append(',');

            if (i >= nTerms) {
                break;
            }
        }
        
        if (oFeatures.totalWords > 0) {
            double totalWords = (double) oFeatures.totalWords;
            builder.append((double) ((double) oFeatures.nuberOfCommas / totalWords));
            builder.append(',');
            builder.append((double) ((double) oFeatures.numberOfPoints / totalWords));
            builder.append(',');
            builder.append((double) ((double) oFeatures.numberOfCharacterFloodingWords / totalWords));
            builder.append(',');
            builder.append((double) ((double) oFeatures.numberOfDifferentWords / totalWords));
            builder.append(',');
            builder.append((double) ((double) oFeatures.numberOfEmoticons / totalWords));
            builder.append(',');
            builder.append((double) ((double) oFeatures.numberOfEmphasisTags / totalWords));
            builder.append(',');
            builder.append((double) ((double) oFeatures.numberOfLaughs / totalWords));
            builder.append(',');
            builder.append((double) ((double) oFeatures.numberOfNumbers / totalWords));
            builder.append(',');
            builder.append((double) ((double) oFeatures.numberOfAccents / oFeatures.numberOfDifferentWords));
            builder.append(',');
        } else {
            builder.append("0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,");
        }

        builder.append((double) oFeatures.hasExclamationFlooding);
        builder.append(',');
        
        builder.append((double) oFeatures.hasEmail);
        builder.append(',');
        
        builder.append((double) oFeatures.usesAtInWords);
        builder.append(',');
        
        builder.append((double) oFeatures.hasUrl);
        builder.append(',');

        builder.append(classValue);
        builder.append('\n');

        return builder.toString();
    }
}
