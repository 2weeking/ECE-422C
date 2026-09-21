package assignment2.core;

import java.util.Set;

/**
 * Game-independent contract for anything that can be configured by:
 *   - how many attempts the player gets,
 *   - how long the secret sequence is,
 *   - which symbols are legal.
 *
 * Any future guessing game whose rules fit this same shape (a hidden
 * sequence drawn from a fixed alphabet, guessed within a bounded
 * number of attempts) can implement this interface and plug directly
 * into GuessingGameEngine and DefaultGuessValidator without any
 * changes to that infrastructure.
 */
public interface GameConfiguration {
    // How many guesses the player is allowed before losing (e.g. 12).
    int getMaxAttempts();

    // How many symbols are in the secret/guess (e.g. 4 pegs).
    int getCodeLength();

    // The set of characters a guess is allowed to use (e.g. {B, G, O, P, R, Y}).
    Set<Character> getAlphabet();
}
