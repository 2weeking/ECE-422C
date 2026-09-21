package assignment2.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates a secret by drawing config.getCodeLength() symbols
 * uniformly at random (with repetition) from config.getAlphabet().
 * This has no Mastermind-specific knowledge, so it is reused unchanged
 * by any future game with the same GameConfiguration shape.
 */
public class RandomSecretGenerator implements SecretGenerator {
    private final Random random;

    // Default constructor: use a normal, unseeded Random (real randomness).
    public RandomSecretGenerator() {
        this(new Random());
    }

    /** Package/test-friendly constructor so a seeded Random can be injected. */
    public RandomSecretGenerator(Random random) {
        this.random = random;
    }

    @Override
    public Code generate(GameConfiguration config) {
        // Alphabet as a List so we can pick a random index out of it.
        List<Character> alphabet = new ArrayList<>(config.getAlphabet());
        List<Character> symbols = new ArrayList<>();

        // Pick codeLength symbols, one at a time, allowing repeats
        // (colors/symbols are allowed to repeat in the secret).
        for (int i = 0; i < config.getCodeLength(); i++) {
            symbols.add(alphabet.get(random.nextInt(alphabet.size())));
        }
        return new Code(symbols);
    }
}
