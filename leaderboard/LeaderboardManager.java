package leaderboard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardManager {
    private static final String SAVE_FILE = "data-game/leaderboard.dat";
    private static final int MAX_ENTRIES = 10;
    private static NavigableSet<ScoreEntry> cache;

    public static synchronized List<ScoreEntry> getScores() {
        if (cache == null) {
            cache = loadScores();
        }
        return new ArrayList<>(cache);
    }

    public static synchronized void addScore(String name, int score) {
        if (name == null || name.isBlank()) return;
        if (score <= 0) return;
        if (cache == null) cache = loadScores();
        cache.add(new ScoreEntry(name, score));
        while (cache.size() > MAX_ENTRIES) {
            cache.pollLast(); // remove lowest score
        }
        saveScores();
    }

    private static NavigableSet<ScoreEntry> loadScores() {
        NavigableSet<ScoreEntry> set = new TreeSet<>((a, b) -> {
            int cmp = Integer.compare(b.getScore(), a.getScore());
            if (cmp != 0) return cmp;
            return a.getName().compareToIgnoreCase(b.getName());
        });
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return set;
        }
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

    private static void saveScores() {
        File file = new File(SAVE_FILE);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (ScoreEntry entry : cache) {
                bw.write(entry.getName() + "," + entry.getScore());
                bw.newLine();
            }
        } catch (IOException ignored) {
        }
    }
}
