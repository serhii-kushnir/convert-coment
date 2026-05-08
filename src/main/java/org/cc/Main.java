package org.cc;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main extends JFrame {

    private final JTextArea inputArea;
    private final JTextArea outputArea;

    public Main() {

        setTitle("Word Formatter");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Поле вводу
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Arial", Font.PLAIN, 16));

        // Поле результату
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Arial", Font.PLAIN, 16));
        outputArea.setEditable(false);

        // Кнопка
        JButton convertButton = new JButton("Конвертувати та скопіювати");

        convertButton.addActionListener(e -> convertText());

        // Панель
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        panel.add(new JScrollPane(inputArea));
        panel.add(new JScrollPane(outputArea));

        add(panel, BorderLayout.CENTER);
        add(convertButton, BorderLayout.SOUTH);
    }

    private void convertText() {

        String input = inputArea.getText();

        String result = Arrays.stream(input.split("\\R"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));

        // Вивести результат
        outputArea.setText(result);

        // Копіювати в буфер
        StringSelection selection = new StringSelection(result);

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(selection, null);

        //JOptionPane.showMessageDialog(this,
        //        "Скопійовано в буфер обміну!");
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Main window = new Main();
            window.setVisible(true);
        });
    }
}