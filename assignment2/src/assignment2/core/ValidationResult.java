package assignment2.core;

/**
 * Outcome of validating raw player input: either a successfully
 * parsed Code, or a human-readable reason it was rejected. Keeping
 * this as its own type (instead of throwing exceptions for ordinary
 * bad input) lets the game loop treat "invalid guess" as a normal,
 * expected outcome that does not consume an attempt.
 */
public final class ValidationResult {
    private final boolean valid;       // true = input was accepted
    private final Code code;           // set only when valid == true
    private final String errorMessage; // set only when valid == false

    // Private constructor -- callers must go through success(...) or
    // failure(...) below, so it's impossible to build a nonsensical
    // "valid but no code" or "invalid but no message" result.
    private ValidationResult(boolean valid, Code code, String errorMessage) {
        this.valid = valid;
        this.code = code;
        this.errorMessage = errorMessage;
    }

    // Build a "this input was fine" result, carrying the parsed Code.
    public static ValidationResult success(Code code) {
        return new ValidationResult(true, code, null);
    }

    // Build a "this input was rejected" result, carrying why.
    public static ValidationResult failure(String errorMessage) {
        return new ValidationResult(false, null, errorMessage);
    }

    public boolean isValid() {
        return valid;
    }

    // Only meaningful when isValid() is true.
    public Code getCode() {
        return code;
    }

    // Only meaningful when isValid() is false.
    public String getErrorMessage() {
        return errorMessage;
    }
}
