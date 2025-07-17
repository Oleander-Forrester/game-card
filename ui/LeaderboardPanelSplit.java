package ui;

import leaderboard.MultiLeaderboardManager;
import leaderboard.ScoreEntry;
import assetsmanager.VideoManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Shows two separate tables: 1-player (ranked by remaining time) and 2-player (ranked by points).
 */
public class LeaderboardPanelSplit extends JPanel {
    private final ImageIcon backgroundGif;

    public LeaderboardPanelSplit() {
        setLayout(new BorderLayout());
        backgroundGif = VideoManager.loadImageIcon("menu-utama-sakura.gif");

        JLabel title = new JLabel("HIGH SCORES", SwingConstants.CENTER);
        title.setFont(Menu.DISPLAY_FONT_LARGE);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(200, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel();
        centerWrapper.setOpaque(false);
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));

        centerWrapper.add(buildSection("1 PLAYER", MultiLeaderboardManager.MODE_1P));
        centerWrapper.add(Box.createVerticalStrut(30));
        centerWrapper.add(buildSection("2 PLAYER", MultiLeaderboardManager.MODE_2P));

        JPanel tableBg = new JPanel();
        tableBg.setOpaque(true);
        tableBg.setBackground(new Color(0, 0, 0, 100));
        tableBg.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        tableBg.add(centerWrapper);

        add(tableBg, BorderLayout.CENTER);

        JButton back = new JButton("Kembali");
        back.setFont(Menu.DISPLAY_FONT_BUTTON);
        back.addActionListener(e -> GameWindow.getInstance().showMenu());
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel buildSection(String headerText, int mode) {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        JLabel header = new JLabel(headerText, SwingConstants.CENTER);
        header.setFont(Menu.DISPLAY_FONT_MEDIUM);
        header.setForeground(Color.WHITE);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.add(header);

        section.add(Box.createVerticalStrut(10));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        Font headerFont = Menu.DISPLAY_FONT_BUTTON;
        Font rowFont = Menu.DISPLAY_FONT_BUTTON;

        content.add(buildRow("RANK", "NAME", "SCORE", headerFont, Color.WHITE));

        List<ScoreEntry> scores = MultiLeaderboardManager.getScores(mode);
        int rank = 1;
        for (ScoreEntry entry : scores) {
            Color c = rank == 1 ? Color.YELLOW : Color.WHITE;
            content.add(buildRow(ordinal(rank), entry.getName(), String.valueOf(entry.getScore()), rowFont, c));
            rank++;
        }
        section.add(content);
        return section;
    }

    private JPanel buildRow(String r, String n, String s, Font f, Color c) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        JLabel rank = createLabel(r, Menu.FONT_ANGKA, c, 80);
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
