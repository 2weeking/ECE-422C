package assignment2.wordle;

import assignment2.core.Code;
import assignment2.core.GameConfiguration;
import assignment2.core.GuessValidator;
import assignment2.core.ValidationResult;
import java.util.Locale;

/** Checks word shape and membership; WordleConfiguration supplies the external word list. */
public final class WordleValidator implements GuessValidator {
    @Override
    public ValidationResult validate(String input, GameConfiguration config) {
        if (!(config instanceof WordleConfiguration)) {
            throw new IllegalArgumentException("WordleValidator requires WordleConfiguration.");
        }
        if (input == null) return ValidationResult.failure("No input received.");
        String word = input.trim().toUpperCase(Locale.ROOT);
        if (word.length() != config.getCodeLength()) {
            return ValidationResult.failure("Word must contain exactly " + config.getCodeLength() + " letters.");
        }
        if (!word.matches("[A-Z]+")) {
            return ValidationResult.failure("Use letters A-Z only.");
        }
        if (!((WordleConfiguration) config).getWords().contains(word)) {
            return ValidationResult.failure("Word is not in the allowed-word list.");
        }
        return ValidationResult.success(Code.fromString(word));
    }
}
