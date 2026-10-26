package dictionary_final;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple file-backed dictionary. Words are stored in lowercase, kept in
 * alphabetical order, and saved as "word:definition" lines (UTF-8).
 */
public class Dictionary {
    private final Map<String, String> entries = new TreeMap<>();
    private final Path path;

    public Dictionary(String filePath) throws IOException {
        this.path = Paths.get(filePath);
        load();
    }

    // ---------- Public API ----------

    /** Returns the definition, or null if the word does not exist. */
    public String get(String word) {
        return entries.get(normalize(word));
    }

    public boolean contains(String word) {
        return entries.containsKey(normalize(word));
    }

    /**
     * Adds or replaces a word and saves to disk.
     * @return the previous definition, or null if the word is new
     * @throws IllegalArgumentException if the word or definition is invalid
     */
    public String put(String word, String definition) throws IOException {
        String key = validateWord(word);
        String def = validateDefinition(definition);
        String previous = entries.put(key, def);
        save();
        return previous;
    }

    /** Removes a word and saves to disk. @return true if the word existed */
    public boolean remove(String word) throws IOException {
        boolean removed = entries.remove(normalize(word)) != null;
        if (removed) {
            save();
        }
        return removed;
    }

    /** Returns all words that start with the given prefix, alphabetically. */
    public List<String> searchPrefix(String prefix) {
        String p = normalize(prefix);
        List<String> result = new ArrayList<>();
        for (String key : entries.keySet()) {
            if (key.startsWith(p)) {
                result.add(key);
            }
        }
        return result;
    }

    public Map<String, String> all() {
        return Collections.unmodifiableMap(entries);
    }

    public int size() {
        return entries.size();
    }

    // ---------- Internals ----------

    private static String normalize(String word) {
        return word == null ? "" : word.trim().toLowerCase(Locale.ROOT);
    }

    private static String validateWord(String word) {
        String key = normalize(word);
        if (key.isEmpty()) {
            throw new IllegalArgumentException("The word cannot be empty.");
        }
        if (key.contains(":") || key.contains("\n") || key.contains("\r")) {
            throw new IllegalArgumentException("The word cannot contain ':' or line breaks.");
        }
        return key;
    }

    private static String validateDefinition(String definition) {
        String def = definition == null ? "" : definition.replaceAll("[\\r\\n]+", " ").trim();
        if (def.isEmpty()) {
            throw new IllegalArgumentException("The definition cannot be empty.");
        }
        return def;
    }

    private void load() throws IOException {
        if (!Files.exists(path)) {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.createFile(path);
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2 && !parts[0].trim().isEmpty() && !parts[1].trim().isEmpty()) {
                    entries.put(normalize(parts[0]), parts[1].trim());
                }
            }
        }
    }

    /** Writes to a temporary file first so a crash can never corrupt the real file. */
    private void save() throws IOException {
        Path temp = Paths.get(path.toString() + ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, String> e : entries.entrySet()) {
                writer.write(e.getKey() + ":" + e.getValue());
                writer.newLine();
            }
        }
        Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
    }
}