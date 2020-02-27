import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws IOException{

        int numberOfDocs = 1;
        // Read in seed URL
        Queue<String> frontier = new ArrayDeque<>();
        frontier.add(args[0]);


        // Read document

        while(!frontier.isEmpty() && numberOfDocs < 100) {
            var currentDoc = Jsoup.connect(frontier.poll()).get();
            Elements urls = currentDoc.select("a[href]");
            for(Element url : urls){
                frontier.add(url.absUrl("href"));
                numberOfDocs++;
            }

        }

        // Check language
        // Reject if not found or incorrect

        // Enqueue links in document

        // Record count to CSV

        // Dump CSV at end

//        System.out.println("Hello World");
//        for (var str : args) System.out.println(str);
//
//        final var connection = connect("https://en.wikipedia.org/wiki/Web_crawler");
//        try {
//            final var doc = connection.get();
//            System.out.println(doc.title());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
}
