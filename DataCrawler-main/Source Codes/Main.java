import org.jfree.ui.RefineryUtilities;
import twitter4j.Status;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        //  This will remove the red system output of the console
        PrintStream err = System.err;

        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }));

        //  User Input
        Scanner input = new Scanner(System.in);
        // User Input: Search Term
        System.out.print("Enter a keyword: ");
        String keyword = input.nextLine();
        // User Input: No. of search results returned
        System.out.print("Enter a comment limit: ");
        int limit = input.nextInt();
        // User Input: Path Directory
        System.out.print("Enter a directory path (eg. C:\\Users\\Student\\Desktop\\WebCrawl\\...) : ");
        String fileLocation = input.next();
        // Text File With Sentiment
        String path = fileLocation + keyword + ".txt";
        String sentimentPath = fileLocation + keyword + "Sentiment.txt";

        System.out.println();
        // Twitter Declaration
        TwitterSearch twitterSearch = new TwitterSearch();
        List<Status> statuses = twitterSearch.search(keyword, limit);

        // Reddit Declaration
        ArrayList<String> redditComments = new ArrayList<>();
        redditComments.addAll(RedditCrawler.crawlReddit(keyword, limit, path));
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

        System.out.println(RedditCrawler.border() + "\nSentiment Analysis for Reddit\n" + RedditCrawler.border() + "\n");

        // Reddit Main
        int c0 = 0;
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        int c4 = 0;

        for (String s : redditComments) {
            ResultSentiment resultSentiment = sentimentAnalyzer.findSentiment(s);
            System.out.println(resultSentiment);
            RedditCrawler.writeToFile(resultSentiment.toString() + "\n",sentimentPath);
            if (resultSentiment.getCssClass().equals("Negative")) {
                c0++;
            }
            if (resultSentiment.getCssClass().equals("Negative-Neutral")) {
                c1++;
            }
            if (resultSentiment.getCssClass().equals("Neutral")) {
                c2++;
            }
            if (resultSentiment.getCssClass().equals("Positive-Neutral")) {
                c3++;
            }
            if (resultSentiment.getCssClass().equals("Positive")) {
                c4++;
            }
        }

        //  Total Sentiment Score Tally for Reddit
        String redditSentiment = RedditCrawler.border() + "\nSentiment Analysis for Reddit\n" + RedditCrawler.border()
                + "\nTotal No. of Negative: " + c0
                + "\nTotal No. of Negative-Neutral: " + c1
                + "\nTotal No. of Neutral: " + c2
                + "\nTotal No. of Positive-Neutral: " + c3
                + "\nTotal No. of Positive: " + c4 + "\n";
        System.out.println(redditSentiment);
        RedditCrawler.writeToFile(redditSentiment ,sentimentPath);

        //  Twitter Main
        int count0 = 0;
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        System.out.println(RedditCrawler.border()+ "\nStart of Twitter Crawler\n" + RedditCrawler.border());
        for (Status status : statuses) {
            //  To File Output
            RedditCrawler.writeToFile(MessageFormat.format("[{0}]@{1} - {2}", status
                    .getCreatedAt(), status.getUser()
                    .getScreenName(), status.getText()), path);

            //  To Console Output
            ResultSentiment resultSentiment = sentimentAnalyzer.findSentiment(MessageFormat
                    .format("[{0}]@{1} - {2}", status.getCreatedAt(), status.getUser()
                            .getScreenName(), status.getText()));
            System.out.println(resultSentiment);
            RedditCrawler.writeToFile(resultSentiment.toString() + "\n" ,sentimentPath);

            if (resultSentiment.getCssClass().equals("Negative")) {
                count0++;
            }
            if (resultSentiment.getCssClass().equals("Negative-Neutral")) {
                count1++;
            }
            if (resultSentiment.getCssClass().equals("Neutral")) {
                count2++;
            }
            if (resultSentiment.getCssClass().equals("Positive-Neutral")) {
                count3++;
            }
            if (resultSentiment.getCssClass().equals("Positive")) {
                count4++;
            }

        }

        //  Total Sentiment Score Tally for Twitter
        String twitterSentiment = RedditCrawler.border() + "\nSentiment Analysis for Twitter\n" + RedditCrawler.border()
            + "\nTotal No. of Negative: " + count0
            + "\nTotal No. of Negative-Neutral: " + count1
            + "\nTotal No. of Neutral: " + count2
            + "\nTotal No. of Positive-Neutral: " + count3
            + "\nTotal No. of Positive: " + count4 + "\n";
        System.out.println(twitterSentiment);
        RedditCrawler.writeToFile(twitterSentiment ,sentimentPath);

        // Total Sentiment tally across both platforms
        System.out.println(RedditCrawler.border() + "\nSentiment Analysis Across Both Platforms\n" + RedditCrawler.border()
                + "\nTotal No. of Negative: " + (c0+count0)
                + "\nTotal No. of Negative-Neutral: " + (c1+count1)
                + "\nTotal No. of Neutral: " + (c2+count2)
                + "\nTotal No. of Positive-Neutral: " + (c3+count3)
                + "\nTotal No. of Positive: " + (c4+count4) + "\n");

        //  Reddit Pie Chart Projection
        PieChart_AWT demoReddit = new PieChart_AWT( "Reddit Sentiment Analysis" , c0, c1, c2, c3 ,c4);
        demoReddit.setSize( 600 , 400 );
        RefineryUtilities.centerFrameOnScreen( demoReddit );
        demoReddit.setVisible( true );

        //  Twitter Pie Chart Projection
        PieChart_AWT demoTwitter = new PieChart_AWT( "Twitter Sentiment Analysis" ,
                count0, count1, count2, count3 ,count4);
        demoTwitter.setSize( 600 , 400 );
        RefineryUtilities.centerFrameOnScreen( demoTwitter );
        demoTwitter.setVisible( true );



    }
}
