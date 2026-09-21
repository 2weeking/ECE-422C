package assignment2.core;

/**
 * Validates that raw player input is a sequence of exactly
 * config.getCodeLength() symbols, each drawn from config.getAlphabet().
 * Input is trimmed and uppercased before checking, which is a
 * console-usability convenience, not a Mastermind-specific rule.
 *
 * Because this validator depends only on the GameConfiguration
 * interface -- never on Mastermind types -- it is a concrete example
 * of infrastructure a future guessing game can reuse unchanged.
 */
public class DefaultGuessValidator implements GuessValidator {
    @Override
    public ValidationResult validate(String rawInput, GameConfiguration config) {
        // Reject a completely missing line of input up front.
        if (rawInput == null) {
            return ValidationResult.failure("No input received.");
        }

        // Clean up whitespace and case so "bgor", " BGOR ", and "BGOR"
        // are all treated the same way.
        String normalized = rawInput.trim().toUpperCase();

        // Empty string (e.g. the player just hit Enter) is not a guess.
        if (normalized.isEmpty()) {
            return ValidationResult.failure("Guess cannot be empty.");
        }

        // Must have exactly as many symbols as the configured code length.
        if (normalized.length() != config.getCodeLength()) {
            return ValidationResult.failure(
                    "Guess must contain exactly " + config.getCodeLength() + " symbols.");
        }

        // Every character used must be one of the legal alphabet symbols.
        for (char c : normalized.toCharArray()) {
            if (!config.getAlphabet().contains(c)) {
                return ValidationResult.failure(
                        "'" + c + "' is not a legal symbol. Legal symbols: " + config.getAlphabet());
            }
        }

        // Passed every check -- turn the cleaned-up string into a Code.
        return ValidationResult.success(Code.fromString(normalized));
    }
}
