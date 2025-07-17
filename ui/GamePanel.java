package ui;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.List;

import assetsmanager.SoundManager;
import assetsmanager.ImageManager;
//import assetsmanager.VideoManager;
import leaderboard.MultiLeaderboardManager;

public class GamePanel extends JPanel {
    private final int mode;
    private final int difficulty;
    private final int rows;
    private final int cols;
    private final String player1Name;
    private final String player2Name;
    private final List<CardUI> cardList = new ArrayList<>();

    private final Stack<CardUI> openedCards = new Stack<>();
    private final JButton hintBtn;
    private final Map<String, Boolean> matchedPairs = new HashMap<>();
    private final Map<CardUI, CardUI> matchGraph = new HashMap<>();

    private int lives = 3;
    private int timeLeft = 60;
    private Timer countdownTimer;
    private JLabel lifeLabel;
    private JLabel timerValueLabel;

    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private final Queue<Integer> playerTurnQueue = new LinkedList<>();
    private JLabel turnLabel, scoreLabel;
    private boolean isChecking = false;
    private boolean hintOnCooldown = false;


    private final ImageIcon backgroundGif;

    public GamePanel(int mode, int difficulty, String player1Name, String player2Name) {
        this.mode = mode;
        this.difficulty = difficulty;
        this.player1Name = player1Name;
        this.player2Name = player2Name;

        setLayout(new BorderLayout());
        setBackground(Color.decode("#ADD8E6"));
        backgroundGif = ImageManager.loadImageIcon("game-panel2.jpg");

        switch (difficulty) {
            case 1 -> {
                rows = 4;
                cols = 5;
            } // Medium
            case 2 -> {
                rows = 4;
                cols = 6;
            } // Hard
            default -> {
                rows = 4;
                cols = 4;
            } // Easy
        }

        // --- Panel Atas (Info Pemain, Skor, Waktu) ---
        RoundedPanel topPanel = new RoundedPanel(30); // 30px radius sudut
        topPanel.setLayout(new BorderLayout(10, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.setBackground(new Color(255, 199, 237, 200));
        Font statusFont = Menu.DISPLAY_FONT_BUTTON;
        Color fontColor = Color.BLACK;

        if (mode == 1) {
            JLabel lifePrefixLabel = new JLabel("Nyawa: ");
            lifePrefixLabel.setFont(statusFont);
            lifePrefixLabel.setForeground(fontColor);
            lifePrefixLabel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));

            lifeLabel = new JLabel(String.valueOf(lives));
            lifeLabel.setFont(Menu.FONT_ANGKA);
            lifeLabel.setForeground(fontColor);
            lifeLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

            JPanel lifePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            lifePanel.setOpaque(false);
            lifeLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            lifePanel.add(lifePrefixLabel);
            lifePanel.add(lifeLabel);

            timerValueLabel = new JLabel(String.valueOf(timeLeft));
            timerValueLabel.setFont(Menu.FONT_ANGKA);
            timerValueLabel.setForeground(fontColor);
            timerValueLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

            JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            timerPanel.setOpaque(false);

            JLabel timerPrefixLabel = new JLabel("Timer: ");
            timerPrefixLabel.setFont(statusFont);
            timerPrefixLabel.setForeground(fontColor);
            timerPrefixLabel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));

            timerValueLabel = new JLabel(String.valueOf(timeLeft));
            timerValueLabel.setFont(Menu.FONT_ANGKA);
            timerValueLabel.setForeground(fontColor);

            JLabel timerSuffixLabel = new JLabel(" detik");
            timerSuffixLabel.setFont(statusFont);
            timerSuffixLabel.setForeground(fontColor);
            timerSuffixLabel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));

            timerPanel.add(timerPrefixLabel);
            timerPanel.add(timerValueLabel);
            timerPanel.add(timerSuffixLabel);

            JLabel levelLabel = new JLabel("⭐ Level: " + getDifficultyLabel());
            levelLabel.setFont(statusFont);
            levelLabel.setForeground(fontColor);
            levelLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            topPanel.add(lifePanel, BorderLayout.WEST);
            topPanel.add(timerPanel, BorderLayout.CENTER);
            topPanel.add(levelLabel, BorderLayout.EAST);

        } else {
            playerTurnQueue.clear();
            playerTurnQueue.add(1);
            playerTurnQueue.add(2);

            turnLabel = new JLabel("Giliran: " + this.player1Name);
            turnLabel.setFont(statusFont);
            turnLabel.setForeground(fontColor);
            turnLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

            JLabel levelLabel = new JLabel("⭐ Level: " + getDifficultyLabel());
            levelLabel.setFont(statusFont);
            levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
            levelLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

            scoreLabel = new JLabel("Skor " + this.player1Name + ": " + scoreP1 + " | Skor " + this.player2Name + ": " + scoreP2);
            scoreLabel.setFont(Menu.FONT_ANGKA);
            scoreLabel.setForeground(fontColor);
            scoreLabel.setHorizontalAlignment(JLabel.RIGHT);
            scoreLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

            topPanel.add(turnLabel, BorderLayout.WEST);
            topPanel.add(scoreLabel, BorderLayout.EAST);
            topPanel.add(levelLabel);
        }
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.setBorder(BorderFactory.createEmptyBorder(20, 50, 0, 50));
        topWrapper.add(topPanel, BorderLayout.CENTER);

        add(topWrapper, BorderLayout.NORTH);


        // --- Panel Kartu (Grid Permainan) ---
        RoundedPanel gridPanel = new RoundedPanel(40);
        gridPanel.setLayout(new GridLayout(rows, cols, 5, 5));
        gridPanel.setBackground(new Color(0, 0, 0, 0));
        gridPanel.setOpaque(false);
        List<String> cardNames = generateCardPairs(rows * cols);
        ImageIcon backIcon = loadCardImage("/assets/cards/card_back.png", true);

        Map<String, CardUI> firstCardByName = new HashMap<>();
        for (String name : cardNames) {
            ImageIcon frontIcon = loadCardImage("/assets/cards/" + name, false);
            CardUI card = new CardUI(name, frontIcon, backIcon);
            card.getButton().addActionListener(_ -> handleCardClick(card));
            gridPanel.add(card.getButton());
            cardList.add(card);
            if (firstCardByName.containsKey(name)) {
                CardUI other = firstCardByName.get(name);
                matchGraph.put(card, other);
                matchGraph.put(other, card);
            } else {
                firstCardByName.put(name, card);
            }
        }
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 300, 20, 300));
        wrapper.setOpaque(false);

        wrapper.add(gridPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        showAllCardsTemporarily();


        // --- Panel Bawah (Tombol Kembali) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
//        bottomPanel.setBackground(Color.decode("#ADD8E6"));
        JButton backBtn = new JButton("Kembali ke Menu");
        backBtn.setBackground(Color.decode("#4682B4"));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(_ -> {
            stopTimer();
            GameWindow.getInstance().showMenu();
        });
        bottomPanel.add(backBtn);

        hintBtn = new JButton("Hint -5 detik");
        hintBtn.setBackground(Color.decode("#32CD32"));
        hintBtn.setForeground(Color.WHITE);
        hintBtn.addActionListener(_ -> giveHint());
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(hintBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        if (mode == 1) {
            startCountdownTimer();
        }


        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "backToMenu");
        getActionMap().put("backToMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        GamePanel.this,
                        "Yakin mau kembali ke menu? Progress akan hilang!",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (result == JOptionPane.YES_OPTION) {
                    stopTimer();
                    GameWindow.getInstance().showMenu();
                }
            }
        });

        setFocusable(true);
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void handleCardClick(CardUI clicked) {
        if (clicked.isMatched() || openedCards.contains(clicked) || isChecking) {
            return;
        }

        clicked.flipUp();
        openedCards.push(clicked);

        if (openedCards.size() == 2) {
            isChecking = true;
            CardUI first = openedCards.pop();
            CardUI second = openedCards.pop();

            if (first.getName().equals(second.getName())) {
                first.setMatched(true);
                second.setMatched(true);
                matchedPairs.put(first.getName(), true);

                SoundManager.playSound("matched.wav");


                if (mode == 2 && !playerTurnQueue.isEmpty()) {
                    if (playerTurnQueue.peek() == 1) scoreP1++;
                    else scoreP2++;
                    updateScoreAndTurn();
                }

                if (isGameWon()) {
                    SwingUtilities.invokeLater(() -> {
                        if (mode == 1) stopTimer();
                        showWinDialog();
                    });
                }
                isChecking = false;
            } else {
                Timer flipBackTimer = new Timer(1000, _ -> {
                    first.flipDown();
                    second.flipDown();
                    if (mode == 2 && !playerTurnQueue.isEmpty()) {
                        playerTurnQueue.add(playerTurnQueue.poll());
                        updateScoreAndTurn();
                    }
                    isChecking = false;
                });
                flipBackTimer.setRepeats(false);
                flipBackTimer.start();
            }
        }
    }

    private void startCountdownTimer() {
        countdownTimer = new Timer(1000, _ -> {
            if (timeLeft > 0) {
                timeLeft--;
                updateTampilanWaktu();
            } else {
                lives--;
                updateTampilanNyawa();
                timeLeft = 60;

                if (lives <= 0) {
                    countdownTimer.stop();
                    SoundManager.playSound("game-over.wav");
                    JOptionPane.showMessageDialog(this, "Game Over! Kamu kehabisan nyawa.", "Game Over", JOptionPane.ERROR_MESSAGE);
                    GameWindow.getInstance().showMenu();
                } else {
                    SoundManager.playSound("game-over.wav");
                    String[] options = {"Kembali ke Menu", "Lanjut Main"};
                    int pilihan = JOptionPane.showOptionDialog(
                            this,
                            "Waktu Habis! Kamu kehilangan 1 nyawa.",
                            "Waktu Habis",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    for (CardUI card : cardList) {
                        if (!card.isMatched()) {
                            card.flipDown();
                        }
                    }
                    openedCards.clear();
                    updateTampilanNyawa();
                    updateTampilanWaktu();

                    if (pilihan == 0) {
                        countdownTimer.stop();
                        GameWindow.getInstance().showMenu();
                    }
                }
            }
        });
        countdownTimer.start();
    }

    public void stopTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }

    private void updateTampilanWaktu() {
        if (timerValueLabel != null) {
            timerValueLabel.setText(String.valueOf(timeLeft));
        }
    }

    private void updateTampilanNyawa() {
        if (lifeLabel != null) {
            lifeLabel.setText(String.valueOf(lives));
        }
    }

    private void updateScoreAndTurn() {
        if (mode == 2) {
            String currentPlayerName = playerTurnQueue.peek() == 1 ? player1Name : player2Name;
            turnLabel.setText("Giliran: " + currentPlayerName);
            scoreLabel.setText("Skor " + player1Name + ": " + scoreP1 + " | Skor " + player2Name + ": " + scoreP2);
        }
    }

    private ImageIcon loadCardImage(String path, boolean isBack) {
        String[] extensions = {".png", ".jpg", ".jpeg"};
        URL imgURL = null;

        if (isBack) {
            imgURL = getClass().getResource(path);
        } else {
            for (String ext : extensions) {
                imgURL = getClass().getResource(path + ext);
                if (imgURL != null) break;
            }
        }

        if (imgURL != null) {
            ImageIcon originalIcon = new ImageIcon(imgURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } else {
            System.err.println("Nggak nemu file gambar untuk: " + path);
            BufferedImage placeholder = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = placeholder.createGraphics();
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, 150, 150);
            g.dispose();
            return new ImageIcon(placeholder);
        }
    }

    private List<String> generateCardPairs(int totalCards) {
        String[] possible = {
                "anjing", "avocado", "baby shark", "bebek", "bee", "blueberry", "burger", "butterfly", "card_back", "carrot",
                "coffe", "cupcake", "depe", "dolphin", "donut", "eskrim", "flamingo", "gajah", "gitar", "hammy", "hantu",
                "husky", "jellyfish", "jerapah", "kelinci", "keropi", "meng", "minion", "monster", "mushroom", "paus",
                "penguin", "pig", "pizza", "pokeball", "powerpuff", "rose", "spiderman", "stitch", "thor", "tikus",
                "tomat", "watermelon"
        };
        List<String> names = new ArrayList<>();
        for (int i = 0; i < totalCards / 2; i++) {
            names.add(possible[i]);
            names.add(possible[i]);
        }
        Collections.shuffle(names);
        return names;
    }

    private boolean isGameWon() {
        return matchedPairs.size() == (rows * cols) / 2;
    }

    private void showWinDialog() {
        SoundManager.playSound("win.wav");
        String message;
        if (mode == 1) {
            message = "Selamat " + player1Name + "! Kamu berhasil mencocokkan semua kartu!";
            int finalScore = timeLeft;
            MultiLeaderboardManager.addScore(MultiLeaderboardManager.MODE_1P, player1Name, finalScore);

        } else {
            if (scoreP1 > scoreP2) {
                MultiLeaderboardManager.addScore(MultiLeaderboardManager.MODE_2P, player1Name, scoreP1);
                MultiLeaderboardManager.addScore(MultiLeaderboardManager.MODE_2P, player2Name, scoreP2);
                message = "Selamat " + player1Name + "! Kamu memenangkan permainan!";
                // LeaderboardManager.addScore(player1Name, scoreP1);

            } else if (scoreP2 > scoreP1) {
                MultiLeaderboardManager.addScore(MultiLeaderboardManager.MODE_2P, player1Name, scoreP1);
                MultiLeaderboardManager.addScore(MultiLeaderboardManager.MODE_2P, player2Name, scoreP2);
                message = "Selamat " + player2Name + "! Kamu memenangkan permainan!";
                // LeaderboardManager.addScore(player2Name, scoreP2);

            } else {
                MultiLeaderboardManager.addScore(MultiLeaderboardManager.MODE_2P, player1Name, scoreP1);
                MultiLeaderboardManager.addScore(MultiLeaderboardManager.MODE_2P, player2Name, scoreP2);
                message = "Permainan berakhir seri!";
            }
        }

        int option = JOptionPane.showOptionDialog(
                this,
                message,
                "Permainan Selesai!",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Main Lagi", "Kembali ke Menu"},
                "Main Lagi"
        );

        if (option == 0) {
            GameWindow.getInstance().showDifficultySelection(mode);
        } else {
            GameWindow.getInstance().showMenu();
        }
    }

    private String getDifficultyLabel() {
        return switch (difficulty) {
            case 0 -> "Easy";
            case 1 -> "Medium";
            case 2 -> "Hard";
            default -> "-";
        };
    }

    static class RoundedPanel extends JPanel {
        private final int cornerRadius;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }


        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundGif != null) {
            g.drawImage(backgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void showAllCardsTemporarily() {
        // 1. Buka semua kartu
        for (CardUI card : cardList) {
            card.flipUp();
        }

        // 2. Tunggu 1 detik, lalu tutup semua kartu
        Timer previewTimer = new Timer(1000, _ -> {
            for (CardUI card : cardList) {
                card.flipDown();
            }

//            `// Baru mulai timer countdown (kalau 1-player mode)
//            if (mode == 1) {
//                startCountdownTimer();
//            }`
        });
        previewTimer.setRepeats(false);
        previewTimer.start();
    }

    private void giveHint() {
        if (hintOnCooldown) return;

        // Apply penalty
        if (mode == 1) {
            timeLeft = Math.max(0, timeLeft - 5);
            updateTampilanWaktu();
        } else if (mode == 2 && !playerTurnQueue.isEmpty()) {
            if (playerTurnQueue.peek() == 1 && scoreP1 > 0) {
                scoreP1--;
            } else if (playerTurnQueue.peek() == 2 && scoreP2 > 0) {
                scoreP2--;
            }
            updateScoreAndTurn();
        }

        // Find first unmatched pair via graph structure
        for (CardUI first : cardList) {
            if (first.isMatched()) continue;
            CardUI second = matchGraph.get(first);
            if (second != null && !second.isMatched()) {
                first.setHinted(true);
                second.setHinted(true);

                javax.swing.Timer t = new javax.swing.Timer(1500, _ -> {
                    first.setHinted(false);
                    second.setHinted(false);
                });
                t.setRepeats(false);
                t.start();

                hintBtn.setEnabled(false);
                hintOnCooldown = true;
                javax.swing.Timer cooldown = new javax.swing.Timer(3000, _ -> {
                    hintOnCooldown = false;
                    hintBtn.setEnabled(true);
                });
                cooldown.setRepeats(false);
                cooldown.start();
                return;
            }
        }
    }


}
