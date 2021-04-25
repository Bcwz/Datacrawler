import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class RedditCrawler {	
	
	/*-----------------------------------------------------------------------------------------------*/
	
	private String keyword, URL, path;
	private Document page;
	private int limit;
	private static int postCounter, commentCounter;
	
	public RedditCrawler() {
		keyword = null;
		URL = null;
		page = null;
		path = null;
		limit = 0;
	}
	
	public RedditCrawler(String keyword, String URL, Document page, String path, int limit) {
		this.keyword = keyword;
		this.URL = URL;
		this.page = page;
		this.path = path;
		this.limit = limit;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword= keyword;
	}
	
	public String getURL() {
		return URL;
	}
	
	public void setURL(String URL) {
		this.URL = URL;
	}
	
	public Document getPage() {
		return page;
	}
	
	public void setPage(Document page) {
		this.page = page;
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	/*-----------------------------------------------------------------------------------------------*/
	
	private static ArrayList<String> removeStringDuplicates(ArrayList<String> list) {
		ArrayList<String> newList = new ArrayList<>();
		for(String s : list) {
			if (!newList.contains(s)) {
				newList.add(s);
			}
		}
		return newList;
	}
	
	/*-----------------------------------------------------------------------------------------------*/
	
	public static void writeToFile (String text, String path) throws IOException {
		FileWriter data = new FileWriter(path, true);
		PrintWriter write = new PrintWriter(data);
		write.println(text);
		write.close();
	}
	
	/*-----------------------------------------------------------------------------------------------*/
	
	public static String border() {
		return "-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+";
	}
	
	/*-----------------------------------------------------------------------------------------------*/
		
	private static String getType() { 
		Scanner input = new Scanner(System.in);
		
		System.out.println("\nEnter type of search:\n1. Search by Relevance\n2. Search by Top\n3. Search by New\n4. Search by Thread Comments\n");
		
		String selection = input.next();
		switch (selection) {
			case "1":
				return "relevance";
			case "2":
				return "top";
			case "3":
				return "new";
			case "4":
				return "comments";
			default:
				return null;
		}
	}
	
	/*-----------------------------------------------------------------------------------------------*/
	
	private static String getTime() { 
		Scanner input = new Scanner(System.in);
		
		System.out.println("\nEnter period of search:\n1. Search within last Hour\n2. Search within last 24 Hours\n3. "
				+ "Search within last Week\n4. Search within last Month\n5. Search within last Year\n6. Search All\n");
		
		String selection = input.next();
		switch (selection) {
			case "1":
				return "hour";
			case "2":
				return "day";
			case "3":
				return "week";
			case "4":
				return "month";
			case "5":
				return "year";
			case "6":
				return "all";
			default:
				return null;
		}
	}
	
	/*-----------------------------------------------------------------------------------------------*/
	
	private static void readTitle(Document page) {
		String title = page.title(); 					//Get title
		System.out.println("Title: " + title); 			//Print title.
	}
	
	/*-----------------------------------------------------------------------------------------------*/
	
	private static void readSubreddits(Document page, String keyword) throws IOException {		
		Elements linkElements = page.select("a[href]");
		for (Element link : linkElements) {
			if (link.text().length()>=3) {
				String subreddit = link.attr("href");
				if (subreddit.substring(0,3).equals("/r/")) {
					System.out.println(subreddit);
				}
			}
		}
		System.out.println();
	}
	
	/*-----------------------------------------------------------------------------------------------*/
	
	private static ArrayList<String> getSubreddits(Document page, String keyword) throws IOException {		
		Elements linkElements = page.select("a[href]");
		ArrayList<String> subredditLinks = new ArrayList<>();
		for (Element link : linkElements) {
			if (link.text().length()>=3) {
				String subreddit = link.attr("href");
				if (subreddit.substring(0,3).equals("/r/")) {
					subredditLinks.add(subreddit);
				}
			}
		}
		subredditLinks = removeStringDuplicates(subredditLinks);
		return subredditLinks;
	}
	
	/*-----------------------------------------------------------------------------------------------*/
	
	private static ArrayList<String> getPosts(Document page, String keyword) throws IOException {
		Elements linkElements = page.select("a[href]");
		
		ArrayList<String> postLinks = new ArrayList<>();
		
		for (Element e : linkElements) {
			if (e.text().length()>=3) {
				String linkFilter = e.attr("href");
				if (linkFilter.substring(0,3).equals("/r/") && linkFilter.contains("comments")) {
					postLinks.add(linkFilter);
				}
			}
		}
		postLinks = removeStringDuplicates(postLinks);
		return postLinks;
	}

	/*-----------------------------------------------------------------------------------------------*/
	
	private static ArrayList<String> getComments(Document page, String keyword, String path, int limit) throws IOException {


		System.out.println("\nSearching: " + page.title().replace(": ", "@ /r/") + "\n");
		writeToFile( border() + "\nThread: " + page.title().replace(": ", "@ /r/") + "\n" + border() + "\n", path);

		ArrayList<String> comments = new ArrayList<>();		//	Array list to store comments
		Elements commentElements = page.select("._1qeIAgB0cPwnLhDF9XSiJM:contains(" + keyword + ")");
		for (Element commentElement : commentElements) {	//	Parse through comments elements in the page that contain keyword
			writeToFile(commentElement.text() + "\n", path);	//	Write comments as string to text file
			comments.add(commentElement.text());					//	Store comments as string for sentiment analysis
			commentCounter++;										//	Count comments
			if (commentCounter >= limit) {							//	Break if comments gathered match limit
				break;
			}
		}

		return comments;
	}

	/*-----------------------------------------------------------------------------------------------*/
	
	private static ArrayList<String> lookThroughSearch(String keyword, String path, String type, String time, int limit) throws IOException{
		ArrayList<String> posts = new ArrayList<>();
		ArrayList<String> comments = new ArrayList<>();
		
		String searchPage = "https://www.reddit.com/search/?q=" + keyword.replace(' ', '_') + "&sort=" + type + "&t=" + time;
		
		RedditCrawler crawl = new RedditCrawler(keyword, searchPage, Jsoup.connect(searchPage).get(), path, limit);

		posts = getPosts(crawl.getPage(), crawl.getKeyword());
				
		for (String post : posts) {

			crawl.setURL("https://www.reddit.com" + post);
			crawl.setPage(Jsoup.connect(crawl.getURL()).get());

			comments.addAll(getComments(crawl.getPage(), crawl.getKeyword(), crawl.getPath(), crawl.getLimit()));
			postCounter ++;
			if (comments.size() >= crawl.getLimit()) {
				if (comments.size() > crawl.getLimit()) {
					comments.subList(crawl.getLimit(), comments.size()).clear();
				}
				return comments;
			}
		}
		return comments;
	}
	
	/*-----------------------------------------------------------------------------------------------*/
	
	private static ArrayList<String> lookThroughSubreddits(String keyword, String path, String type, String time, int limit, int newLimit) throws IOException{
		ArrayList<String> subreddits = new ArrayList<>();
		ArrayList<String> posts = new ArrayList<>();
		ArrayList<String> comments = new ArrayList<>();
		
		String searchPage = "https://www.reddit.com/search?type=sr%2Cuser&q=" + keyword;
		
		RedditCrawler crawl = new RedditCrawler(keyword, searchPage, Jsoup.connect(searchPage).get(), path, limit);
		
		crawl.setURL("https://www.reddit.com/search?type=sr%2Cuser&q=" + crawl.getKeyword());
		crawl.setPage(Jsoup.connect(crawl.getURL()).get());
		readSubreddits(crawl.getPage(), crawl.getKeyword());
		subreddits = getSubreddits(crawl.getPage(), crawl.getKeyword());
		for (String subreddit : subreddits) {
			crawl.setURL("https://www.reddit.com" + subreddit + "search/?q=" + crawl.getKeyword() + "&restrict_sr=1&sort=" + type + "/?t=" + time);
			crawl.setPage(Jsoup.connect(crawl.getURL()).get());
			posts = getPosts(crawl.getPage(), crawl.getKeyword());
			for (String post : posts) {
				crawl.setURL("https://www.reddit.com" + post);
				crawl.setPage(Jsoup.connect(crawl.getURL()).get());
				comments.addAll(getComments(crawl.getPage(), crawl.getKeyword(), crawl.getPath(), crawl.getLimit()));
				postCounter ++;
				if (comments.size() >= newLimit) {
					if (comments.size() > newLimit) {
						comments.subList(newLimit, comments.size()).clear();
					}
					return comments;
				}
			}
		}
		return comments; 
	}
	
	/*-----------------------------------------------------------------------------------------------*/

	static ArrayList<String> crawlReddit(String keyword, int limit, String path) throws IOException {
		RedditCrawler crawl = new RedditCrawler();

		System.out.println(border() + "\nStart of Reddit Crawl\n" + border());

		crawl.setKeyword(keyword);
		crawl.setLimit(limit);
		crawl.setPath(path);

		String type = getType();
		String time = getTime();

		ArrayList<String> compiledComments = new ArrayList<>();

		System.out.println("\nPrinting to " + crawl.getPath() + "\n");

		compiledComments.addAll(lookThroughSearch(crawl.getKeyword(), crawl.getPath(), type, time, crawl.getLimit()));
		if (commentCounter < crawl.getLimit()) {
			int newLimit = crawl.getLimit() - commentCounter;
			compiledComments.addAll(lookThroughSubreddits(crawl.getKeyword(), crawl.getPath(), type, time, crawl.getLimit(), newLimit));
		}

		System.out.println(border() + "\nEnd of Reddit Crawl\nPosts Examined: "
				+ postCounter + "\nComments Collected: " + commentCounter + "\n" + border() + "\n\n");
		writeToFile(border() + "\nEnd of Reddit Crawl\nPosts Examined: "
				+ postCounter + "\nComments Collected: " + commentCounter + "\n" + border() + "\n\n", crawl.getPath());
		return compiledComments;
	}

	/*-----------------------------------------------------------------------------------------------*/

}