package assignment2.wordle;

import assignment2.core.Feedback;

/** Positional Wordle marks; C exact, P present elsewhere, A absent. */
public final class WordleFeedback implements Feedback {
    public enum Mark { CORRECT('C'), PRESENT('P'), ABSENT('A');
        private final char letter;
        Mark(char letter) { this.letter = letter; }
        public char letter() { return letter; }
    }
    private final Mark[] marks;
    public WordleFeedback(Mark[] marks) { this.marks = marks.clone(); }
    public Mark[] getMarks() { return marks.clone(); }
    @Override public boolean isWinningFeedback() {
        for (Mark mark : marks) if (mark != Mark.CORRECT) return false;
        return true;
    }
    @Override public String toDisplayString() {
        StringBuilder result = new StringBuilder();
        for (Mark mark : marks) {
            if (result.length() != 0) result.append(' ');
            result.append(mark.letter());
        }
        return result.toString();
    }
}
