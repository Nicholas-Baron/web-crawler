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

    public static void main(String[] args) throws IOException {

        int numberOfDocs = 1;
        final int crawlSize = 100;
        // Read in seed URL

        // Seed language
        String crawlLanguage = null;
        Queue<String> frontier = new ArrayDeque<>();
        frontier.add(args[0]);

        Map<String, Integer> outlinkCount = new HashMap<>(crawlSize);

        while (!frontier.isEmpty() && numberOfDocs < crawlSize) {

            // Read document
            String currentUrl = frontier.poll();
            if (currentUrl.isEmpty()) continue;

            System.out.println("Downloading " + currentUrl);
            Document currentDoc = Jsoup.connect(currentUrl).get();

            Elements urls = currentDoc.select("a[href]");

            numberOfDocs++;

            // Check language
            String lang = currentDoc.getElementsByTag("html").attr("lang");
            if (crawlLanguage == null) {
                crawlLanguage = lang;
                System.out.println("Language set to " + crawlLanguage);
            } else if (!crawlLanguage.equalsIgnoreCase(lang)) {
                System.out.println("Rejecting " + currentUrl);
                continue;
            }

            System.out.println("Accepting " + currentUrl);
            // Reject if not found or incorrect

            // Enqueue links in document
            for (Element url : urls) {
                frontier.add(url.absUrl("href"));
            }

            // Record count to CSV
            outlinkCount.put(currentUrl, urls.size());
        }

        // Dump CSV at end

        PrintWriter writer = new PrintWriter("report.csv");
        outlinkCount.forEach((key, value) -> writer.println(key + "," + value));
        writer.close();
    }
}
