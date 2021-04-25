import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

public class SentimentAnalyzer {

    public ResultSentiment findSentiment(String line) {

        //  Creating the Pipeline Properties for the Model
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        long textLength = 0L;
        int sumOfValues = 0;

        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    textLength += partText.length();
                    sumOfValues = sumOfValues + sentiment * partText.length();
                    longest = partText.length();
                }
            }
        }
        double overall = Math.ceil((double)sumOfValues/textLength);
        System.out.println("\nOverall Sentiment Score (0 to 4): " + overall);


        if (overall > 4 || overall < 0) {
            return null; // Should not get this. Means that there exist a duplicate or the sentiment gets too inaccurate to analyze
        }
        ResultSentiment resultSentiment = new ResultSentiment(line, toCss((int) overall));
        return resultSentiment;
    }

    private String toCss(int overall) {
        switch (overall) {
            case 0:
                return "Negative";
            case 1:
                return "Negative-Neutral";
            case 2:
                return "Neutral";
            case 3:
                return "Positive-Neutral";
            case 4:
                return "Positive";
            default:
                return "";

        }
    }


}