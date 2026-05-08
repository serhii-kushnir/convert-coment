package org.cc;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main extends JFrame {

    private final JTextArea outputArea;
    private boolean enabled = true;

    private String lastClipboard = "";

    public Main() {

        setTitle("Word Formatter PRO");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // OUTPUT AREA
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Arial", Font.PLAIN, 16));
        outputArea.setEditable(false);

        // TOGGLE BUTTON
        JToggleButton toggleButton = new JToggleButton("ON");
        toggleButton.setSelected(true);

        toggleButton.addActionListener(e -> {
            enabled = toggleButton.isSelected();
            toggleButton.setText(enabled ? "ON" : "OFF");
        });

        // LAYOUT
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(toggleButton, BorderLayout.SOUTH);

        // START BACKGROUND CLIPBOARD WATCHER
        startClipboardWatcher();
    }

    // =========================
    // CLIPBOARD WATCHER (BACKGROUND)
    // =========================
    private void startClipboardWatcher() {

        Thread watcher = new Thread(() -> {

            while (true) {

                if (!enabled) {
                    sleep(300);
                    continue;
                }

                try {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                    if (!clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                        sleep(300);
                        continue;
                    }

                    String data = (String) clipboard.getData(DataFlavor.stringFlavor);

                    if (data == null || data.equals(lastClipboard)) {
                        sleep(300);
                        continue;
                    }

                    lastClipboard = data;

                    String converted = convert(data);

                    // update clipboard
                    clipboard.setContents(new StringSelection(converted), null);

                    // update UI
                    SwingUtilities.invokeLater(() -> {
                        outputArea.setText(converted);
                    });

                } catch (Exception ignored) {}

                sleep(300);
            }
        });

        watcher.setDaemon(true);
        watcher.start();
    }

    // =========================
    // CONVERT LOGIC
    // =========================
    private String convert(String text) {

        return Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }

    // =========================
    // SLEEP HELPER
    // =========================
    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    // =========================
    // MAIN
    // =========================
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Main window = new Main();
            window.setVisible(true);
        });
    }
}