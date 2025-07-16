// Buat file baru bernama CardUI.java
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CardUI {
    private final String name;
    private final JButton button;
    private final ImageIcon iconFront; // Gambar depan (isi kartu)
    private final ImageIcon iconBack;  // Gambar belakang (kartu tertutup)
    private final ImageIcon iconBackHint; // back tinted yellow
    private boolean isMatched = false;
    private boolean isHinted = false;

    public CardUI(String name, ImageIcon front, ImageIcon back, ImageIcon iconBackHint) {
        this.name = name;
        this.iconFront = front;
        this.iconBack = back;
        this.iconBackHint = createTintedIcon(back, new Color(255, 255, 0, 120));
        this.button = new JButton(iconBack);

        // Buat tombol transparan
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    public void flipUp() {
        if (isHinted) setHinted(false);
        button.setIcon(iconFront);
    }

    public void flipDown() {
        button.setIcon(iconBack);
        if (isHinted) setHinted(false);
    }

    // --- Getter dan Setter lainnya ---
    public JButton getButton() { return button; }
    public String getName() { return name; }
    public boolean isMatched() { return isMatched; }
    public boolean isHinted() { return isHinted; }
    public void setHinted(boolean hinted) {
        isHinted = hinted;
        if (hinted) {
            // Show yellow tinted back icon
            button.setIcon(iconBackHint);
            button.setContentAreaFilled(false);
            button.setOpaque(false);
            button.setBorderPainted(false);
        } else {
            button.setContentAreaFilled(false);
            button.setOpaque(false);
            button.setBorderPainted(false);
            button.setIcon(iconBack);
        }
    }
    public void setMatched(boolean matched) {
        isMatched = matched;
        if (matched) {
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }
    }

    private static ImageIcon createTintedIcon(ImageIcon base, Color tintColor) {
        int w = base.getIconWidth();
        int h = base.getIconHeight();
        BufferedImage tinted = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = tinted.createGraphics();
        g2.drawImage(base.getImage(), 0, 0, null);
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.setColor(tintColor);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return new ImageIcon(tinted);
    }
}