import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Main {

    private static final int crawlSize = 100;

    // Seed language
    private static String crawlLanguage = null;
    // Queue of URLs to read
    private static Queue<String> frontier = new ArrayDeque<>();
    // Each URL with its links out
    private static Map<String, Integer> outlinkCount = new HashMap<>(crawlSize);
    // A count of each word
    private static Map<String, Integer> wordCounts = new HashMap<>();

    static void countWords(Document doc) {
        String docBody = doc.body().text();
        String[] words = docBody.split(
                "[^A-ZÃƒâ€¦Ãƒâ€žÃƒâ€“a-zÃƒÂ¥ÃƒÂ¤ÃƒÂ¶]+");
        for (String word : words) {
            if (wordCounts.containsKey(word)) {
                wordCounts.put(word, wordCounts.get(word) + 1);
            } else {
                wordCounts.put(word, 1); //add new element if not seen before
            }

        }

        // if(!hashmap.contains(word)){
//  add word to hashmap
//  else()
//  hashmap.get(word)
// ,ap.put(key, map.get(key) + 1);

    }

    static boolean acceptDocument(Document doc) {
        String lang = doc.getElementsByTag("html").attr("lang");
        if (crawlLanguage == null) {
            crawlLanguage = lang;
            System.out.println("Language set to " + crawlLanguage);
            return true;
        } else { return crawlLanguage.equalsIgnoreCase(lang); }
    }

    public static void main(String[] args) throws IOException {

        // Read in seed URL
        frontier.add(args[0]);

        while (!frontier.isEmpty() && outlinkCount.size() < crawlSize) {

            // Read document
            String currentUrl = frontier.poll();
            if (currentUrl.isEmpty()) continue;

            System.out.println("Downloading " + currentUrl);
            Document currentDoc = Jsoup.connect(currentUrl).get();

            // Check language
            // Reject if not found or incorrect
            if (!acceptDocument(currentDoc) || outlinkCount.containsKey(currentUrl)) {continue;}

            System.out.println("Accepting " + currentUrl);

            // Enqueue links in document
            Elements urls = currentDoc.select("a[href]");
            for (Element url : urls) {
                frontier.add(url.absUrl("href"));
            }

            // Record count to CSV
            outlinkCount.put(currentUrl, urls.size());

            // Count word frequencies
            countWords(currentDoc);
        }

        // Dump CSV at end
        PrintWriter writer = new PrintWriter("report.csv");
        outlinkCount.forEach((key, value) -> writer.println(key + "," + value));
        writer.close();

        // Dump word counts
        PrintWriter counts = new PrintWriter("word_frequencies.csv");
        wordCounts.forEach((key, value) -> counts.println(key + ',' + value));
        counts.close();
    }
}
