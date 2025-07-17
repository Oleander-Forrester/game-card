package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import assetsmanager.SoundManager;
import assetsmanager.VideoManager;

public class PlayerNamePanel extends JPanel {

    private final ImageIcon backgroundGif;

    public PlayerNamePanel(int mode, int difficulty) {
        setLayout(new GridBagLayout());
        setBackground(Color.decode("#ADD8E6"));
        GridBagConstraints gbc = new GridBagConstraints();

        backgroundGif = VideoManager.loadImageIcon("menu-utama-sakura.gif");

        Dimension componentSize = new Dimension(280, 40);
        Font labelFont = Menu.DISPLAY_FONT_LARGE;
        Font fieldFont = Menu.DISPLAY_FONT_BUTTON;
        gbc.gridx = 0;

        List<JTextField> nameFields = new ArrayList<>();

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        if (mode == 1) {
            setBorder(BorderFactory.createEmptyBorder(400, 0, 0, 0));
            gbc.gridy = 0;
            gbc.insets = new Insets(25, 0, 5, 0);
            JLabel p1Label = new JLabel("Masukkan Nama Player");
            p1Label.setFont(labelFont);
            p1Label.setForeground(Color.decode("#FFC7ED"));
            add(p1Label, gbc);
        } else {
            setBorder(BorderFactory.createEmptyBorder(450, 0, 0, 0));
            gbc.gridy = 0;
            gbc.insets = new Insets(25, 0, 5, 0);
            JPanel p1LabelPanel = getPanel(labelFont);
            add(p1LabelPanel, gbc);
        }

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        JTextField p1Field = new JTextField();
        p1Field.setPreferredSize(componentSize);
        p1Field.setFont(fieldFont);
        p1Field.setHorizontalAlignment(JTextField.CENTER);
        nameFields.add(p1Field);
        add(p1Field, gbc);

        if (mode == 2) {
            gbc.gridy = 2;
            gbc.insets = new Insets(5, 0, 5, 0);
            JPanel p2LabelPanel = getJPanel(labelFont);
            add(p2LabelPanel, gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(0, 0, 5, 0);
            JTextField p2Field = new JTextField();
            p2Field.setPreferredSize(componentSize);
            p2Field.setFont(fieldFont);
            p2Field.setHorizontalAlignment(JTextField.CENTER);
            nameFields.add(p2Field);
            add(p2Field, gbc);
        }

        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionPanel.setOpaque(false);

        Dimension buttonSize = new Dimension(130, 40);
        Font buttonFont = Menu.DISPLAY_FONT_BUTTON;

        JButton backButton = new JButton("Kembali");
        backButton.setPreferredSize(buttonSize);
        backButton.setFont(buttonFont);
        backButton.setBackground(Color.decode("#696969"));
        backButton.setForeground(Color.WHITE);
        actionPanel.add(backButton);
        add(actionPanel, gbc);

        JButton startButton = new JButton("Mulai");
        startButton.setPreferredSize(buttonSize);
        startButton.setFont(buttonFont);
        startButton.setBackground(Color.decode("#4682B4"));
        startButton.setForeground(Color.WHITE);
        actionPanel.add(startButton);

        startButton.addActionListener(_ -> {
            SoundManager.playSound("button-click.wav");
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

        backButton.addActionListener(_ -> {
            SoundManager.playSound("button-click.wav");
            GameWindow.getInstance().showDifficultySelection(mode);
        });

        SwingUtilities.invokeLater(() -> {
            JRootPane rootPane = SwingUtilities.getRootPane(this);
            if (rootPane != null) {
                rootPane.setDefaultButton(startButton);
            }
        });

        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();

        KeyStroke backspaceKey = KeyStroke.getKeyStroke("BACK_SPACE");

        actionMap.put("backAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backButton.doClick();
            }
        });

        inputMap.put(backspaceKey, "backAction");
    }

    private static JPanel getPanel(Font labelFont) {
        JPanel p1LabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p1LabelPanel.setOpaque(false);

        JLabel p1Prefix = new JLabel("Nama Player ");
        p1Prefix.setFont(labelFont);
        p1Prefix.setForeground(Color.decode("#FFC7ED"));
        p1Prefix.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel p1Number = new JLabel("1");
        p1Number.setFont(Menu.FONT_ANGKA.deriveFont(labelFont.getStyle(), labelFont.getSize() + 0f));
        p1Number.setForeground(Color.decode("#FFC7ED"));

        p1LabelPanel.add(p1Prefix);
        p1LabelPanel.add(p1Number);
        return p1LabelPanel;
    }

    private static JPanel getJPanel(Font labelFont) {
        JPanel p2LabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p2LabelPanel.setOpaque(false);

        JLabel p2Prefix = new JLabel("Nama Player ");
        p2Prefix.setFont(labelFont);
        p2Prefix.setForeground(Color.decode("#FFC7ED"));
        p2Prefix.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel p2Number = new JLabel("2");
        p2Number.setFont(Menu.FONT_ANGKA.deriveFont(labelFont.getStyle(), labelFont.getSize() + 0f));
        p2Number.setForeground(Color.decode("#FFC7ED"));
        p2Number.setBorder(BorderFactory.createEmptyBorder(-3, 0, 0, 0));

        p2LabelPanel.add(p2Prefix);
        p2LabelPanel.add(p2Number);
        return p2LabelPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundGif != null) {
            g.drawImage(backgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}