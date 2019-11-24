package com.example.gabriel.needlit;

import java.util.ArrayList;

/**
 * Class to process select specific elements from html format
 * Created by gabrielbaki on 12/31/15.
 */
public class HtmlPrccssr {

    //note: remove square brackets from source

    /**
     * get text from html paragraphs
     * @param unprocessedText
     * @return
     */
    public static StringBuilder htmlParagraph(StringBuilder unprocessedText){
        StringBuilder processedText = new StringBuilder();
        Boolean append = false;
        Boolean innerAppend = false;
        int x = 0;
        while(x < unprocessedText.length()) {
            if (unprocessedText.charAt(x) == '<' &&
                    unprocessedText.charAt(x+1) == 'p' &&
                    unprocessedText.charAt(x+2) == '>') {
                append = true;
                x = x + 2;
            }
            else if (unprocessedText.charAt(x) == '<' &&
                    unprocessedText.charAt(x+1) == '/' &&
                    unprocessedText.charAt(x+2) == 'p' &&
                    unprocessedText.charAt(x+3) == '>') {
                append = false;
                x = x +3;
            }
            else {
                if (append == true) {
                    if (unprocessedText.charAt(x) == '<'
                        //|| unprocessedText.charAt(x) == '('
                            //|| unprocessedText.charAt(x) == '['
                            ) {
                        innerAppend = false;
                    }
                    else if (unprocessedText.charAt(x) == '>'
                                //|| unprocessedText.charAt(x) == ')'
                                   // || unprocessedText.charAt(x) == ']'
                                   ){
                        innerAppend = true;
                    }
                    else {
                        if (innerAppend == true) {
                            processedText.append(unprocessedText.charAt(x));
                        }
                    }
                }
            }
            x++;
        }
        return processedText;
    }

    /**
     * get links from html text
     * @param unprocessedText
     * @return
     */
    public static StringBuilder htmlTextLinks(StringBuilder unprocessedText){
        StringBuilder processedText = new StringBuilder();
        Boolean append = false;
        Boolean innerAppend = false;
        int x = 0;
        while(x < unprocessedText.length()) {
            if (unprocessedText.charAt(x) == '<' &&
                    unprocessedText.charAt(x+1) == 'a' &&
                    unprocessedText.charAt(x+2) == ' ' &&
                    unprocessedText.charAt(x+3) == 'h' &&
                    unprocessedText.charAt(x+4) == 'r' &&
                    unprocessedText.charAt(x+5) == 'e' &&
                    unprocessedText.charAt(x+6) == 'f' &&
                    unprocessedText.charAt(x+7) == '=' &&
                    unprocessedText.charAt(x+8) == '"' ) {
                append = true;
                x = x + 8;
            }
            else if (unprocessedText.charAt(x) == '"') {
                append = false;
                //x = x +3;
            }

            if (append == true) {
                processedText.append(unprocessedText.charAt(x));
            }

            x++;
        }
        return processedText;
    }

    /**
     * collect sentences from html
     * @param unprocessedText
     * @return
     */
    public static String collectSentences(StringBuilder unprocessedText) {
        ArrayList<String> sentences;
        sentences = new ArrayList<String>();
        StringBuilder tempText = new StringBuilder();
        int x = 0;
        while(x < unprocessedText.length()){
            tempText.append(unprocessedText.charAt(x));
            if (unprocessedText.charAt(x) == '.'){
                sentences.add(tempText.toString());
                tempText.setLength(0);
                tempText.trimToSize();
            }
            x++;
        }
        return sentences.toString();
    }
}
