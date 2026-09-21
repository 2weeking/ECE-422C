package assignment2.mastermind;

import assignment2.core.Feedback;

/**
 * Mastermind-specific feedback: counts of black pegs (right color,
 * right position) and white pegs (right color, wrong position),
 * displayed as "nB_mW". This display convention and the black/white
 * peg concept are entirely Mastermind's own; a different game would
 * implement Feedback differently while GuessingGameEngine, which only
 * calls isWinningFeedback() and toDisplayString(), stays unchanged.
 */
public class MastermindFeedback implements Feedback {
    private final int blackPegs;  // right color, right position
    private final int whitePegs;  // right color, wrong position
    private final int codeLength; // needed to know what "all black" (a win) looks like

    public MastermindFeedback(int blackPegs, int whitePegs, int codeLength) {
        this.blackPegs = blackPegs;
        this.whitePegs = whitePegs;
        this.codeLength = codeLength;
    }

    //a
    public int getBlackPegs() {
        return blackPegs;
    }

    public int getWhitePegs() {
        return whitePegs;
    }

    // The player wins exactly when every peg is a black (exact) match.
    @Override
    public boolean isWinningFeedback() {
        return blackPegs == codeLength;
    }

    // The required console format, e.g. "2B_1W".
    @Override
    public String toDisplayString() {
        return blackPegs + "B_" + whitePegs + "W";
    }
}
