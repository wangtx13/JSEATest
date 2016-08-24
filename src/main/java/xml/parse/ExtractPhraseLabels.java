package xml.parse;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * Created by wangtianxia1 on 16/8/24.
 */
public class ExtractPhraseLabels {

    private String[] allPhraseLabels;

    public ExtractPhraseLabels(String phraseLabelFilePath, int topicCount, int topicIndex) {
        allPhraseLabels = new String[topicCount];

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            TopicPhraseHandler handler = new TopicPhraseHandler(topicIndex);
            saxParser.parse(phraseLabelFilePath, handler);
            String phrasesLabels = handler.getMatchedPhrases();
            allPhraseLabels[topicIndex] = phrasesLabels;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getAllPhraseLabels() {
        return allPhraseLabels;
    }
}
