package org.cc;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main extends JFrame {

    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private boolean enabled = true;

    public Main() {

        setTitle("Word Formatter PRO");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // INPUT (for debug / view only)
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Arial", Font.PLAIN, 16));

        // OUTPUT
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Arial", Font.PLAIN, 16));
        outputArea.setEditable(false);

        // TOGGLE
        JToggleButton toggle = new JToggleButton("ON");
        toggle.setSelected(true);

        toggle.addActionListener(e -> {
            enabled = toggle.isSelected();
            toggle.setText(enabled ? "ON" : "OFF");
        });

        // BUTTON (backup)
        JButton btn = new JButton("Convert");
        btn.addActionListener(e -> processClipboard());

        JPanel top = new JPanel(new BorderLayout());
        top.add(toggle, BorderLayout.WEST);
        top.add(btn, BorderLayout.EAST);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(inputArea),
                new JScrollPane(outputArea)
        );

        split.setDividerLocation(200);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        registerHotkey();
    }

    // =========================
    // HOTKEY: CTRL + SHIFT + V
    // =========================
    private void registerHotkey() {

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {

                    if (!enabled) return false;

                    if (e.getID() == KeyEvent.KEY_PRESSED) {

                        boolean ctrl = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;
                        boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;

                        if (ctrl && shift && e.getKeyCode() == KeyEvent.VK_V) {
                            processClipboard();
                            return true;
                        }
                    }

                    return false;
                });
    }

    // =========================
    // CORE: clipboard → convert → clipboard
    // =========================
    private void processClipboard() {

        if (!enabled) return;

        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            if (!clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) return;

            String text = (String) clipboard.getData(DataFlavor.stringFlavor);

            if (text == null || text.isBlank()) return;

            // show in input field (optional)
            inputArea.setText(text);

            String result = Arrays.stream(text.split("\\R"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(","));

            outputArea.setText(result);

            // overwrite clipboard
            clipboard.setContents(new StringSelection(result), null);

        } catch (Exception ignored) {}
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