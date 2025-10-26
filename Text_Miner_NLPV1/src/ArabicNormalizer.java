import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ArabicNormalizer {

    private static final ArabicAnalyzer analyzer = new ArabicAnalyzer();

    // Normalisation de base
    public static String normalize(String s) {
        if (s == null) return "";
        s = s.replace("أ", "ا").replace("إ", "ا").replace("آ", "ا");
        s = s.replace("ى", "ي").replace("ئ", "ي").replace("ؤ", "و").replace("ة", "ه");
        s = s.replaceAll("[ًٌٍَُِّْ]", "");
        return s;
    }

    // Extraction de la racine simplifiée
    public static String getRoot(String word) {
        // Supprimer préfixes courants
        word = word.replaceFirst("^(ال|و|ف|ب|ك|ل)", "");
        // Supprimer suffixes courants
        word = word.replaceFirst("(ون|ين|ات|ة)$", "");
        return word;
    }

    // Pipeline complet : normalisation + stemming via Lucene
    public static String[] process(String text) {
        text = normalize(text);
        List<String> result = new ArrayList<>();
        try (TokenStream stream = analyzer.tokenStream("", new StringReader(text))) {
            CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
            stream.reset();
            while (stream.incrementToken()) {
                result.add(cattr.toString());
            }
            stream.end();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toArray(new String[0]);
    }
}
