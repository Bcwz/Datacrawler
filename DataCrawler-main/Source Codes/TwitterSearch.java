import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Collections;
import java.util.List;

public class TwitterSearch {

    public List<Status> search(String keyword, int limit) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().setTweetModeExtended(true);
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey("AN6A2bjebsK8wQ51Hm4ZhsKFB")
                .setOAuthConsumerSecret("d6CW6FiuldwBSHFasKV0WN99FK7HIoYqsnfFLvxVmBghW5Ug9t")
                .setOAuthAccessToken("1234506345274990595-lyXB1qhLxjK9f5Xk6zD30uNFuZ0Qpy")
                .setOAuthAccessTokenSecret("UfVIOnKU6DTcG9M513le5aQN3b3CMDMn1YbroRIyaZH2a");

        TwitterFactory tf = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = tf.getInstance();

        Query query = new Query(keyword + " filter:safe -filter:media -filter:news " +
                "-filter:nativeretweets -filter:retweets -filter:links -filter:replies");
        query.setCount(limit);
        query.setLocale("en-SG");
        query.setLang("en");
        try {
            QueryResult queryResult = twitter.search(query);
            return queryResult.getTweets();

        } catch (TwitterException e) {
            e.printStackTrace();
            System.out.println("Failure to search tweets: " + e.getMessage());
        }
        return Collections.emptyList();
    }

}