/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.injector.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProcessSelector extends JDialog {

    public ProcessSelector(JFrame parent, JTextField pidField) {
        super(parent, "Select Java Process", true);

        setSize(600, 400);
        setLayout(new BorderLayout());

        JList<String> processList = new JList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        getProcessChoices().forEach((pid, name) -> model.addElement(pid + " - " + name));
        processList.setModel(model);

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> {
            String selectedProcess = processList.getSelectedValue();
            if (selectedProcess != null) {
                String pid = selectedProcess.split(" ")[0];
                pidField.setText(pid);
                dispose();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(processList), BorderLayout.CENTER);
        panel.add(selectButton, BorderLayout.SOUTH);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(panel);
        setLocationRelativeTo(parent);
    }

    private static Map<Long, String> getProcessChoices() {
        try {
            OperatingSystem os = getOperatingSystem();

            Map<Long, String> processInfos = new HashMap<>();

            if (os == OperatingSystem.WINDOWS) {
                String[] commands = {"tasklist /FI \"IMAGENAME eq javaw.exe\" /V", "tasklist /FI \"IMAGENAME eq java.exe\" /V"};
                for (String cmd : commands) {
                    Process p = Runtime.getRuntime().exec(cmd);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.isEmpty() && !line.startsWith("Image Name") && !line.startsWith("=============") && !line.startsWith("INFO:")) {
                                long pid = Long.parseLong(line.substring(26, 34).trim());
                                String windowTitle = line.substring(157).trim();
                                processInfos.put(pid, windowTitle);
                            }
                        }
                    }
                }
            } else if (os == OperatingSystem.LINUX || os == OperatingSystem.MACOS) {
                Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "ps -e -o pid,comm,args --no-headers | grep 'java' | grep -v 'grep'"});
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.trim().split("\\s+", 3);
                        if (parts.length >= 3) {
                            long pid = Long.parseLong(parts[0]);
                            String command = parts[1];
                            String args = parts[2];
                            String processName = command + " " + args;
                            processInfos.put(pid, processName);
                        }
                    }
                }
            } else {
                throw new UnsupportedOperationException("Unsupported operating system");
            }

            return processInfos.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty() && !entry.getValue().equals("N/A") && !entry.getValue().equals(Gui.TITLE))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            throw new RuntimeException("Failed to get process choices", e);
        }
    }

    private static OperatingSystem getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OperatingSystem.WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux")) {
            return OperatingSystem.LINUX;
        } else if (osName.contains("mac")) {
            return OperatingSystem.MACOS;
        } else {
            return OperatingSystem.OTHER;
        }
    }

    private enum OperatingSystem {
        WINDOWS, LINUX, MACOS, OTHER
    }
}
