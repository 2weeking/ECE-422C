package assignment2.mastermind;

import assignment2.core.Code;
import assignment2.core.Feedback;
import assignment2.core.GuessEvaluator;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements Mastermind's peg-matching rule:
 *   1. Exact-position matches (black pegs) are resolved first and
 *      consume both the secret peg and the guess peg at that position.
 *   2. Remaining, unmatched pegs are then matched by color only
 *      (white pegs); each remaining secret peg and each remaining
 *      guess peg contributes to feedback at most once.
 *
 * This two-pass, "exact matches take precedence, then count remaining
 * colors" algorithm is exactly what the assignment's duplicate-color
 * rule requires.
 */
public class MastermindEvaluator implements GuessEvaluator {

    @Override
    public Feedback evaluate(Code secret, Code guess) {
        if (secret.length() != guess.length()) {
            throw new IllegalArgumentException("Secret and guess must be the same length.");
        }
        int length = secret.length();

        // Tracks which positions have already been "claimed" by an
        // exact match, so pass 2 knows to skip them.
        boolean[] secretUsed = new boolean[length];
        boolean[] guessUsed = new boolean[length];

        // ---- Pass 1: exact matches take precedence over color-only matches. ----
        int blackPegs = 0;
        for (int i = 0; i < length; i++) {
            if (secret.symbolAt(i) == guess.symbolAt(i)) {
                blackPegs++;
                secretUsed[i] = true; // this secret peg is spoken for
                guessUsed[i] = true;  // this guess peg is spoken for
            }
        }

        // Count how many of each color are left in the secret, now that
        // exact-match pegs have been removed from consideration.
        Map<Character, Integer> remainingSecretColorCounts = new HashMap<>();
        for (int i = 0; i < length; i++) {
            if (!secretUsed[i]) {
                char c = secret.symbolAt(i);
                remainingSecretColorCounts.merge(c, 1, Integer::sum);
            }
        }

        // ---- Pass 2: each remaining guess peg claims at most one ----
        // ---- remaining secret peg of the same color.               ----
        int whitePegs = 0;
        for (int i = 0; i < length; i++) {
            if (guessUsed[i]) {
                continue; // already counted as black in pass 1
            }
            char c = guess.symbolAt(i);
            Integer remaining = remainingSecretColorCounts.get(c);
            if (remaining != null && remaining > 0) {
                whitePegs++;
                // "Use up" one of the remaining secret pegs of this
                // color so it can't be double-counted by a later guess peg.
                remainingSecretColorCounts.put(c, remaining - 1);
            }
        }

        return new MastermindFeedback(blackPegs, whitePegs, length);
    }
}
