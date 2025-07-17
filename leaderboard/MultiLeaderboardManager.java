package leaderboard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Stores separate leaderboards for 1-player (mode 1) and 2-player (mode 2).
 * For mode 1 we store remaining time; for mode 2 we store points.
 */
public class MultiLeaderboardManager {

    public static final int MODE_1P = 1;
    public static final int MODE_2P = 2;
    private static final int MAX_ENTRIES = 10;
    private static final String BASE_DIR = "data-game";

    // Map<mode, cachedSet>
    private static final Map<Integer, NavigableSet<ScoreEntry>> cacheMap = new HashMap<>();

    public static synchronized List<ScoreEntry> getScores(int mode) {
        NavigableSet<ScoreEntry> set = cacheMap.get(mode);
        if (set == null) {
            set = loadScores(mode);
            cacheMap.put(mode, set);
        }
        return new ArrayList<>(set);
    }

    public static synchronized void addScore(int mode, String name, int score) {
        if (name == null || name.isBlank()) return;
        if (score <= 0) return;
        NavigableSet<ScoreEntry> set = cacheMap.computeIfAbsent(mode, MultiLeaderboardManager::loadScores);
        set.add(new ScoreEntry(name, score));
        while (set.size() > MAX_ENTRIES) {
            set.pollLast(); // remove lowest score
        }
        saveScores(mode, set);
    }

    private static String filePath(int mode) {
        String fname = mode == MODE_1P ? "leaderboard_1p.dat" : "leaderboard_2p.dat";
        return BASE_DIR + File.separator + fname;
    }

    private static NavigableSet<ScoreEntry> loadScores(int mode) {
        NavigableSet<ScoreEntry> set = new TreeSet<>((a, b) -> {
            int cmp = Integer.compare(b.getScore(), a.getScore()); // higher score/time first
            if (cmp != 0) return cmp;
            return a.getName().compareToIgnoreCase(b.getName());
        });
        File file = new File(filePath(mode));
        if (!file.exists()) return set;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    try {
                        int s = Integer.parseInt(parts[1]);
                        set.add(new ScoreEntry(parts[0], s));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return set;
    }

    private static void saveScores(int mode, NavigableSet<ScoreEntry> set) {
        File file = new File(filePath(mode));
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (ScoreEntry entry : set) {
                bw.write(entry.getName() + "," + entry.getScore());
                bw.newLine();
            }
        } catch (IOException ignored) {
        }
    }
}
