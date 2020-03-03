import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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

    // Alphabetical
    static List<String> sortedURLReport() {

        List<String> sortedURLEntries = new ArrayList<>(outlinkCount.size());
        outlinkCount.forEach((key, value) -> sortedURLEntries.add(key + ", " + value));
        Collections.sort(sortedURLEntries);
        return sortedURLEntries;
    }

    // Numerical
    static List<String> sortedWordCount() {

        List<String> sortedWordCount = new ArrayList<>(wordCounts.size());
        wordCounts.entrySet()      // get the entries in the map
                  .stream()         // read them one by one
                  .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // sort the stream
                  .forEachOrdered(entry -> sortedWordCount
                          .add(entry.getKey() + ", " + entry.getValue())); // insert into arraylist
        return sortedWordCount;
    }

    //Format URL
    private static String formatURL(String raw) {
        if (raw.contains("#")) raw = raw.substring(0, raw.indexOf("#"));
        if (raw.contains("?")) raw = raw.substring(0, raw.indexOf("?"));
        return raw;
    }

    private static boolean acceptURL(String url) {

        return !url.isEmpty() && !url.endsWith("ogg") && !url.endsWith("php") && !outlinkCount.containsKey(url);
    }

    public static void main(String[] args) throws IOException {

        // Read in seed URL
        frontier.add(args[0]);

        while (!frontier.isEmpty() && outlinkCount.size() < crawlSize) {

            // Read document
            String currentUrl = frontier.poll();

            System.out.println("Downloading " + currentUrl);
            Document currentDoc = Jsoup.connect(currentUrl).get();

            // Check language
            // Reject if not found or incorrect
            if (!acceptDocument(currentDoc)) {continue;}

            System.out.println("Accepting " + currentUrl);

            // Enqueue links in document
            Elements urls = currentDoc.select("a[href]");
            int acceptedURLCount = 0;
            for (Element url : urls) {
                String urlToAdd = formatURL(url.absUrl("href"));
                if (acceptURL(urlToAdd)) {
                    frontier.add(urlToAdd);
                    acceptedURLCount++;
                }
            }

            // Record count to CSV
            outlinkCount.put(currentUrl, acceptedURLCount);

            // Count word frequencies
            countWords(currentDoc);

            System.out.println(frontier.size() + " items in the queue");
        }

        // Dump CSV at end
        PrintWriter writer = new PrintWriter("report.csv");
        sortedURLReport().forEach(entry -> writer.println(entry));
        writer.close();

        // Dump word counts
        PrintWriter counts = new PrintWriter("word_frequencies.csv");
        sortedWordCount().forEach(entry -> counts.println(entry));
        counts.close();
    }
}
