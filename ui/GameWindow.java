package ui;

import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import assetsmanager.SoundManager;

public class GameWindow extends JFrame {

    private final ImageIcon backgroundGif;
    private static GameWindow instance;

    // Variabel untuk CardLayout
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public static GameWindow getInstance() {
        if (instance == null) {
            instance = new GameWindow();
        }
        return instance;
    }

    private GameWindow() {
        setTitle("Memorizing Card");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        backgroundGif = assetsmanager.VideoManager.loadImageIcon("menu-utama-sakura.gif");
        // Inisialisasi CardLayout dan panel utama yang akan menampung semua "layar"
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Logika menggambar GIF sekarang ada di sini
                if (backgroundGif != null) {
                    g.drawImage(backgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        // Tambahkan panel-panel yang statis (selalu ada)
        mainPanel.add(new PlayPanel(), "play");
        mainPanel.add(new ModeSelectionPanel(), "mode");

        // Set panel utama sebagai content pane dari JFrame
        setContentPane(mainPanel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    String[] options = {"Kembali ke Menu", "Keluar dari Game", "Batal"};
                    int choice = JOptionPane.showOptionDialog(
                            GameWindow.this,
                            "Ingin keluar?",
                            "Konfirmasi Keluar",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[2]
                    );
                    switch (choice) {
                        case 0:
                            showPlayPanel();
                            break;
                        case 1:
                            System.exit(0);
                            break;
                        case 2:
                        case JOptionPane.CLOSED_OPTION:
                            break;
                    }
                }
            }
        });

        // Atur agar bisa menerima input keyboard
        setFocusable(true);

        showPlayPanel(); // Tampilkan layar awal
        setVisible(true);
    }

    // Satu method utama untuk berpindah panel
    private void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
        // Pastikan window utama tetap fokus untuk menangkap event 'Escape'
        requestFocusInWindow();
    }

    public void showPlayPanel() {
        SoundManager.stopMusic();
        SoundManager.loopMusic("watflo.wav");
        showCard("play");
    }

    public void showModeSelection() {
        showCard("mode");
    }

    public void showDifficultySelection(int mode) {
        // Panel ini dinamis, jadi kita buat baru setiap kali dibutuhkan
        DifficultySelectionPanel difficultyPanel = new DifficultySelectionPanel(mode);
        mainPanel.add(difficultyPanel, "difficulty");
        showCard("difficulty");
    }

    public void showPlayerNameInput(int mode, int difficulty) {
        // Panel ini juga dinamis
        PlayerNamePanel playerNamePanel = new PlayerNamePanel(mode, difficulty);
        mainPanel.add(playerNamePanel, "player_name");
        showCard("player_name");
    }

    public void showMenu() {
        SoundManager.stopMusic();
        SoundManager.loopMusic("watflo.wav");
        // Kembali ke menu utama (PlayPanel)
        showCard("play");
    }

    public void showGame(int mode, int difficulty, String player1Name, String player2Name) {
        SoundManager.loopMusic("gameplay-music.wav");
        GamePanel gamePanel = new GamePanel(mode, difficulty, player1Name, player2Name);
        mainPanel.add(gamePanel, "game");
        showCard("game");
    }

    public void showLeaderboard() {
        // Panel ini bisa dibuat baru setiap kali atau dibuat sekali di konstruktor
        LeaderboardPanel leaderboardPanel = new LeaderboardPanel();
        mainPanel.add(leaderboardPanel, "leaderboard");
        showCard("leaderboard");
    }
}
