import java.util.*;
import java.util.regex.*;

public class TextCleaner {
    public static List<String> cleanText(String text, Set<String> stopwords) {
        text = text.replaceAll("[^\\p{InArabic}\\s]", ""); // garde uniquement les lettres arabes
        text = text.replaceAll("\\s+", " ").trim();
        String[] tokens = text.split(" ");
        List<String> cleaned = new ArrayList<>();
        for (String token : tokens) {
            if (!token.isEmpty() && !stopwords.contains(token)) {
                cleaned.add(token);
            }
        }
        return cleaned;
    }
}
