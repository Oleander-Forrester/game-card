package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import assetsmanager.SoundManager;
import assetsmanager.VideoManager;

public abstract class AbstractMenuPanel extends JPanel {

    protected ImageIcon backgroundImage;
    protected ImageIcon cursorImage;
    protected int selectedIndex = 0;
    protected String[] menuOptions;
    protected String title;
    protected String subtitle;

    public AbstractMenuPanel(String backgroundGifName) {
        this.backgroundImage = VideoManager.loadImageIcon(backgroundGifName);
        this.cursorImage = VideoManager.loadImageIcon("cursor.png");

        this.setFocusable(true);
        this.requestFocusInWindow();

        setupKeyBindings();
    }

    protected abstract void onEnterPressed();
    protected abstract void onBackPressed();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (title != null) {
            g2d.setFont(Menu.DISPLAY_FONT_XLARGE);
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            int titleX = (getWidth() - titleWidth) / 2;
            int titleY = 270;
            drawTextWithOutline(g2d, title, titleX, titleY, Menu.WARNA_JUDUL, 4);
        }

        if (subtitle != null) {
            g2d.setFont(Menu.DISPLAY_FONT_LARGE);
            FontMetrics fm = g2d.getFontMetrics();
            int subtitleWidth = fm.stringWidth(subtitle);
            int subtitleX = (getWidth() - subtitleWidth) / 2;
            int subtitleY = 600;
            drawTextWithOutline(g2d, subtitle, subtitleX, subtitleY, Menu.WARNA_SUBJUDUL, 2);
        }

        int startY = 650;
        int lineHeight = 50;

        for (int i = 0; i < menuOptions.length; i++) {
            String text = menuOptions[i];

            Font baseFont = (i == selectedIndex) ? Menu.DISPLAY_FONT_LARGE : Menu.DISPLAY_FONT_MEDIUM;
            Color baseColor = (i == selectedIndex) ? Menu.WARNA_JUDUL : Menu.WARNA_TEKS_UTAMA;

            int totalWidth = 0;
            FontMetrics fm;
            for (char c : text.toCharArray()) {
                if (Character.isDigit(c)) {
                    fm = g2d.getFontMetrics(Menu.FONT_ANGKA.deriveFont(baseFont.getStyle(), baseFont.getSize()));
                } else {
                    fm = g2d.getFontMetrics(baseFont);
                }
                totalWidth += fm.stringWidth(String.valueOf(c));
            }

            int currentX = (getWidth() - totalWidth) / 2;
            int y = startY + (i * lineHeight);

            if (i == selectedIndex && cursorImage != null) {
                fm = g2d.getFontMetrics(baseFont);
                g2d.drawImage(cursorImage.getImage(), currentX - 50, y - fm.getAscent() / 2 - 25, 50, 50, null);
            }

            for (char c : text.toCharArray()) {
                String character = String.valueOf(c);
                Font fontToUse;

                if (Character.isDigit(c)) {
                    fontToUse = Menu.FONT_ANGKA.deriveFont(baseFont.getStyle(), baseFont.getSize());
                } else {
                    fontToUse = baseFont;
                }

                g2d.setFont(fontToUse);
                drawTextWithOutline(g2d, character, currentX, y, baseColor, 2);

                currentX += g2d.getFontMetrics().stringWidth(character);
            }
        }
    }

    private void drawTextWithOutline(Graphics g, String text, int x, int y, Color textColor, int outlineThickness) {
        g.setColor(Color.BLACK);
        g.drawString(text, x - outlineThickness, y - outlineThickness);
        g.drawString(text, x + outlineThickness, y - outlineThickness);
        g.drawString(text, x - 1, y + outlineThickness);
        g.drawString(text, x + outlineThickness, y + outlineThickness);

        g.setColor(textColor);
        g.drawString(text, x, y);
    }

    private void setupKeyBindings() {
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "downAction");
        actionMap.put("downAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                SoundManager.playSound("hovere.wav");
                selectedIndex = (selectedIndex + 1) % menuOptions.length;
                repaint();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("UP"), "upAction");
        actionMap.put("upAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                SoundManager.playSound("hovere.wav");
                selectedIndex = (selectedIndex - 1 + menuOptions.length) % menuOptions.length;
                repaint();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "enterAction");
        actionMap.put("enterAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                SoundManager.playSound("button-click.wav");
                onEnterPressed();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("BACK_SPACE"), "backAction");
        actionMap.put("backAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                SoundManager.playSound("button-click.wav");
                onBackPressed();
            }
        });
    }
}