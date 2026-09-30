package assignment2;

import assignment2.core.*;
import assignment2.mastermind.*;
import assignment2.wordle.*;
import java.nio.file.Path;
import java.util.*;

/** Standalone regression suite. Run with java -cp out assignment2.Phase2Tests. */
public final class Phase2Tests {
    private static int passed;
    private static final WordleEvaluator wordle = new WordleEvaluator();

    private static void check(String name, boolean result) {
        if (!result) throw new AssertionError(name);
        System.out.println("PASS " + name);
        passed++;
    }

    private static String marks(String secret, String guess) {
        return wordle.evaluate(Code.fromString(secret), Code.fromString(guess)).toDisplayString();
    }

    private static final class ScriptedUI implements UserInterface {
        private final Deque<String> input = new ArrayDeque<>();
        private final StringBuilder output = new StringBuilder();
        ScriptedUI(String... lines) { input.addAll(Arrays.asList(lines)); }
        @Override public void display(String line) { output.append(line).append('\n'); }
        @Override public String promptInput(String prompt) {
            output.append(prompt).append('\n');
            return input.pollFirst();
        }
        String transcript() { return output.toString(); }
    }

    public static void main(String[] args) throws Exception {
        WordleConfiguration config = WordleConfiguration.fromFile(Path.of("data/words.txt"));
        WordleValidator validator = new WordleValidator();
        check("W01 exact win", marks("APPLE", "APPLE").equals("C C C C C")
                && wordle.evaluate(Code.fromString("APPLE"), Code.fromString("APPLE")).isWinningFeedback());
        check("W02 letter exceeds secret count", marks("APPLE", "ALLEY").equals("C P A P A"));
        check("W03 correct A consumes extra A", marks("APPLE", "ALARM").equals("C P A A A"));
        check("W04 exact P reserved first", marks("APPLE", "PAPAL").equals("P P C A P"));
        check("W05 duplicate A matched twice at most", marks("BANAL", "LLAMA").equals("P A P A P"));
        check("W06 no matches", marks("APPLE", "GHOST").equals("A A A A A"));
        check("W07 alphabetic and length", !validator.validate("A1PLE", config).isValid()
                && !validator.validate("APP", config).isValid());
        check("W08 dictionary and normalization", !validator.validate("ZZZZZ", config).isValid()
                && validator.validate(" apple ", config).getCode().toString().equals("APPLE"));
        check("W09 random secret is a dictionary word", config.getWords().contains(
                new WordleSecretGenerator(new Random(17)).generate(config).toString()));
        check("W10 missing/invalid dictionary rejected", rejectsDictionary(Collections.emptyList())
                && rejectsDictionary(Arrays.asList("APPLE", "TOO")));

        ScriptedUI flow = new ScriptedUI("AA", "A1PLE", "ZZZZZ", "ALLEY", "HISTORY", "REVEAL", "apple");
        boolean win = new GuessingGameEngine(config, validator, wordle,
                new FixedSecretGenerator(Code.fromString("APPLE")), flow, true, "word").playOneGame();
        String transcript = flow.transcript();
        check("W11 invalid guesses and commands cost no turn", win
                && count(transcript, "Attempt 1/6") == 4 && count(transcript, "Attempt 2/6") == 3);
        check("W12 history records valid guesses with positional marks", transcript.contains("1. ALLEY -> C P A P A")
                && !transcript.contains("1. ZZZZZ") && transcript.contains("Secret word is: APPLE"));
        check("W13 six valid guesses lose and reveal word", lossScenario(config));
        ScriptedUI session = new ScriptedUI("APPLE", "apple", "Y", "BANAL", "banal", "N");
        new WordleGame(config, session, true).run();
        check("W14 replay resets attempts and chooses new fixed word", count(session.transcript(), "You won!") == 2
                && session.transcript().contains("The word was BANAL")
                && count(session.transcript(), "Attempt 1/6") == 2);

        MastermindConfiguration mm = new MastermindConfiguration();
        MastermindEvaluator mmEval = new MastermindEvaluator();
        check("M01 duplicate pegs counted once", mmEval.evaluate(Code.fromString("AABB"),
                Code.fromString("AAAA")).toDisplayString().equals("2B_0W"));
        check("M02 four white pegs", mmEval.evaluate(Code.fromString("BGOR"),
                Code.fromString("RBGO")).toDisplayString().equals("0B_4W"));
        check("M03 Mastermind validation", !new DefaultGuessValidator().validate("BG", mm).isValid()
                && !new DefaultGuessValidator().validate("BGOZ", mm).isValid());
        ScriptedUI mmFlow = new ScriptedUI("ZZZZ", "BGOR", "HISTORY", "PYYY");
        check("M04 valid-only history and attempt accounting", new GuessingGameEngine(mm,
                new DefaultGuessValidator(), mmEval, new FixedSecretGenerator(Code.fromString("PYYY")),
                mmFlow, false).playOneGame() && mmFlow.transcript().contains("1. BGOR -> 0B_0W")
                && count(mmFlow.transcript(), "Attempt 1/12") == 2
                && count(mmFlow.transcript(), "Attempt 2/12") == 2);
        ScriptedUI mmSession = new ScriptedUI("BGOR", "BGOR", "Y", "PYYY", "PYYY", "N");
        new MastermindGame(mm, mmSession, true).run();
        check("M05 Mastermind replays", count(mmSession.transcript(), "You won!") == 2);
        ScriptedUI mmLoss = new ScriptedUI("YYYY", "YYYY", "YYYY", "YYYY", "YYYY", "YYYY",
                "YYYY", "YYYY", "YYYY", "YYYY", "YYYY", "YYYY");
        check("M06 Mastermind loss limit", !new GuessingGameEngine(mm, new DefaultGuessValidator(),
                mmEval, new FixedSecretGenerator(Code.fromString("BGOR")), mmLoss, false).playOneGame()
                && mmLoss.transcript().contains("Attempt 12/12")
                && mmLoss.transcript().contains("secret code was BGOR"));
        System.out.println("TOTAL " + passed + " / " + passed + " passed");
    }

    private static boolean rejectsDictionary(List<String> words) {
        try { new WordleConfiguration(6, 5, words); return false; }
        catch (IllegalArgumentException expected) { return true; }
    }

    private static boolean lossScenario(WordleConfiguration config) {
        ScriptedUI ui = new ScriptedUI("GHOST", "GHOST", "GHOST", "GHOST", "GHOST", "GHOST");
        return !new GuessingGameEngine(config, new WordleValidator(), new WordleEvaluator(),
                new FixedSecretGenerator(Code.fromString("APPLE")), ui, false, "word").playOneGame()
                && ui.transcript().contains("The secret word was APPLE")
                && count(ui.transcript(), "Attempt 6/6") == 1;
    }

    private static int count(String text, String needle) {
        int found = 0, offset = 0;
        while ((offset = text.indexOf(needle, offset)) >= 0) { found++; offset += needle.length(); }
        return found;
    }
}
