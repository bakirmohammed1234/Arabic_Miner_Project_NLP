// StopwordLoader.java
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class StopwordLoader {
    public static Set<String> loadStopwords(Path path) throws IOException {
        Set<String> stops = new HashSet<>();
        if (!Files.exists(path)) return stops;
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String norm = ArabicNormalizer.normalize(line);
                if (norm != null && !norm.isEmpty()) {
                    stops.add(norm);
                }
            }
        }
        return stops;
    }
}
