import java.io.*;
import java.util.Properties;

/**
 * Persists best (fewest) attempts per difficulty to a small properties file,
 * so records survive between sessions.
 *
 * The file is stored as "number-guessing-scores.properties" in the current
 * working directory when writable, otherwise in the system temp directory.
 * All I/O failures are swallowed gracefully - scoring is a bonus feature,
 * the game must always remain playable.
 */
public class ScoreStore {

    private static final String FILE_NAME = "number-guessing-scores.properties";

    private final File file;
    private final Properties props = new Properties();

    public ScoreStore() {
        this(resolveFile());
    }

    public ScoreStore(File file) {
        this.file = file;
        load();
    }

    private static File resolveFile() {
        File local = new File(FILE_NAME);
        try {
            File parent = local.getAbsoluteFile().getParentFile();
            if (parent == null || parent.isDirectory()) {
                // Touch test: can we create/write it here?
                if (local.exists() || local.createNewFile()) {
                    return local;
                }
            }
        } catch (IOException ignored) {
            // Fall through to temp dir.
        }
        return new File(System.getProperty("java.io.tmpdir"), FILE_NAME);
    }

    private void load() {
        if (file.isFile()) {
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException ignored) {
                // Corrupt or unreadable file: start fresh rather than crash.
            }
        }
    }

    private void save() {
        try (OutputStream out = new FileOutputStream(file)) {
            props.store(out, "Number Guessing Game - best scores (fewest attempts)");
        } catch (IOException ignored) {
            // Read-only location: best scores just won't persist this session.
        }
    }

    /** Best attempts for the difficulty, or null if never completed. */
    public Integer getBest(GameEngine.Difficulty d) {
        String value = props.getProperty(key(d));
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Records the result if it beats the stored best.
     *
     * @return true if this was a new record
     */
    public boolean recordIfBest(GameEngine.Difficulty d, int attempts) {
        Integer best = getBest(d);
        if (best == null || attempts < best) {
            props.setProperty(key(d), Integer.toString(attempts));
            save();
            return true;
        }
        return false;
    }

    private static String key(GameEngine.Difficulty d) {
        return "best." + d.name();
    }

    /** Where scores are stored (used by the UIs to show the file location). */
    public File getFile() {
        return file.getAbsoluteFile();
    }
}
