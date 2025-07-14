package assetsmanager;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

public class SoundManager {
    private static Clip backgroundMusic;
    private static String currentMusicName;
    private static final Map<String, Clip> soundCache = new HashMap<>();

    /**
     * Memutar sound effect pendek sekali jalan (untuk klik, hover, menang, kalah, dll).
     * @param soundName Nama file suara (contoh: "click.wav")
     */
    public static void playSound(String soundName) {
        Clip clip = soundCache.get(soundName);
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0); // Wajib: Putar ulang dari awal
            clip.start();
        }
    }

    /**
     * Memutar musik latar secara berulang.
     * @param musicName Nama file musik (contoh: "main-theme.wav")
     */
    public static void loopMusic(String musicName) {
        if (musicName.equals(currentMusicName) && backgroundMusic != null && backgroundMusic.isRunning()) {
            return;
        }
        stopMusic(); // Hentikan musik sebelumnya jika ada
        try {
            InputStream audioSrc = SoundManager.class.getResourceAsStream("/assets/sounds/" + musicName);
            if (audioSrc == null) {
                System.err.println("Music file not found: " + musicName);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioIn);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            currentMusicName = musicName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Menghentikan musik latar yang sedang berjalan.
     */
    public static void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
        currentMusicName = null;
    }

    public static void loadSounds() {
        String[] soundNames = {"button-click.wav", "hovere.wav", "win.wav", "game-over.wav", "watflo.wav", "matched.wav", "win.wav", "gameplay-music.wav"};

        for (String name : soundNames) {
            try {
                InputStream audioSrc = SoundManager.class.getResourceAsStream("/assets/sounds/" + name);
                if (audioSrc == null) {
                    System.err.println("Sound file not found: " + name);
                    continue;
                }
                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                soundCache.put(name, clip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Sound effects dimuat.");
    }
}
