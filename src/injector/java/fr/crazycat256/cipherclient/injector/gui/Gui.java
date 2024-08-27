/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.injector.gui;

import fr.crazycat256.cipherclient.injector.Attacher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Gui {

    public static final String TITLE = "CipherClient Injector";

    private JLabel outputLabel;

    public static void start() {
        SwingUtilities.invokeLater(Gui::new);
    }

    public Gui() {
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());

        Font comfortaa50 = loadFont("/comfortaa.ttf", 50);
        Font comfortaa40 = loadFont("/comfortaa.ttf", 40);
        Color titleColor = Color.BLACK;
        Color bg = new Color(224, 224, 224);

        JLabel titleLabel = new JLabel("CipherClient", SwingConstants.CENTER);
        titleLabel.setFont(comfortaa50);
        titleLabel.setForeground(titleColor);

        JLabel titleLabel2 = new JLabel("Injector", SwingConstants.CENTER);
        titleLabel2.setFont(comfortaa40);
        titleLabel2.setForeground(titleColor);

        JPanel titlePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        titlePanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 0, 0);
        titlePanel.add(titleLabel2, gbc);

        titlePanel.setBackground(bg);

        JLabel pidLabel = new JLabel("PID:");
        pidLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        JTextField pidField = new JTextField(8);
        pidField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

        JButton selectInstanceButton = new JButton("Select");
        selectInstanceButton.setFont(new Font("Arial", Font.PLAIN, 14));
        selectInstanceButton.addActionListener(e -> new ProcessSelector(frame, pidField).setVisible(true));

        JPanel pidPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pidPanel.setBackground(bg);
        pidPanel.add(pidLabel);
        pidPanel.add(pidField);
        pidPanel.add(selectInstanceButton);

        JPanel injectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        injectPanel.setBackground(bg);
        JButton injectButton = new JButton("Inject");
        injectButton.setFont(new Font("Arial", Font.PLAIN, 20));
        injectButton.addActionListener(e -> {
            long pid;
            try {
                pid = Long.parseLong(pidField.getText());
            } catch (NumberFormatException ex) {
                error("Invalid PID");
                return;
            }
            try {
                Attacher.attach(pid);
                success("CipherClient has been injected to process " + pid);
            } catch (Exception ex) {
                ex.printStackTrace();
                error("An unexpected error has occured");
            }
        });
        injectPanel.add(injectButton);

        JPanel outputPanel = new JPanel();
        outputPanel.setBackground(bg);
        outputLabel = new JLabel();
        outputPanel.setVisible(false);
        outputPanel.add(outputLabel);

        JPanel githubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        githubPanel.setBackground(bg);
        JLabel githubLabel = new JLabel("<html><a href=''>GitHub Project</a></html>");
        githubLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        githubLabel.setForeground(Color.BLUE);
        githubLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openLink("https://github.com/crazycat256/CipherClient");
            }
        });
        githubPanel.add(githubLabel);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bg);
        mainPanel.add(titlePanel);
        mainPanel.add(pidPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(injectPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(outputPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(githubPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);

    }

    private Font loadFont(String path, float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(path)).deriveFont(size);
        } catch (Exception e) {
            return new Font("Arial", Font.PLAIN, (int) size);
        }
    }

    private void error(String message) {
        outputLabel.setText(message);
        outputLabel.setForeground(Color.RED);
        outputLabel.getParent().setVisible(true);
    }

    private void success(String message) {
        outputLabel.setText(message);
        outputLabel.setForeground(Color.GREEN);
        outputLabel.getParent().setVisible(true);
    }

    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        start();
    }
}


