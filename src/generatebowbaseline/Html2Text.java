package generatebowbaseline;

import java.io.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

/**
 *
 * @author Francisco Manuel Rangel Pardo / Corex, Building Knowledge Solutions
 */
public class Html2Text extends HTMLEditorKit.ParserCallback
{
   StringBuffer s;
   int hasEmoticons;

     public Html2Text() {
         hasEmoticons = 0;
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
    
     /* No de momento
    @Override
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        System.out.println(t.toString());
    }

    @Override
    public void handleEndTag(HTML.Tag t, int pos) {
        System.out.println(t.toString());
    }
     */

    @Override
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        // Si la etiqueta rompe el flujo de texto como br anyadimos espacio
        // para evitar juntar dos palabras diferentes
        if(t.breaksFlow())
            s.append(' ');
        
        // Si es una imagen nos quedamos el atributo alt
        if(t == HTML.Tag.IMG) {
            hasEmoticons = 1;
            //System.out.println("Imagen "  + a.getAttribute(HTML.Attribute.CLASS) + " " + a.getAttribute(HTML.Attribute.SRC));
            //System.out.println(s.toString());
        }
    }

     public String getText() {
       return s.toString();
     }
     
    
}
