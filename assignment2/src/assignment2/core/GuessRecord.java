package assignment2.core;

/** A single recorded (valid) guess and the feedback it received. */
public final class GuessRecord {
    private final int attemptNumber; // 1-based: "this was attempt #N"
    private final Code guess;        // what the player actually guessed
    private final Feedback feedback; // how that guess scored

    public GuessRecord(int attemptNumber, Code guess, Feedback feedback) {
        this.attemptNumber = attemptNumber;
        this.guess = guess;
        this.feedback = feedback;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public Code getGuess() {
        return guess;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    // Used by the HISTORY command, e.g. "1. BGOR -> 2B_0W".
    @Override
    public String toString() {
        return attemptNumber + ". " + guess + " -> " + feedback.toDisplayString();
    }
}
