import java.io.IOException;

import static org.jsoup.Jsoup.connect;

public class Main {

    public static void main(String[] args) {

        // Read in seed URL

        // Read document

        // Check langauge
        // Reject if not found or incorrect

        // Enqueue links in document

        // Record count to CSV

        // Dump CSV at end

        System.out.println("Hello World");
        for (var str : args) System.out.println(str);

        final var connection = connect("https://en.wikipedia.org/wiki/Web_crawler");
        try {
            final var doc = connection.get();
            System.out.println(doc.title());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
