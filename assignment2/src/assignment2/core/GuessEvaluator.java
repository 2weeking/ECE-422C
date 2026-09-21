package assignment2.core;

/** Compares a guess against the secret and produces Feedback. */
public interface GuessEvaluator {
    // Score one guess against the secret and return the resulting Feedback.
    Feedback evaluate(Code secret, Code guess);
}
