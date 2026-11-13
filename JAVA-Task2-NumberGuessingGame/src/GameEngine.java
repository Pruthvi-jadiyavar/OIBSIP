import java.util.*;

/**
 * Core game logic for the Swing GUI front-end.
 *
 * Handles: random number generation, guess evaluation, attempt limits,
 * round numbering, session history and persistent best scores.
 */
public class GameEngine {

    /** Difficulty levels: Easy (1-50, 10 attempts), Medium (1-100, 7), Hard (1-200, 5). */
    public enum Difficulty {
        EASY("Easy", 1, 50, 10),
        MEDIUM("Medium", 1, 100, 7),
        HARD("Hard", 1, 200, 5);

        public final String label;
        public final int min;
        public final int max;
        public final int maxAttempts;

        Difficulty(String label, int min, int max, int maxAttempts) {
            this.label = label;
            this.min = min;
            this.max = max;
            this.maxAttempts = maxAttempts;
        }

        @Override
        public String toString() {
            return label + " (" + min + "-" + max + ", " + maxAttempts + " attempts)";
        }
    }

    /** Result of a single guess. A round also ends when attempts run out. */
    public enum Result {
        TOO_LOW,
        TOO_HIGH,
        CORRECT
    }

    /** One finished round, used for the end-of-session summary. */
    public record RoundSummary(int round, Difficulty difficulty, boolean won,
                               int attempts, int secret) {
        @Override
        public String toString() {
            if (won) {
                return String.format("Round %d - guessed in %d %s (%s)",
                        round, attempts, attempts == 1 ? "attempt" : "attempts", difficulty.label);
            }
            return String.format("Round %d - LOST! The number was %d (%s)",
                    round, secret, difficulty.label);
        }
    }

    private final Random random = new Random();
    private final ScoreStore scoreStore;
    private final List<RoundSummary> history = new ArrayList<>();

    private Difficulty difficulty = Difficulty.MEDIUM;
    private int secret;
    private int attemptsUsed;
    private int roundNumber;
    private boolean roundActive;

    public GameEngine() {
        this(new ScoreStore());
    }

    public GameEngine(ScoreStore scoreStore) {
        this.scoreStore = scoreStore;
    }

    /** Starts a new round with the given difficulty. */
    public void startRound(Difficulty d) {
        this.difficulty = d;
        this.secret = random.nextInt(d.max - d.min + 1) + d.min;
        this.attemptsUsed = 0;
        this.roundNumber++;
        this.roundActive = true;
    }

    /**
     * Evaluates a guess. Consumes one attempt.
     * Throws IllegalStateException if no round is active and
     * IllegalArgumentException if the guess is out of range.
     */
    public Result guess(int value) {
        if (!roundActive) {
            throw new IllegalStateException("No round in progress - start a new round first.");
        }
        if (value < difficulty.min || value > difficulty.max) {
            throw new IllegalArgumentException(
                    "Guess out of range: must be between " + difficulty.min + " and " + difficulty.max);
        }

        attemptsUsed++;
        boolean won = (value == secret);
        Result result = won ? Result.CORRECT : (value < secret ? Result.TOO_LOW : Result.TOO_HIGH);
        boolean outOfAttempts = attemptsUsed >= difficulty.maxAttempts;

        if (won || outOfAttempts) {
            endRound(won);
        }
        return result;
    }

    private void endRound(boolean won) {
        roundActive = false;
        history.add(new RoundSummary(roundNumber, difficulty, won, attemptsUsed, secret));
        if (won) {
            scoreStore.recordIfBest(difficulty, attemptsUsed);
        }
    }

    public boolean isRoundActive() { return roundActive; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getSecret() { return secret; }
    public int getAttemptsUsed() { return attemptsUsed; }
    public int getAttemptsRemaining() { return difficulty.maxAttempts - attemptsUsed; }
    public int getMaxAttempts() { return difficulty.maxAttempts; }
    public int getMin() { return difficulty.min; }
    public int getMax() { return difficulty.max; }
    public int getRoundNumber() { return roundNumber; }

    /** All finished rounds this session, in order. */
    public List<RoundSummary> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /** Best (fewest) attempts ever recorded per difficulty, or null if never won. */
    public Integer getBestScore(Difficulty d) {
        return scoreStore.getBest(d);
    }

    /** True if the last recorded win set a new best for that difficulty. */
    public boolean wasLastWinARecord(Difficulty d, int attempts) {
        Integer best = scoreStore.getBest(d);
        return best != null && best == attempts;
    }

    public ScoreStore getScoreStore() { return scoreStore; }
}
