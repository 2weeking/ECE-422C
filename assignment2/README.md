# Assignment 2, Phase I — Mastermind

## Build & run

```
javac -d out $(find src -name "*.java")
cd out
java assignment2.Driver mastermind
java assignment2.Driver mastermind test
```

## Package layout

```
assignment2/
  Driver.java                     <- required launch point only

  core/                           <- game-independent infrastructure
    Code.java                     <- immutable symbol sequence (secret or guess)
    GameConfiguration.java        <- interface: max attempts, code length, alphabet
    SecretGenerator.java          <- interface for producing a secret
    RandomSecretGenerator.java    <- random secret (any game/config)
    FixedSecretGenerator.java     <- deterministic secret (testing mode)
    GuessValidator.java           <- interface for turning input into a Code
    DefaultGuessValidator.java    <- generic length/alphabet validator
    ValidationResult.java         <- success/failure + parsed Code or error text
    Feedback.java                 <- interface: win check + display string
    GuessEvaluator.java           <- interface: secret+guess -> Feedback
    GuessRecord.java / GuessHistory.java  <- ordered record of valid guesses
    UserInterface.java / ConsoleUserInterface.java  <- I/O abstraction
    GuessingGameEngine.java       <- the turn loop (HISTORY, REVEAL, win/loss)

  mastermind/                     <- Mastermind-specific rules
    MastermindConfiguration.java  <- defaults: 4 pegs, 12 attempts, B G O P R Y
    MastermindFeedback.java       <- nB_mW black/white peg feedback
    MastermindEvaluator.java      <- two-pass peg-matching algorithm
    MastermindGame.java           <- wires engine + rules, handles replay & test-mode secret entry
```

## Why it's split this way

Everything under `core/` only knows about `GameConfiguration`, `Code`,
`Feedback`, and the other interfaces — it has no idea what a "peg" or
a "color" is. A Phase II game with the same shape (a hidden sequence,
guessed within a bounded number of attempts) can reuse every class in
`core/` unchanged and only needs to write its own version of the four
Mastermind-specific classes: a `GameConfiguration`, a `Feedback`, a
`GuessEvaluator`, and a small session-driver class like
`MastermindGame`.

`GuessingGameEngine` also owns the two console behaviors that make
sense for *any* such game — the `HISTORY` command and, in test mode,
`REVEAL` — so a future game gets those for free instead of
reimplementing them.

## Testing mode

`java assignment2.Driver mastermind test` adds two things without
changing any game logic:
- Before each round, you can type a fixed secret code (or press Enter
  for a random one) — this is the "deterministically control the
  secret" requirement.
- During play, typing `REVEAL` prints the current secret without
  consuming an attempt — this is the "reveal the secret" requirement.

## Verified behavior (see chat for full transcripts)

- Duplicate-color feedback (e.g. secret `RGRY` vs guess `RRRR` → `2B_0W`,
  never double-counting a peg) matches the standard Mastermind
  algorithm: exact matches are removed first, then remaining pegs are
  matched by color at most once each.
- Invalid guesses (wrong length, illegal symbol) are rejected and do
  not consume an attempt.
- `HISTORY` displays all valid guesses in order and does not consume
  an attempt.
- Win and loss are both reported correctly, including revealing the
  secret on a loss.
- A changed configuration (5 pegs, a 3-symbol alphabet, 6 attempts)
  runs correctly with zero changes to `GuessingGameEngine`,
  `DefaultGuessValidator`, or either secret generator — only a
  different `MastermindConfiguration` was constructed.

## Still needed for submission

This zip contains only the source code. The assignment also requires:
- `Design.pdf` (class diagram + responsibilities + reuse plan)
- `Tests.pdf` (at least 8 described tests)
- `AI.pdf` (your AI tool usage and one suggestion you changed or rejected)

I'm happy to help draft any of these — just ask.
