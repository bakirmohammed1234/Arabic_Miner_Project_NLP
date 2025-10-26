// WordStat.java
public class WordStat {
    private final String word;
    private final int count;
    private final double tf;
    private final double idf;
    private final double tfidf;

    public WordStat(String word, int count, double tf, double idf) {
        this.word = word;
        this.count = count;
        this.tf = tf;
        this.idf = idf;
        this.tfidf = tf * idf;
    }

    public String getWord() { return word; }
    public int getCount() { return count; }
    public double getTf() { return tf; }
    public double getIdf() { return idf; }
    public double getTfidf() { return tfidf; }

    @Override
    public String toString() {
        return String.format("%s -> count=%d, TF=%.6f, IDF=%.6f, TF-IDF=%.6f",
                word, count, tf, idf, tfidf);
    }
}
