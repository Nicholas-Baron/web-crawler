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

    static final int crawlSize = 100;
    static int numberOfDocs = 1;
    // Seed language
    static String crawlLanguage = null;
    static Queue<String> frontier = new ArrayDeque<>();

    static Map<String, Integer> outlinkCount = new HashMap<>(crawlSize);
    static Map<String, Integer> wordCounts = new HashMap<>();

    static void countWords(Document doc) {

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

        while (!frontier.isEmpty() && numberOfDocs < crawlSize) {

            // Read document
            String currentUrl = frontier.poll();
            if (currentUrl.isEmpty()) continue;

            System.out.println("Downloading " + currentUrl);
            Document currentDoc = Jsoup.connect(currentUrl).get();

            // Check language
            // Reject if not found or incorrect
            if (!acceptDocument(currentDoc)) {continue;} else {
                numberOfDocs++;
            }

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
    }
}
