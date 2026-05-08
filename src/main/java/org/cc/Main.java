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
        setSize(780, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // =========================
        // INPUT
        // =========================
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Arial", Font.PLAIN, 16));

        // =========================
        // OUTPUT
        // =========================
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Arial", Font.PLAIN, 16));
        outputArea.setEditable(false);

        // =========================
        // TOGGLE HOTKEY
        // =========================
        JToggleButton toggle = new JToggleButton("ON");
        toggle.setSelected(true);

        toggle.addActionListener(e -> {
            enabled = toggle.isSelected();
            toggle.setText(enabled ? "ON" : "OFF");
        });

        // =========================
        // CONVERT BUTTON
        // =========================
        JButton convertBtn = new JButton("Convert");
        convertBtn.addActionListener(e -> processClipboard());

        // =========================
        // CLEAR BUTTON 🔥
        // =========================
        JButton clearBtn = new JButton("Clear");

        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });

        // =========================
        // ALWAYS ON TOP
        // =========================
        JToggleButton topBtn = new JToggleButton("Top OFF");
        topBtn.setSelected(true);

        topBtn.addActionListener(e -> {

            boolean state = topBtn.isSelected();

            setAlwaysOnTop(state);

            topBtn.setText(state ? "Top ON" : "Top OFF");
        });

        // =========================
        // TOP PANEL
        // =========================
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        top.add(toggle);
        top.add(convertBtn);
        top.add(clearBtn);
        top.add(topBtn);

        // =========================
        // SPLIT
        // =========================
        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(inputArea),
                new JScrollPane(outputArea)
        );

        split.setDividerLocation(300);

        // =========================
        // HINT PANEL
        // =========================
        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));
        hintPanel.setBackground(new Color(25, 25, 25));
        hintPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("💡 Hotkeys & Tips:");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel h1 = new JLabel("CTRL + SHIFT + V → Convert clipboard");
        JLabel h2 = new JLabel("ALT + X → Quick convert");
        JLabel h3 = new JLabel("Clear → очистити поля");
        JLabel h4 = new JLabel("Top ON → поверх всіх вікон");

        for (JLabel l : new JLabel[]{h1, h2, h3, h4}) {
            l.setForeground(new Color(180, 180, 180));
        }

        hintPanel.add(title);
        hintPanel.add(Box.createVerticalStrut(5));
        hintPanel.add(h1);
        hintPanel.add(h2);
        hintPanel.add(h3);
        hintPanel.add(h4);

        // =========================
        // ADD UI
        // =========================
        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(hintPanel, BorderLayout.SOUTH);

        registerHotkey();
    }

    // =========================
    // HOTKEYS
    // =========================
    private void registerHotkey() {

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {

                    if (!enabled) return false;

                    if (e.getID() == KeyEvent.KEY_PRESSED) {

                        boolean ctrl = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;
                        boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
                        boolean alt = (e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != 0;

                        if (ctrl && shift && e.getKeyCode() == KeyEvent.VK_V) {
                            processClipboard();
                            return true;
                        }

                        if (alt && e.getKeyCode() == KeyEvent.VK_X) {
                            processClipboard();
                            return true;
                        }
                    }

                    return false;
                });
    }

    // =========================
    // CORE LOGIC
    // =========================
    private void processClipboard() {

        if (!enabled) return;

        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            if (!clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) return;

            String text = (String) clipboard.getData(DataFlavor.stringFlavor);

            if (text == null || text.isBlank()) return;

            inputArea.setText(text);

            String result = Arrays.stream(text.split("\\R"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(","));

            outputArea.setText(result);

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