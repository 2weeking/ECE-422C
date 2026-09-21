package assignment2.core;

/**
 * Game-independent turn loop. It knows nothing about pegs, colors, or
 * Mastermind's rules -- only that a round consists of:
 *   1. a secret Code, produced by a SecretGenerator;
 *   2. up to config.getMaxAttempts() guesses;
 *   3. a GuessValidator that turns raw input into a Code (or rejects it
 *      without consuming an attempt);
 *   4. a GuessEvaluator that turns (secret, guess) into Feedback;
 *   5. Feedback.isWinningFeedback() to know when the player has won.
 *
 * It also understands two console commands that are useful for *any*
 * such game and are therefore handled here rather than duplicated per
 * game: HISTORY (show past guesses) and, when running in test mode,
 * REVEAL (show the secret).
 *
 * A future guessing game reuses this class completely unchanged by
 * supplying its own GameConfiguration, GuessValidator, and
 * GuessEvaluator implementations.
 */
public class GuessingGameEngine {
    // Everything this engine needs is handed in through the
    // constructor as an interface -- the engine itself never "knows"
    // which concrete implementation it's talking to.
    private final GameConfiguration config;
    private final GuessValidator validator;
    private final GuessEvaluator evaluator;
    private final SecretGenerator secretGenerator;
    private final UserInterface ui;
    private final boolean testMode;

    // Tracks every valid guess made so far this round, for the HISTORY command.
    private final GuessHistory history = new GuessHistory();

    public GuessingGameEngine(GameConfiguration config,
                               GuessValidator validator,
                               GuessEvaluator evaluator,
                               SecretGenerator secretGenerator,
                               UserInterface ui,
                               boolean testMode) {
        this.config = config;
        this.validator = validator;
        this.evaluator = evaluator;
        this.secretGenerator = secretGenerator;
        this.ui = ui;
        this.testMode = testMode;
    }

    /**
     * Runs one full round: generates a secret, loops on guesses until
     * the player wins or runs out of attempts, and reports the result.
     * Returns true if the player won.
     */
    public boolean playOneGame() {
        // Ask the injected generator for this round's secret -- the
        // engine doesn't care whether it's random or fixed.
        Code secret = secretGenerator.generate(config);

        // In test mode, show the secret immediately so a tester
        // doesn't have to guess blind while debugging.
        if (testMode) {
            ui.display("[TEST MODE] Secret code is: " + secret);
            ui.display("");
        }

        int attemptsUsed = 0;
        boolean won = false;

        // Main loop: keep prompting until the player wins or runs out of attempts.
        while (attemptsUsed < config.getMaxAttempts() && !won) {
            String prompt = "Attempt " + (attemptsUsed + 1) + "/" + config.getMaxAttempts()
                    + " - enter your guess: ";
            String raw = ui.promptInput(prompt);
            // Normalize once so command-checking below isn't case-sensitive.
            String command = raw == null ? "" : raw.trim().toUpperCase();

            // "HISTORY" is a special command, not a guess -- show past
            // guesses and go straight back to the prompt without
            // touching attemptsUsed.
            if (command.equals("HISTORY")) {
                showHistory();
                ui.display("");
                continue;
            }
            // "REVEAL" only works in test mode, and likewise doesn't cost an attempt.
            if (testMode && command.equals("REVEAL")) {
                ui.display("[TEST MODE] Secret code is: " + secret);
                ui.display("");
                continue;
            }

            // Not a special command -- try to parse it as an actual guess.
            ValidationResult result = validator.validate(raw, config);
            if (!result.isValid()) {
                // Bad input (wrong length, illegal symbol, etc.): tell
                // the player why and loop back WITHOUT consuming an attempt.
                ui.display("Invalid guess: " + result.getErrorMessage());
                ui.display("");
                continue;
            }

            // Guess was valid -- this is the point where an attempt is
            // actually spent.
            Code guess = result.getCode();
            Feedback feedback = evaluator.evaluate(secret, guess);
            history.record(guess, feedback);
            attemptsUsed++;

            ui.display("Feedback: " + feedback.toDisplayString());

            // Blank line for readability: visually separates this guess's
            // feedback from the next "Attempt N/M" prompt below it.
            ui.display("");

            if (feedback.isWinningFeedback()) {
                won = true;
            }
        }

        // Loop has ended either because the player won or ran out of attempts.
        if (won) {
            ui.display("You won! The code was " + secret + ". Attempts used: " + attemptsUsed
                    + "/" + config.getMaxAttempts() + ".");
            ui.display("");
        } else {
            // Reveal the secret on a loss so the player can see what they missed.
            ui.display("You lost. Out of guesses. The secret code was " + secret + ".");
            ui.display("");
        }
        return won;
    }

    // Prints every recorded guess and its feedback, in order, for the HISTORY command.
    private void showHistory() {
        if (history.isEmpty()) {
            ui.display("No guesses yet.");
            return;
        }
        ui.display("---- History ----");
        for (GuessRecord record : history.getRecords()) {
            ui.display(record.toString());
        }
        ui.display("-----------------");
    }
}
