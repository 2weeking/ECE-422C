package assignment2.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable ordered sequence of single-character symbols.
 *
 * This is the game-independent representation of both a secret and a
 * guess. Any turn-based game in which a player tries to match a hidden
 * sequence drawn from a fixed alphabet (Mastermind colors, Bulls-and-
 * Cows digits, a Wordle-style word, etc.) can reuse this type as-is;
 * nothing about it is specific to pegs or colors.
 */
public final class Code {
    // The actual symbols, in order. Wrapped in Collections.unmodifiableList
    // below so that once a Code is built, nobody can sneak in and change it.
    private final List<Character> symbols;

    public Code(List<Character> symbols) {
        // Guard against nonsense input: a code has to have something in it.
        if (symbols == null || symbols.isEmpty()) {
            throw new IllegalArgumentException("A code must contain at least one symbol.");
        }
        // Copy the incoming list first, then make the copy read-only.
        // This protects us even if the caller keeps a reference to the
        // original list and mutates it later.
        this.symbols = Collections.unmodifiableList(new ArrayList<>(symbols));
    }

    /** Convenience factory: builds a Code from a String, one symbol per character. */
    public static Code fromString(String raw) {
        // Split the string into individual characters and hand them to the constructor.
        List<Character> chars = new ArrayList<>();
        for (char c : raw.toCharArray()) {
            chars.add(c);
        }
        return new Code(chars);
    }

    // How many pegs/symbols this code has (e.g. 4 for default Mastermind).
    public int length() {
        return symbols.size();
    }

    // The symbol at a given 0-based position, e.g. symbolAt(0) is the first peg.
    public char symbolAt(int index) {
        return symbols.get(index);
    }

    // Exposes the underlying list (already unmodifiable) for callers that
    // want to iterate over every symbol.
    public List<Character> asList() {
        return symbols;
    }

    // Renders the code back as a plain string, e.g. [B, G, O, R] -> "BGOR".
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (char c : symbols) {
            sb.append(c);
        }
        return sb.toString();
    }

    // Two Codes are equal when they have the same symbols in the same order.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Code)) return false;
        return symbols.equals(((Code) o).symbols);
    }

    @Override
    public int hashCode() {
        return symbols.hashCode();
    }
}
