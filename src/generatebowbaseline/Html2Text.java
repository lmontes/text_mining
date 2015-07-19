package generatebowbaseline;

import java.io.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

/**
 *
 * @author Francisco Manuel Rangel Pardo / Corex, Building Knowledge Solutions
 */
public class Html2Text extends HTMLEditorKit.ParserCallback {

    private StringBuffer s;
    private int numberOfEmoticons;
    private int numberOfEmphasisTags;

    public Html2Text() {
        numberOfEmoticons = 0;
        numberOfEmphasisTags = 0;
    }

    public void parse(Reader in) throws IOException {
        s = new StringBuffer();
        ParserDelegator delegator = new ParserDelegator();
        // the third parameter is TRUE to ignore charset directive
        delegator.parse(in, this, Boolean.TRUE);
    }

    @Override
    public void handleText(char[] text, int pos) {
        s.append(text);
    }

    @Override
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        if (t == HTML.Tag.EM || t == HTML.Tag.STRONG || t == HTML.Tag.B || t == HTML.Tag.U || t == HTML.Tag.I) {
            ++numberOfEmphasisTags;
        }
    }

    @Override
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        // Si la etiqueta rompe el flujo de texto como br anyadimos espacio
        // para evitar juntar dos palabras diferentes
        if (t.breaksFlow()) {
            s.append(' ');
        }

        // Si es una imagen nos quedamos el atributo alt
        if (t == HTML.Tag.IMG/* && a.containsAttribute(HTML.Attribute.CLASS, "smiley")*/) {
            ++numberOfEmoticons;
        }
    }

    public String getText() {
        return s.toString();
    }

    public int getNumberOfEmoticons() {
        return numberOfEmoticons;
    }

    public int getNumberOfEmphasisTags() {
        return numberOfEmphasisTags;
    }
}
