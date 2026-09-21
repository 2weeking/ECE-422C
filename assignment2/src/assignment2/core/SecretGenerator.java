package assignment2.core;

/**
 * Produces the secret Code for a round. Kept separate from the game
 * engine so the *source* of the secret (random, fixed for testing,
 * read from a file, etc.) can vary independently of how a round is
 * played.
 */
public interface SecretGenerator {
    // Build (or look up) the secret Code that the player will try to guess this round.
    Code generate(GameConfiguration config);
}
