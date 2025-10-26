import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class TextProcessor {

    // Lit tous les fichiers et renvoie tous les tokens normalisés et racinisés
    public static List<String> tokenizeFiles(Path inputPath, Set<String> stopwords, List<Path> filesToProcess) throws IOException {
        List<String> allTokens = new ArrayList<>();

        for (Path file : filesToProcess) {
            try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    // 1️⃣ Remplacer ponctuation et chiffres par espace
                    line = line.replaceAll("[\\p{Punct}؟،؛\\d]", " ");
                    // 2️⃣ Split sur espaces
                    String[] raw = line.split("\\s+");
                    for (String r : raw) {
                        // 3️⃣ Normalisation
                        String norm = ArabicNormalizer.normalize(r);
                        if (norm == null || norm.isEmpty()) continue;

                        // 4️⃣ Supprimer caractères non arabes restants
                        norm = norm.replaceAll("[^\\p{IsArabic}]", "");
                        if (norm.isEmpty()) continue;

                        // 5️⃣ Stopwords
                        if (stopwords.contains(norm)) continue;

                        // 6️⃣ Extraire racine
                        String root = ArabicRootExtractor.getRoot(norm);
                        if (root == null || root.isEmpty()) continue;

                        allTokens.add(root);
                    }
                }
            }
        }
        return allTokens;
    }

    // Compte occurrences
    public static Map<String, Integer> countOccurrences(List<String> tokens) {
        Map<String, Integer> occ = new HashMap<>();
        for (String t : tokens) {
            occ.put(t, occ.getOrDefault(t, 0) + 1);
        }
        return occ;
    }

    // Construire map fréquence -> nombre de mots ayant cette fréquence
    public static Map<Integer, Integer> buildFreqToCount(Map<String, Integer> occ) {
        Map<Integer, Integer> freqCount = new HashMap<>();
        for (int count : occ.values()) {
            freqCount.put(count, freqCount.getOrDefault(count, 0) + 1);
        }
        return freqCount;
    }

    // Calcul TF
    public static Map<String, Double> computeTF(Map<String, Integer> occ, int totalTokens) {
        Map<String, Double> tf = new HashMap<>();
        if (totalTokens <= 0) return tf;
        for (Map.Entry<String, Integer> e : occ.entrySet()) {
            tf.put(e.getKey(), (double) e.getValue() / totalTokens);
        }
        return tf;
    }

    // Calcul IDF basé sur le nombre de fichiers contenant chaque racine
    public static Map<String, Double> computeIDF(Map<String, Set<Path>> rootDocs, int totalDocs) {
        Map<String, Double> idf = new HashMap<>();
        for (Map.Entry<String, Set<Path>> e : rootDocs.entrySet()) {
            int df = e.getValue().size();
            idf.put(e.getKey(), Math.log((double) totalDocs / (1 + df)) + 1.0);
        }
        return idf;
    }

    // Construire WordStat (TF, IDF, TF-IDF)
    public static Map<String, WordStat> buildWordStats(Map<String, Integer> occ, Map<String, Double> tf,
                                                       Map<String, Double> idf) {
        Map<String, WordStat> stats = new HashMap<>();
        for (String w : occ.keySet()) {
            int count = occ.get(w);
            double tfVal = tf.getOrDefault(w, 0.0);
            double idfVal = idf.getOrDefault(w, 1.0);
            stats.put(w, new WordStat(w, count, tfVal, idfVal));
        }
        return stats;
    }

    // Processus principal
    public static void process(Path inputPath, Path stopwordPath) throws IOException {
        Set<String> stops = StopwordLoader.loadStopwords(stopwordPath);
        List<Path> filesToProcess = new ArrayList<>();

        if (Files.isDirectory(inputPath)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(inputPath, "*.txt")) {
                for (Path p : ds) filesToProcess.add(p);
            }
        } else {
            filesToProcess.add(inputPath);
        }

        // Map racine -> ensemble de fichiers contenant la racine (pour IDF)
        Map<String, Set<Path>> rootDocs = new HashMap<>();

        // Pour chaque fichier, stocker les racines uniques pour IDF
        for (Path file : filesToProcess) {
            Set<String> rootsInFile = new HashSet<>();
            List<String> tokens = tokenizeFiles(file, stops, Arrays.asList(file));
            rootsInFile.addAll(tokens);
            for (String root : rootsInFile) {
                rootDocs.computeIfAbsent(root, k -> new HashSet<>()).add(file);
            }
        }

        // Tous les tokens normalisés et racinisés (pour TF global)
        List<String> allTokens = tokenizeFiles(inputPath, stops, filesToProcess);
        Map<String, Integer> occ = countOccurrences(allTokens);
        int totalTokens = allTokens.size();

        Map<Integer, Integer> freqCount = buildFreqToCount(occ);

        // Calcul TF et IDF
        Map<String, Double> tf = computeTF(occ, totalTokens);
        Map<String, Double> idf = computeIDF(rootDocs, filesToProcess.size());

        // Construire WordStat
        Map<String, WordStat> stats = buildWordStats(occ, tf, idf);

        // Affichage
        System.out.println("=== Résultat global ===");
        System.out.println("Total tokens (après nettoyage) : " + totalTokens);
        System.out.println("Nombre racines distinctes : " + occ.size());
        System.out.println("Distribution fréquence -> nombre de racines : " + freqCount);
        System.out.println("Racines et statistiques :");

        List<WordStat> sorted = stats.values().stream()
                .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
        sorted.forEach(s -> System.out.println(s.toString()));
    }

    // Main
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TextProcessor <inputFileOrDir> <stopwordsFile>");
            return;
        }
        Path input = Paths.get(args[0]);
        Path stops = Paths.get(args[1]);
        try {
            process(input, stops);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
