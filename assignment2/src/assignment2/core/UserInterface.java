package assignment2.core;

/**
 * Abstraction over how the game talks to the player. Console is the
 * only implementation this assignment needs, but isolating it behind
 * an interface keeps GuessingGameEngine testable (a test can supply a
 * fake UserInterface that feeds scripted input and captures output)
 * without launching a real process or touching System.in.
 */
public interface UserInterface {
    // Print a line of output to the player.
    void display(String message);

    // Show a prompt, then read and return one line the player typed.
    String promptInput(String prompt);
}
