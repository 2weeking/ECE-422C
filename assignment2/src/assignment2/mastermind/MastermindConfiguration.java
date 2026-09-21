package assignment2.mastermind;

import assignment2.core.GameConfiguration;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Mastermind's own configuration values. Defaults match the assignment
 * spec (4 pegs, 12 attempts, colors B G O P R Y), but every value can
 * be overridden through the constructor -- for example to change the
 * number of pegs, the attempt limit, or the legal color set -- without
 * touching GuessingGameEngine, DefaultGuessValidator, or the secret
 * generators, since those only depend on the GameConfiguration
 * interface. This is the "GameConfiguration class" the assignment asks
 * for.
 */
public class MastermindConfiguration implements GameConfiguration {
    // Default values straight from the assignment spec.
    public static final int DEFAULT_CODE_LENGTH = 4;
    public static final int DEFAULT_MAX_ATTEMPTS = 12;
    public static final char[] DEFAULT_COLORS = {'B', 'G', 'O', 'P', 'R', 'Y'};
    public static final int MAX_LEGAL_COLORS = 10; // spec: "up to 10 distinct" colors

    // The actual values this particular configuration was built with.
    private final int maxAttempts;
    private final int codeLength;
    private final Set<Character> alphabet;

    /** Builds the default Mastermind configuration described in the assignment. */
    public MastermindConfiguration() {
        this(DEFAULT_MAX_ATTEMPTS, DEFAULT_CODE_LENGTH, defaultColorSet());
    }

    /** Builds a customized configuration, e.g. for a "changed configuration" test. */
    public MastermindConfiguration(int maxAttempts, int codeLength, Set<Character> alphabet) {
        // Validate inputs up front so a bad configuration fails fast and
        // loudly, instead of causing confusing bugs later in the game loop.
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be positive.");
        }
        if (codeLength <= 0) {
            throw new IllegalArgumentException("codeLength must be positive.");
        }
        if (alphabet == null || alphabet.isEmpty()) {
            throw new IllegalArgumentException("alphabet must contain at least one color.");
        }
        if (alphabet.size() > MAX_LEGAL_COLORS) {
            throw new IllegalArgumentException(
                    "alphabet may contain at most " + MAX_LEGAL_COLORS + " colors.");
        }
        this.maxAttempts = maxAttempts;
        this.codeLength = codeLength;
        // Copy defensively (LinkedHashSet keeps a predictable display order)
        // so the caller can't mutate our alphabet after the fact.
        this.alphabet = new LinkedHashSet<>(alphabet);
    }

    // Builds the {B, G, O, P, R, Y} set used by the no-argument constructor.
    private static Set<Character> defaultColorSet() {
        Set<Character> colors = new LinkedHashSet<>();
        for (char c : DEFAULT_COLORS) {
            colors.add(c);
        }
        return colors;
    }

    @Override
    public int getMaxAttempts() {
        return maxAttempts;
    }

    @Override
    public int getCodeLength() {
        return codeLength;
    }

    @Override
    public Set<Character> getAlphabet() {
        return alphabet;
    }
}
