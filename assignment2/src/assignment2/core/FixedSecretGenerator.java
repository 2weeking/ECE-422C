package assignment2.core;

/**
 * Always returns the same pre-set Code. This is what lets testing mode
 * deterministically control the secret instead of relying on
 * randomness; it satisfies the assignment's testability requirement
 * without adding any special-case branching inside the game engine.
 */
public class FixedSecretGenerator implements SecretGenerator {
    // The secret this generator was told to always hand back.
    private final Code fixedSecret;

    public FixedSecretGenerator(Code fixedSecret) {
        this.fixedSecret = fixedSecret;
    }

    @Override
    public Code generate(GameConfiguration config) {
        // Ignore config entirely -- the whole point of this class is that
        // the secret is fixed, not derived from the configuration.
        return fixedSecret;
    }
}
