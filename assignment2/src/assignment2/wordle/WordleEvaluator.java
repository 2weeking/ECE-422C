package assignment2.wordle;

import assignment2.core.Code;
import assignment2.core.Feedback;
import assignment2.core.GuessEvaluator;
import java.util.HashMap;
import java.util.Map;
import static assignment2.wordle.WordleFeedback.Mark.*;

/** Two passes ensure exact matches reserve their secret letters before present matches. */
public final class WordleEvaluator implements GuessEvaluator {
    @Override public Feedback evaluate(Code secret, Code guess) {
        if (secret.length() != guess.length()) {
            throw new IllegalArgumentException("Secret and guess must have the same length.");
        }
        WordleFeedback.Mark[] marks = new WordleFeedback.Mark[guess.length()];
        Map<Character, Integer> remaining = new HashMap<>();
        for (int i = 0; i < guess.length(); i++) {
            if (secret.symbolAt(i) == guess.symbolAt(i)) marks[i] = CORRECT;
            else remaining.merge(secret.symbolAt(i), 1, Integer::sum);
        }
        for (int i = 0; i < guess.length(); i++) {
            if (marks[i] == CORRECT) continue;
            char letter = guess.symbolAt(i);
            int available = remaining.getOrDefault(letter, 0);
            if (available > 0) {
                marks[i] = PRESENT;
                remaining.put(letter, available - 1);
            } else marks[i] = ABSENT;
        }
        return new WordleFeedback(marks);
    }
}
