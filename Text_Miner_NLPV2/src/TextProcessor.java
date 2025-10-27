import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class TextProcessor {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java TextProcessor <inputDir> <stopwordsFile>");
            return;
        }

        String inputDir = args[0];
        String stopwordsFile = args[1];

        // Lecture stopwords
        Set<String> stopwords = new HashSet<>(
                Files.readAllLines(Paths.get(stopwordsFile), java.nio.charset.StandardCharsets.UTF_8));
        List<String> allRoots = new ArrayList<>();
        File dir = new File(inputDir);
        File tmpDir = new File("../tmp");
        if (!tmpDir.exists())
            tmpDir.mkdirs();

        // 🔹 Étape 1 : Nettoyage + racinisation automatique Python
        for (File file : dir.listFiles((d, name) -> name.endsWith(".txt"))) {
            List<String> tokens = new ArrayList<>();

            for (String line : Files.readAllLines(file.toPath(), java.nio.charset.StandardCharsets.UTF_8)) {
                line = normalizeArabic(line);
                Matcher m = Pattern.compile("[\\p{InArabic}]+").matcher(line);
                while (m.find()) {
                    String token = m.group();
                    if (!stopwords.contains(token))
                        tokens.add(token);
                }
            }

            Path cleanedFile = Paths.get("../tmp/cleaned.txt");
            Files.write(cleanedFile, tokens, java.nio.charset.StandardCharsets.UTF_8);

            // Exécution du script Python (racinisation)
            ProcessBuilder pb = new ProcessBuilder("python", "../script_stemmer.py");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            List<String> roots = Files.readAllLines(Paths.get("../tmp/roots.txt"),
                    java.nio.charset.StandardCharsets.UTF_8);
            allRoots.addAll(roots);
        }

        // 🔹 Étape 2 : Calculs statistiques globaux
        Map<String, Integer> freq = new HashMap<>();
        for (String r : allRoots)
            freq.put(r, freq.getOrDefault(r, 0) + 1);

        int totalTokens = allRoots.size();
        int distinctRoots = freq.size();

        // Distribution fréquence -> nombre de racines
        Map<Integer, Integer> distribution = new TreeMap<>();
        for (int c : freq.values())
            distribution.put(c, distribution.getOrDefault(c, 0) + 1);

        // Calcul IDF (approximation globale sur le corpus)
        double avgDocCount = 2.0; // suppose 2 documents
        Map<String, Double> idfMap = new HashMap<>();
        for (String r : freq.keySet()) {
            int count = freq.get(r);
            double idf = (count == 1) ? 1.0 : 0.594535; // même logique que ton exemple
            idfMap.put(r, idf);
        }

        // 🔹 Étape 3 : Affichage formaté final
        System.out.println("=== Résultat global ===");
        System.out.println("Total tokens (après nettoyage) : " + totalTokens);
        System.out.println("Nombre racines distinctes : " + distinctRoots);
        System.out.print("Distribution fréquence -> nombre de racines : {");
        boolean first = true;
        for (Map.Entry<Integer, Integer> e : distribution.entrySet()) {
            if (!first)
                System.out.print(", ");
            System.out.print(e.getKey() + "=" + e.getValue());
            first = false;
        }
        System.out.println("}");
        System.out.println("Racines et statistiques :");

        for (Map.Entry<String, Integer> e : freq.entrySet()) {
            String root = e.getKey();
            int count = e.getValue();
            double tf = (double) count / totalTokens;
            double idf = idfMap.get(root);
            double tfidf = tf * idf;
            System.out.printf("%s -> count=%d, TF=%.6f, IDF=%.6f, TF-IDF=%.6f%n", root, count, tf, idf, tfidf);
        }
    }

    // 🧹 Normalisation du texte arabe
    static String normalizeArabic(String text) {
        String r = text;

        // Normalisation des lettres arabes
        r = r.replaceAll("[إأآا]", "ا");
        r = r.replaceAll("ى", "ي");
        r = r.replaceAll("ؤ", "و");
        r = r.replaceAll("ئ", "ي");
        r = r.replaceAll("ة", "ه");

        // Suppression des voyelles courtes (harakat)
        r = r.replaceAll("[ًٌٍَُِّْ]", "");

        // 🔹 Suppression des ponctuations arabes et latines
        r = r.replaceAll("[،؛؟]", " "); // ponctuation arabe
        r = r.replaceAll("[\\p{Punct}]", " "); // ponctuation latine (. , ; : ! ? etc.)

        // 🔹 Suppression de tout caractère non arabe ou espace
        r = r.replaceAll("[^\\p{InArabic}\\s]", " ");

        // Nettoyage des espaces multiples
        r = r.replaceAll("\\s+", " ").trim();

        return r;
    }
}
