package assignment2;

import assignment2.core.ConsoleUserInterface;
import assignment2.core.UserInterface;
import assignment2.mastermind.MastermindConfiguration;
import assignment2.mastermind.MastermindGame;
import assignment2.wordle.WordleConfiguration;
import assignment2.wordle.WordleGame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Required launch point:
 *   From the compiled out directory:
 *   java assignment2.Driver mastermind [test]
 *   java assignment2.Driver wordle [test [dictionary-path]]
 *
 * Driver's only job is to parse the command line and start the
 * requested game.
 */
public class Driver {
    public static void main(String[] args) {
        // Need at least a game name to know what to launch.
        if (args.length < 1 || args.length > 3) {
            usage();
            return;
        }

        // args[0] is the game name (case-insensitive); args[1], if
        // present and equal to "test", turns on testing mode.
        String gameName = args[0].trim().toLowerCase(Locale.ROOT);
        boolean testMode = args.length > 1 && args[1].trim().equalsIgnoreCase("test");

        // Console I/O implementation shared by whichever game gets started.
        UserInterface ui = new ConsoleUserInterface();

        if (gameName.equals("mastermind") && (args.length == 1 || args.length == 2 && testMode)) {
                // Build Mastermind's default configuration and hand off
                // control to its session driver.
                MastermindConfiguration config = new MastermindConfiguration();
                new MastermindGame(config, ui, testMode).run();
        } else if (gameName.equals("wordle") && (args.length == 1 || testMode && args.length <= 3)) {
            Path words = args.length == 3 ? Path.of(args[2]) : defaultWordList();
            try {
                new WordleGame(WordleConfiguration.fromFile(words), ui, testMode).run();
            } catch (IOException | IllegalArgumentException ex) {
                System.err.println("Unable to load word list '" + words + "': " + ex.getMessage());
            }
        } else {
            usage();
        }
    }

    /** The dictionary remains external, next to the out directory in the project. */
    private static Path defaultWordList() {
        Path inProject = Path.of("data", "words.txt");
        if (Files.isRegularFile(inProject)) return inProject;
        Path fromOut = Path.of("..", "data", "words.txt");
        if (Files.isRegularFile(fromOut)) return fromOut;
        return inProject; // preserve a useful missing-file error
    }

    private static void usage() {
        System.out.println("From the compiled out directory:");
        System.out.println("Usage: java assignment2.Driver mastermind [test]");
        System.out.println("       java assignment2.Driver wordle [test [dictionary-path]]");
    }
}
