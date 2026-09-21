package assignment2.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordered record of every valid guess made during a round, and the
 * feedback it received. Only valid guesses are recorded, which is
 * exactly what the HISTORY command needs to display.
 */
public class GuessHistory {
    // Guesses in the order they were made; attempt numbers are
    // derived from position (records.size() + 1) rather than stored
    // separately, so they can never get out of sync.
    private final List<GuessRecord> records = new ArrayList<>();

    // Called once per valid guess, right after it's been scored.
    public void record(Code guess, Feedback feedback) {
        records.add(new GuessRecord(records.size() + 1, guess, feedback));
    }

    // Read-only view of every guess made so far, in order -- used by HISTORY.
    public List<GuessRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    // Handy for the engine to check "has anything been guessed yet?"
    public boolean isEmpty() {
        return records.isEmpty();
    }
}
