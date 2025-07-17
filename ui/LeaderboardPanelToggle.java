package ui;

import leaderboard.MultiLeaderboardManager;
import leaderboard.ScoreEntry;
import assetsmanager.VideoManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Leaderboard panel that shows either 1-player or 2-player scores.
 * A pair of buttons lets the user switch between the two modes.
 * The visual layout matches the earlier single-board style (narrow, centred table
 * over a translucent black background).
 */
public class LeaderboardPanelToggle extends JPanel {
    private static final int MODE_1P = MultiLeaderboardManager.MODE_1P;
    private static final int MODE_2P = MultiLeaderboardManager.MODE_2P;

    private final ImageIcon backgroundGif;

    private int currentMode = MODE_1P;
    private JPanel centerWrapper; // container holding the scrollable table

    public LeaderboardPanelToggle() {
        setLayout(new BorderLayout());
        backgroundGif = VideoManager.loadImageIcon("menu-utama-sakura.gif");

        // ---------- Title ----------
        JLabel title = new JLabel("HIGH SCORES", SwingConstants.CENTER);
        title.setFont(Menu.DISPLAY_FONT_LARGE);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(200, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ---------- Mode Switch Buttons ----------
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        switchPanel.setOpaque(false);
        JButton btn1P = new JButton("1 PLAYER");
        JButton btn2P = new JButton("2 PLAYER");
        btn1P.setFont(Menu.FONT_ANGKA);
        btn2P.setFont(Menu.FONT_ANGKA);
        btn1P.addActionListener(e -> switchMode(MODE_1P));
        btn2P.addActionListener(e -> switchMode(MODE_2P));
        switchPanel.add(btn1P);
        switchPanel.add(btn2P);

        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);
        topWrapper.add(title);
        topWrapper.add(switchPanel);
        add(topWrapper, BorderLayout.NORTH);

        // ---------- Center table ----------
        centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrapper.setOpaque(false);
        add(centerWrapper, BorderLayout.CENTER);
        rebuildCenter();

        // ---------- Back button ----------
        JButton back = new JButton("Kembali");
        back.setFont(Menu.DISPLAY_FONT_BUTTON);
        back.addActionListener(e -> GameWindow.getInstance().showMenu());
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        // ESC shortcut (same as back)
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke("BACK_SPACE"), "back");
        am.put("back", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                back.doClick();
            }
        });
    }

    private void switchMode(int mode) {
        if (currentMode != mode) {
            currentMode = mode;
            rebuildCenter();
        }
    }

    private void rebuildCenter() {
        centerWrapper.removeAll();
        centerWrapper.add(buildScrollableTable(currentMode));
        revalidate();
        repaint();
    }

    private JScrollPane buildScrollableTable(int mode) {
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        Font headerFont = Menu.DISPLAY_FONT_MEDIUM;
        Font rowFont = Menu.DISPLAY_FONT_BUTTON;

        String scoreHeader = (mode == MODE_1P ? "SISA WAKTU" : "SCORE");
        centerContent.add(buildRow("RANK", "NAME", scoreHeader, headerFont, Color.WHITE));
        ((JComponent) centerContent.getComponent(0)).setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));

        int fixedWidth = 450;
        centerContent.setMaximumSize(new Dimension(fixedWidth, Integer.MAX_VALUE));

        List<ScoreEntry> scores = MultiLeaderboardManager.getScores(mode);
        int rank = 1;
        for (ScoreEntry entry : scores) {
            Color color = rank == 1 ? Color.YELLOW : Color.WHITE;
            String scoreText = (mode == MODE_1P ? entry.getScore() + " detik" : String.valueOf(entry.getScore()));
            JPanel row = buildRow(ordinal(rank), entry.getName(), scoreText, rowFont, color);
            row.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            centerContent.add(row);
            rank++;
        }

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.setOpaque(true);
        tablePanel.setBackground(new Color(0, 0, 0, 100));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 80));
        tablePanel.add(centerContent);

        JScrollPane scroll = new JScrollPane(tablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(fixedWidth, 300));
        return scroll;
    }

    // Helper to build each row
    private JPanel buildRow(String r, String n, String s, Font f, Color c) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        JLabel rank = createLabel(r, Menu.FONT_ANGKA, c, 100);
        rank.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel name = createLabel(n, f, c, 230);
        JLabel score = createLabel(s, Menu.FONT_ANGKA, c, 100);
        score.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(rank);
        row.add(Box.createHorizontalStrut(20));
        row.add(name);
        row.add(Box.createHorizontalGlue());
        row.add(score);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return row;
    }

    private JLabel createLabel(String text, Font font, Color color, int minWidth) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        if (minWidth > 0) {
            Dimension size = new Dimension(minWidth, 24);
            label.setMinimumSize(size);
            label.setPreferredSize(size);
            label.setMaximumSize(size);
        }
        return label;
    }

    private String ordinal(int n) {
        int mod100 = n % 100;
        int mod10 = n % 10;
        if (mod100 - mod10 == 10) return n + "TH";
        return switch (mod10) {
            case 1 -> n + "ST";
            case 2 -> n + "ND";
            case 3 -> n + "RD";
            default -> n + "TH";
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundGif != null) {
            g.drawImage(backgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}
