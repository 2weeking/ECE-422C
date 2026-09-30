package assignment2.core;

import java.util.function.Supplier;

/** Shared replay loop; each game supplies its own rules and secret selection. */
public final class GameSession {
    private final GameConfiguration config;
    private final GuessValidator validator;
    private final GuessEvaluator evaluator;
    private final Supplier<SecretGenerator> selectSecret;
    private final UserInterface ui;
    private final boolean testMode;
    private final String secretName;

    public GameSession(GameConfiguration config, GuessValidator validator,
                       GuessEvaluator evaluator, Supplier<SecretGenerator> selectSecret,
                       UserInterface ui, boolean testMode, String secretName) {
        this.config = config;
        this.validator = validator;
        this.evaluator = evaluator;
        this.selectSecret = selectSecret;
        this.ui = ui;
        this.testMode = testMode;
        this.secretName = secretName;
    }

    public void run(String farewell) {
        do {
            new GuessingGameEngine(config, validator, evaluator, selectSecret.get(),
                    ui, testMode, secretName).playOneGame();
            String answer = ui.promptInput("Play again? (Y/N): ");
            if (answer == null || !answer.trim().equalsIgnoreCase("Y")) {
                break;
            }
        } while (true);
        ui.display(farewell);
    }
}
