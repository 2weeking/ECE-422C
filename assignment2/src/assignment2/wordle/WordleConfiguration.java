package assignment2.wordle;

import assignment2.core.GameConfiguration;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Wordle's external dictionary and turn limits; words serve as possible answers and guesses. */
public final class WordleConfiguration implements GameConfiguration {
    public static final int DEFAULT_LENGTH = 5;
    public static final int DEFAULT_ATTEMPTS = 6;
    private final int length;
    private final int attempts;
    private final Set<String> words;
    private final Set<Character> alphabet;

    public static WordleConfiguration fromFile(Path file) throws IOException {
        return new WordleConfiguration(DEFAULT_ATTEMPTS, DEFAULT_LENGTH,
                Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    public WordleConfiguration(int attempts, int length, List<String> lines) {
        if (attempts <= 0 || length <= 0 || lines == null) {
            throw new IllegalArgumentException("Attempts and length must be positive; word list must exist.");
        }
        this.length = length;
        this.attempts = attempts;
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String line : lines) {
            String word = line.trim().toUpperCase(Locale.ROOT);
            if (word.isEmpty() || word.startsWith("#")) continue;
            if (word.length() != length || !word.matches("[A-Z]+")) {
                throw new IllegalArgumentException("Invalid dictionary entry: '" + line + "' (expected "
                        + length + " English letters).");
            }
            normalized.add(word);
        }
        if (normalized.isEmpty()) throw new IllegalArgumentException("Word list is empty.");
        this.words = Collections.unmodifiableSet(normalized);
        LinkedHashSet<Character> letters = new LinkedHashSet<>();
        for (char c = 'A'; c <= 'Z'; c++) letters.add(c);
        this.alphabet = Collections.unmodifiableSet(letters);
    }

    public Set<String> getWords() { return words; }
    @Override public int getMaxAttempts() { return attempts; }
    @Override public int getCodeLength() { return length; }
    @Override public Set<Character> getAlphabet() { return alphabet; }
}
