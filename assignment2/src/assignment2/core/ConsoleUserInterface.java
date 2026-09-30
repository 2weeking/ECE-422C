package assignment2.core;

import java.util.Scanner;

/** Reads from System.in and writes to System.out. */
public class ConsoleUserInterface implements UserInterface {
    // One Scanner reused for the whole session, wrapping standard input.
    private final Scanner scanner;

    public ConsoleUserInterface() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void display(String message) {
        System.out.println(message);
    }

    @Override
    public String promptInput(String prompt) {
        // Print the prompt without a trailing newline so the player's
        // typed answer appears right after it on the same line.
        System.out.print(prompt);

        // Distinguish exhausted input from a blank line, so a scripted
        // session cannot loop forever after standard input closes.
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }
}
