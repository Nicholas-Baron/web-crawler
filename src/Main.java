import org.jsoup.Jsoup;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World");
        for (var str : args) System.out.println(str);

        var connection = Jsoup.connect("https://en.wikipedia.org/wiki/Web_crawler");
        try {
            var doc = connection.get();
            System.out.println(doc.title());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
