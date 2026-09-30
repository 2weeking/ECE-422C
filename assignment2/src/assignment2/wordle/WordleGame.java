package assignment2.wordle;

import assignment2.core.FixedSecretGenerator;
import assignment2.core.GameSession;
import assignment2.core.SecretGenerator;
import assignment2.core.UserInterface;
import assignment2.core.ValidationResult;

/** Game-specific introduction and test-mode secret selection. */
public final class WordleGame {
    private final WordleConfiguration config;
    private final UserInterface ui;
    private final boolean testMode;

    public WordleGame(WordleConfiguration config, UserInterface ui, boolean testMode) {
        this.config = config;
        this.ui = ui;
        this.testMode = testMode;
    }

    public void run() {
        ui.display("Welcome to Wordle!");
        ui.display("Guess a " + config.getCodeLength() + "-letter word in " + config.getMaxAttempts()
                + " valid guesses. Type HISTORY to see prior guesses.");
        ui.display("Feedback: C = correct position, P = present elsewhere, A = absent.");
        if (testMode) ui.display("[TEST MODE] Fix a secret each round or press Enter for random. REVEAL is available.");
        new GameSession(config, new WordleValidator(), new WordleEvaluator(),
                this::chooseSecret, ui, testMode, "word").run("Thanks for playing Wordle!");
    }

    private SecretGenerator chooseSecret() {
        if (!testMode) return new WordleSecretGenerator();
        String raw = ui.promptInput("[TEST MODE] Enter a fixed allowed word, or press Enter for random: ");
        if (raw == null || raw.trim().isEmpty()) return new WordleSecretGenerator();
        ValidationResult checked = new WordleValidator().validate(raw, config);
        if (!checked.isValid()) {
            ui.display("Invalid fixed secret (" + checked.getErrorMessage() + "); using a random word instead.");
            return new WordleSecretGenerator();
        }
        return new FixedSecretGenerator(checked.getCode());
    }
}
