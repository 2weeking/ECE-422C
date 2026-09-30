package assignment2.mastermind;

import assignment2.core.DefaultGuessValidator;
import assignment2.core.FixedSecretGenerator;
import assignment2.core.GameSession;
import assignment2.core.RandomSecretGenerator;
import assignment2.core.SecretGenerator;
import assignment2.core.UserInterface;
import assignment2.core.ValidationResult;

/**
 * Mastermind-specific welcome and secret choice; GameSession owns replay.
 */
public class MastermindGame {
    private final MastermindConfiguration config;
    private final UserInterface ui;
    private final boolean testMode;

    public MastermindGame(MastermindConfiguration config, UserInterface ui, boolean testMode) {
        this.config = config;
        this.ui = ui;
        this.testMode = testMode;
    }

    // Print the game-specific introduction, then delegate replay to GameSession.
    public void run() {
        ui.display("");
        ui.display("Welcome to Mastermind!");
        ui.display("Guess the secret code of " + config.getCodeLength()
                + " pegs using colors " + config.getAlphabet() + ".");
        ui.display("You have " + config.getMaxAttempts()
                + " attempts. Type HISTORY at any time to see past guesses.");
        if (testMode) {
            ui.display("[TEST MODE] You may fix the secret code each round and type REVEAL during play.");
        }

        new GameSession(config, new DefaultGuessValidator(), new MastermindEvaluator(),
                this::chooseSecretGenerator, ui, testMode, "code")
                .run("Thanks for playing Mastermind!");
    }

    /**
     * In test mode, lets the player type a fixed secret (or press Enter
     * for a random one). Outside test mode, always plays randomly.
     */
    private SecretGenerator chooseSecretGenerator() {
        // Normal play: always random, no prompt needed.
        if (!testMode) {
            return new RandomSecretGenerator();
        }

        // Test mode: offer to fix the secret for this round.
        String raw = ui.promptInput(
                "[TEST MODE] Enter a fixed secret code (" + config.getCodeLength()
                        + " symbols from " + config.getAlphabet() + "), or press Enter for random: ");
        if (raw == null || raw.trim().isEmpty()) {
            // Blank input means "just give me a random one".
            return new RandomSecretGenerator();
        }

        // Re-use the same validator guesses go through, so a fixed
        // secret has to obey the same length/alphabet rules as a guess.
        ValidationResult result = new DefaultGuessValidator().validate(raw, config);
        if (!result.isValid()) {
            ui.display("Invalid fixed secret (" + result.getErrorMessage() + "); using a random secret instead.");
            return new RandomSecretGenerator();
        }
        return new FixedSecretGenerator(result.getCode());
    }
}
