package assignment2.core;

/**
 * Result of comparing a guess to the secret. Deliberately abstract:
 * Mastermind's black/white peg counts are one possible implementation,
 * but a different guessing game (e.g. a Wordle-style letter game)
 * would implement this differently while GuessingGameEngine, which
 * only calls these two methods, stays unchanged.
 */
public interface Feedback {
    // Did this guess match the secret well enough to end the game as a win?
    boolean isWinningFeedback();

    // How this feedback should be printed to the player (e.g. "2B_1W").
    String toDisplayString();
}