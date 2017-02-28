/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author apple
 */
public class ParseWords {
    private StringBuffer originalWords;
    private boolean ifGeneral;
    private Map<String, Boolean> libraryTypeCondition;
    private String copyrightStopwordList;
    private String customizedPackageList;
    private Map<String, Integer> documentWordsCountList;
    private File extractedFile;

    public ParseWords(StringBuffer originalWords, boolean ifGeneral, Map<String, Boolean> libraryTypeCondition, String copyrightInfoContent, String customizedPackageList, Map<String, Integer> documentWordsCountList, File extractedFile) {
        this.originalWords = originalWords;
        this.ifGeneral = ifGeneral;
        this.libraryTypeCondition = libraryTypeCondition;
        this.copyrightStopwordList = copyrightInfoContent;
        this.customizedPackageList =customizedPackageList;
        this.documentWordsCountList = documentWordsCountList;
        this.extractedFile = extractedFile;
    }

    public StringBuffer parseAllWords() {
        StringBuffer outputWords = new StringBuffer();

        String[] allWords = originalWords.toString().split(" |\"|\\(|\\)|\\[|\\]|\\.|&|:|;|\r\n|\\\\r\\\\n|\n|\\\\n|\t|\\\\t|,|-|_|//|/|\\*|$|@|\\{|\\}|'");

        int wordCount = 0;
        for (String word : allWords) {
            if (!word.equals("")) {
                String[] splitWords = splitCamelWords(word);

                /*若word被拆分，将拆分后的词加入*/
                for (String aSplitWord : splitWords) {
                    String parsedWords = removeStopWords(aSplitWord);
                    if (parsedWords != null)
                        parsedWords = removeClassLibrary(parsedWords.toLowerCase());
                    if (parsedWords != null)
                        parsedWords = removeCustomizedPackageInfo(parsedWords.toLowerCase());
                    if (parsedWords != null)
                        parsedWords = removeCopyrightInfo(parsedWords.toLowerCase());
                    if (parsedWords != null) {
                        outputWords.append(parsedWords);
                        outputWords.append(" ");
                        wordCount++;
                    }
                }
            }
        }

        documentWordsCountList.put(extractedFile.getName(), wordCount);
        return outputWords;
    }

    private String[] splitCamelWords(String word) {
        word = word.replace("XML", "Xml");
        word = word.replace("DOM", "Dom");
        word = word.replace("JHotDraw", "Jhotdraw");
        word = word.replace("ID", "Id");

        String regEx = "[A-Z]";
        Pattern p1 = Pattern.compile(regEx);
        Matcher m1 = p1.matcher(word);

        boolean startWithUpper = false;
        startWithUpper = Pattern.matches("[A-Z].*", word);

        String[] words = p1.split(word);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            list.add(words[i]);
        }

        int count = 0;
        while (m1.find()) {
            if (count + 1 < words.length) {
                list.set(count + 1, m1.group() + list.get(count + 1));
                ++count;
            } else {
                list.add(m1.group());
            }
        }

        if (startWithUpper && words.length != 0) {
            list.remove(0);
        }

        for (int i = 0; i < list.size(); ++i) {
            list.set(i, list.get(i).toLowerCase());
        }

        String[] result = list.toArray(new String[1]);
        return result;
    }

    private String removeStopWords(String word) {
        String stopList = "abstract array arg args assert boolean br break byte catch case char class code continue " +
                "default dd ddouble dl do don double dt else enum error exception exist exists extends false file final " +
                "finally float for gt id if implementation implemented implements import instanceof int integer interface " +
                "interfaces invoke invokes java lead li long main method methodname methods native nbsp new null object " +
                "objects overrides package packages param parameters precison println private protected public quot return " +
                "returned returns short static string strictfp super switch synchronized system this throw throws transient " +
                "true try ul version void volatile while";
        String[] stopwords = stopList.split(" ");
        for (String s : stopwords) {
            if (s.equals(word)) {
                word = null;
                break;
            }
        }

        return word;
    }

    private String removeClassLibrary(String word) {

        String stopList_common = "util lang";
        boolean general = ifGeneral;
        String stopList_draw = "javax swing awt org jhotdraw";
        boolean draw = libraryTypeCondition.get("Drawing");
        String stopList_modeling = "cc grmm fst";
        boolean modeling = libraryTypeCondition.get("Modeling");

        if (general) {
            word = processAWord(stopList_common, word);
        }

        if (draw && (word != null)) {
            word = processAWord(stopList_draw, word);
        }

        if (modeling && (word != null)) {
            word = processAWord(stopList_modeling, word);
        }

        return word;
    }

    private String removeCustomizedPackageInfo(String word) {
        word = processAWord(customizedPackageList, word);

        return word;
    }

    private String removeCopyrightInfo(String word) {
        word = processAWord(copyrightStopwordList, word);

        return word;
    }

    private String processAWord(String stopwordsString, String word) {
        String[] stopwords = stopwordsString.split(" ");
        for (String s : stopwords) {
            if (s.equals(word)) {
                word = null;
                break;
            }
        }
        return word;
    }
}
