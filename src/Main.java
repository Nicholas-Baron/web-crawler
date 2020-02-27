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
        Queue<String> frontier = new ArrayDeque<>();
        Map<String, Integer> outlinkCount = new HashMap<>(crawlSize);
        frontier.add(args[0]);

        while (!frontier.isEmpty() && numberOfDocs < crawlSize) {

            // Read document
            String currentUrl = frontier.poll();
            Document currentDoc = Jsoup.connect(currentUrl).get();
            Elements urls = currentDoc.select("a[href]");

            // Check language
            // Reject if not found or incorrect

            // Enqueue links in document
            for (Element url : urls) {
                frontier.add(url.absUrl("href"));
                numberOfDocs++;
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
