package assignment2;

import assignment2.core.ConsoleUserInterface;
import assignment2.core.UserInterface;
import assignment2.mastermind.MastermindConfiguration;
import assignment2.mastermind.MastermindGame;

/**
 * Required launch point:
 *   java assignment2.Driver mastermind [test]
 *
 * Driver's only job is to parse the command line and start the
 * requested game. Adding a second game in Phase II means adding one
 * more branch here and one more game-specific package; it does not
 * require any changes to assignment2.core.
 */
public class Driver {
    public static void main(String[] args) {
        // Need at least a game name to know what to launch.
        if (args.length < 1) {
            System.out.println("Usage: java assignment2.Driver <gameName> [test]");
            System.out.println("Supported games: mastermind");
            return;
        }

        // args[0] is the game name (case-insensitive); args[1], if
        // present and equal to "test", turns on testing mode.
        String gameName = args[0].trim().toLowerCase();
        boolean testMode = args.length > 1 && args[1].trim().equalsIgnoreCase("test");

        // Console I/O implementation shared by whichever game gets started.
        UserInterface ui = new ConsoleUserInterface();

        switch (gameName) {
            case "mastermind":
                // Build Mastermind's default configuration and hand off
                // control to its session driver.
                MastermindConfiguration config = new MastermindConfiguration();
                new MastermindGame(config, ui, testMode).run();
                break;
            default:
                // Unrecognized game name -- fail politely instead of
                // guessing what the user meant.
                System.out.println("Unknown game: " + gameName);
                System.out.println("Supported games: mastermind");
        }
    }
}
