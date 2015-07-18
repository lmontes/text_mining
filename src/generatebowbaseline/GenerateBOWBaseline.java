package generatebowbaseline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author kicorangel
 */
public class GenerateBOWBaseline {

    // Directorio donde se encuentran todos los ficheros XML
    private static String PATH = "/home/luis/Text_mining/pan13-author-profiling-test-corpus2-2013-04-29/es";

    // Fichero que para cada autor indica el genero y rango de edad
    private static String TRUTH = "/home/luis/Text_mining/pan13-author-profiling-test-corpus2-2013-04-29/truth-es.txt";

    // Ficheros que generamos nosotros, Bag of words y datos para weka
    private static String BOW = "/home/luis/Text_mining/bow-es.txt";
    private static String OUTPUT = "/home/luis/Text_mining/laugh-total-es-{task}.arff";

    private static int NTERMS = 1000;

    public static void main(String[] args) {
        FileWriter fw = null;

        try {
            Hashtable<String, TruthInfo> oTruth = ReadTruth(TRUTH);
            ArrayList<String> oBOW = ReadBOW(PATH, BOW, oTruth);
            GenerateBaseline(PATH, oBOW, oTruth, OUTPUT.replace("{task}", "gender"), "MALE, FEMALE");
            GenerateBaseline(PATH, oBOW, oTruth, OUTPUT.replace("{task}", "age"), "10S, 20S, 30S");

        } catch (Exception ex) {

        }
    }

    private static void GenerateBaseline(String path, ArrayList<String> aBOW, /*ArrayList<Double> aIdf,*/ Hashtable<String, TruthInfo> oTruth, String outputFile, String classValues) {
        FileWriter fw = null;

        try {
            fw = new FileWriter(outputFile);
            fw.write(Weka.HeaderToWeka(aBOW, NTERMS, classValues));
            //fw.flush();

            File directory = new File(path);
            String[] files = directory.list();
            for (int iFile = 0; iFile < files.length; iFile++) {
                System.out.println("--> Generating " + (iFile + 1) + "/" + files.length);
                try {
                    String sFileName = files[iFile];

                    File fXmlFile = new File(path + "/" + sFileName);
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(fXmlFile);
                    NodeList documents = doc.getDocumentElement().getElementsByTagName("conversation");
                    String[] fileInfo = sFileName.split("_");
                    String sAuthor = fileInfo[0];
                    StringBuilder sAuthorContent = new StringBuilder();

                    // Append all the texts of the same author in one string
                    for (int i = 0; i < documents.getLength(); i++) {
                        try {
                            Element element = (Element) documents.item(i);
                            sAuthorContent.append(element.getTextContent());
                            sAuthorContent.append(' ');

                        } catch (Exception ex) {
                            System.out.println("ERROR: " + ex.toString());
                            String s = ex.toString();
                        }
                    }

                    FeatureExtractor ext = new FeatureExtractor(sAuthorContent.toString());
                    ext.processText();

                    if (oTruth.containsKey(sAuthor)) {
                        TruthInfo truth = oTruth.get(sAuthor);
                        String sGender = truth.Gender.toUpperCase();
                        String sAge = truth.Age.toUpperCase();

                        if (classValues.contains("MALE")) {
                            fw.write(Weka.FeaturesToWeka(aBOW, ext.getBagOfWords(), ext.getFeatures(), NTERMS, sGender));
                        } else {
                            fw.write(Weka.FeaturesToWeka(aBOW, ext.getBagOfWords(), ext.getFeatures(), NTERMS, sAge));
                        }
                        //fw.flush();
                    }

                } catch (Exception ex) {
                    System.out.println("ERROR: " + ex.toString());
                }
            }
        } catch (Exception ex) {

        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (Exception k) {
                }
            }
        }
    }

    private static ArrayList<String> ReadBOW(String corpusPath, String bowPath, Hashtable<String, TruthInfo> oTruth) {
        Hashtable<String, Integer> oBOW = new Hashtable<String, Integer>();
        ArrayList<String> auxBOW = new ArrayList<String>();
        ArrayList<String> aBOW = new ArrayList<String>();

        FileReader fr = null;
        BufferedReader bf = null;

        if(!(new File(bowPath).exists()))
            createBOW(corpusPath, bowPath, oTruth);
        
        try {
            fr = new FileReader(bowPath);
            bf = new BufferedReader(fr);
            String sCadena = "";

            while ((sCadena = bf.readLine()) != null) {
                String[] data = sCadena.split(":::");
                if (data.length == 3) {
                    String sTerm = data[0];
                    double importance = Double.parseDouble(data[2]);
                    auxBOW.add(sTerm);
                }
            }
            
            // Add the first and the last 200 to the BOW
            int size = auxBOW.size();
            for(int i = 0; i < 200; ++i) {
                aBOW.add(auxBOW.get(i));
                aBOW.add(auxBOW.get(size - i - 1));
            }
            
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (Exception k) {
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception k) {
                }
            }
        }

        return aBOW;
    }
    
    
    private static void createBOW(String corpusPath, String bowPath, Hashtable<String, TruthInfo> oTruth) {
        Hashtable<String, Integer> oBOW = new Hashtable<String, Integer>();
        // The part of the total that represents the number of words that are from men
        Hashtable<String, Integer> menWords = new Hashtable<String, Integer>();
        Hashtable<String, Double> importance = new Hashtable<String, Double>();
        
        File directory = new File(corpusPath);
        String[] files = directory.list();

        for (int iFile = 0; iFile < files.length; iFile++) {
            System.out.println("--> Preprocessing " + (iFile + 1) + "/" + files.length);

            try {
                File fXmlFile = new File(corpusPath + "/" + files[iFile]);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                NodeList documents = doc.getDocumentElement().getElementsByTagName("conversation");

                String[] fileInfo = files[iFile].split("_");
                String sAuthor = fileInfo[0];
                boolean isMale = false;
                if(oTruth.containsKey(sAuthor)) {
                    TruthInfo truth = oTruth.get(sAuthor);
                    if(truth.Gender.equals("male"))
                        isMale = true;
                }

                double iWords = 0;
                double iDocs = documents.getLength();
                for (int i = 0; i < iDocs; i++) {
                    Element element = (Element) documents.item(i);
                    String sHtml = element.getTextContent();
                    String sContent = GetText(sHtml);
                    ArrayList<String> aTerms = getTokens(sContent);
                    for (int t = 0; t < aTerms.size(); t++) {
                        String sTerm = aTerms.get(t);
                        int freq = 0;
                        if(oBOW.containsKey(sTerm))
                            freq = oBOW.get(sTerm);
                        oBOW.put(sTerm, ++freq);

                        if(isMale) {
                            int freqMen = 0;
                            if(menWords.containsKey(sTerm))
                                freqMen = menWords.get(sTerm);
                            menWords.put(sTerm, ++freqMen);
                        }
                    }
                }
            } catch (Exception ex) {

            }
        }

        // Compute the importance of each word
        Iterator it_ = oBOW.keySet().iterator();
        while(it_.hasNext()) {
            String sTerm = (String) it_.next();
            int totalFreq = oBOW.get(sTerm);

            // Filter the words that appear less than 100 times
            if(totalFreq >= 200) {
                int menFreq = 0;
                if(menWords.containsKey(sTerm))
                    menFreq = menWords.get(sTerm);

                double wordImportance = ((double)menFreq) / ((double)totalFreq);
                importance.put(sTerm, wordImportance);
            }
        }
        
        // Sort the word by importance
        ValueComparator bvc = new ValueComparator(importance);
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
        sorted_map.putAll(importance);
        
        FileWriter fw = null;
        try {
            fw = new FileWriter(bowPath);
            for (Iterator it = sorted_map.keySet().iterator(); it.hasNext();) {
                String sTerm = (String) it.next();
                int iFreq = oBOW.get(sTerm);
                double wordImportance = importance.get(sTerm);
           
                fw.write(sTerm + ":::" + iFreq + ":::" + wordImportance + "\n");
                //fw.flush();
            }
        } catch (Exception ex) {

        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (Exception k) {
                }
            }
        }
    }
    
    
    private static Hashtable<String, TruthInfo> ReadTruth(String path) {
        Hashtable<String, TruthInfo> oTruth = new Hashtable<String, TruthInfo>();

        FileReader fr = null;
        BufferedReader bf = null;

        try {
            fr = new FileReader(path);
            bf = new BufferedReader(fr);
            String sCadena = "";

            while ((sCadena = bf.readLine()) != null) {
                String[] data = sCadena.split(":::");
                if (data.length == 3) {
                    String sAuthorId = data[0];
                    if (!oTruth.containsKey(sAuthorId)) {
                        TruthInfo info = new TruthInfo();
                        info.Gender = data[1];
                        info.Age = data[2];
                        oTruth.put(sAuthorId, info);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (Exception k) {
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception k) {
                }
            }
        }

        return oTruth;
    }

    public static ArrayList<String> getTokens(String text) throws IOException {
        return getTokens(new SpanishAnalyzer(new String[0]), "myfield", text);
    }

    public static ArrayList<String> getTokens(Analyzer analyzer, String field, String text) throws IOException {
        return getTokens(analyzer.tokenStream(field, new StringReader(text)));
    }

    public static ArrayList<String> getTokens(TokenStream stream) throws IOException {
        ArrayList<String> oTokens = new ArrayList<String>();
        TermAttribute term = stream.addAttribute(TermAttribute.class);
        while (stream.incrementToken()) {
            oTokens.add(term.term());
        }
        return oTokens;
    }

    public static ArrayList<String> getTokens(Analyzer analyzer, String text) throws IOException {
        return getTokens(analyzer.tokenStream("myfield", new StringReader(text)));
    }

    public static String GetText(String html) {
        try {
            Html2Text html2text = new Html2Text();
            Reader in = new StringReader(html);
            html2text.parse(in);
            return html2text.getText();
        } catch (IOException ex) {
            return html;
        }
    }
}
