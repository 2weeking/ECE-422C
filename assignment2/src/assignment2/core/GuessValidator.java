package assignment2.core;

/** Turns raw console input into a Code, or explains why it can't. */
public interface GuessValidator {
    // Check rawInput against the rules in config (length, alphabet, etc.)
    // and return either a parsed Code or an error message.
    ValidationResult validate(String rawInput, GameConfiguration config);
}
