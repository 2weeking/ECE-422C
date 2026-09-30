package assignment2.wordle;

import assignment2.core.Code;
import assignment2.core.GameConfiguration;
import assignment2.core.SecretGenerator;
import java.util.ArrayList;
import java.util.Random;

/** Chooses a complete allowed word, rather than independent random letters. */
public final class WordleSecretGenerator implements SecretGenerator {
    private final Random random;
    public WordleSecretGenerator() { this(new Random()); }
    public WordleSecretGenerator(Random random) { this.random = random; }

    @Override public Code generate(GameConfiguration config) {
        if (!(config instanceof WordleConfiguration)) {
            throw new IllegalArgumentException("WordleSecretGenerator requires WordleConfiguration.");
        }
        ArrayList<String> words = new ArrayList<>(((WordleConfiguration) config).getWords());
        return Code.fromString(words.get(random.nextInt(words.size())));
    }
}
