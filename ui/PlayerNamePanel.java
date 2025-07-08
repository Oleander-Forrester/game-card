package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerNamePanel extends JPanel {
    public PlayerNamePanel(int mode, int difficulty) {
        setLayout(new GridBagLayout());
        setBackground(Color.decode("#ADD8E6"));
        GridBagConstraints gbc = new GridBagConstraints();

        // Pengaturan Ukuran & Font standar
        Dimension componentSize = new Dimension(280, 40);
        Font labelFont = Menu.DISPLAY_FONT_MEDIUM;
        Font fieldFont = Menu.DISPLAY_FONT_BUTTON;

        gbc.insets = new Insets(0, 0, 5, 0); // Jarak antar label dan field
        gbc.gridx = 0;

        List<JTextField> nameFields = new ArrayList<>();

        // Label Player 1
        gbc.gridy = 0;
        JLabel p1Label = new JLabel(mode == 1 ? "Masukkan Nama Player" : "Nama Player 1");
        p1Label.setFont(labelFont);
        add(p1Label, gbc);

        // Field Player 1
        gbc.gridy = 1;
        JTextField p1Field = new JTextField();
        p1Field.setPreferredSize(componentSize);
        p1Field.setFont(fieldFont);
        p1Field.setHorizontalAlignment(JTextField.CENTER);
        nameFields.add(p1Field);
        add(p1Field, gbc);

        if (mode == 2) {
            // Label Player 2
            gbc.gridy = 2;
            gbc.insets = new Insets(15, 0, 5, 0); // Jarak atas lebih besar
            JLabel p2Label = new JLabel("Nama Player 2");
            p2Label.setFont(labelFont);
            add(p2Label, gbc);

            // Field Player 2
            gbc.gridy = 3;
            gbc.insets = new Insets(0, 0, 5, 0);
            JTextField p2Field = new JTextField();
            p2Field.setPreferredSize(componentSize);
            p2Field.setFont(fieldFont);
            p2Field.setHorizontalAlignment(JTextField.CENTER);
            nameFields.add(p2Field);
            add(p2Field, gbc);
        }

        // --- Panel untuk Tombol Aksi ---
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0); // Jarak atas dari field terakhir
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionPanel.setBackground(Color.decode("#ADD8E6"));

        Dimension buttonSize = new Dimension(130, 40); // Tombol sedikit lebih kecil
        Font buttonFont = Menu.DISPLAY_FONT_BUTTON;

        // Tombol Kembali
        JButton backButton = new JButton("Kembali");
        backButton.setPreferredSize(buttonSize);
        backButton.setFont(buttonFont);
        backButton.setBackground(Color.decode("#696969"));
        backButton.setForeground(Color.WHITE);
        actionPanel.add(backButton);
        add(actionPanel, gbc);

        // Tombol Mulai
        JButton startButton = new JButton("Mulai");
        startButton.setPreferredSize(buttonSize);
        startButton.setFont(buttonFont);
        startButton.setBackground(Color.decode("#4682B4"));
        startButton.setForeground(Color.WHITE);
        actionPanel.add(startButton);

        // --- Action Listeners ---
        startButton.addActionListener(_ -> {
            List<String> playerNames = new ArrayList<>();
            boolean allNamesValid = true;
            for (JTextField field : nameFields) {
                if (field.getText().trim().isEmpty()) {
                    allNamesValid = false;
                    break;
                }
                playerNames.add(field.getText().trim());
            }

            if (!allNamesValid) {
                JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                String p1Name = playerNames.get(0);
                String p2Name = (mode == 2) ? playerNames.get(1) : null;
                GameWindow.getInstance().showGame(mode, difficulty, p1Name, p2Name);
            }
        });

        backButton.addActionListener(_ -> GameWindow.getInstance().showDifficultySelection(mode));
    }
}