import org.apache.lucene.analysis.ar.ArabicStemmer;

public class ArabicRootExtractor {

    private static final ArabicStemmer stemmer = new ArabicStemmer();

    // Retourne la racine (stem) d'un mot arabe
    public static String getRoot(String word) {
        if (word == null || word.isEmpty()) return "";
        char[] chars = word.toCharArray();
        int len = chars.length;
        int newLen = stemmer.stem(chars, len);
        return new String(chars, 0, newLen);
    }
}
