package pdfproject.window;

import pdfproject.window.utils.CustomOutputStream;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.PrintStream;

public class Window {
    public Window(int h) {
        JFrame jFrame = new JFrame("PDFProject 2.0");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.getContentPane().setLayout(new BorderLayout());

        // Set dark mode colors
        Color darkGray = new Color(40, 40, 40);
        Color lightGray = new Color(70, 70, 70);
        Color textColor = Color.WHITE;

        JPanel mainContent = new JPanel();
        mainContent.setBackground(lightGray);
        int w = h * 16 / 9;
        int mainContentHeight = h * 3 / 5;
        mainContent.setPreferredSize(new Dimension(w, mainContentHeight));




        mainContent.setLayout(null);

        JPanel fileSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton browseButton = new JButton("Browse...");
        browseButton.setFocusable(false);
        JLabel chosenFileLabel = new JLabel("No file chosen");

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                chosenFileLabel.setText(fileChooser.getSelectedFile().getName());
            }
        });

        fileSelectionPanel.setBounds(0,100,300,40);

        fileSelectionPanel.add(browseButton);
        fileSelectionPanel.add(chosenFileLabel);

        mainContent.add(fileSelectionPanel);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        settingsPanel.setBounds(400, 20, 450, 250); // Adjust these values if necessary
        settingsPanel.setBackground(new Color(60, 60, 60));

        settingsPanel.setLayout(null);

        // Label for image quality
        JLabel qualityLabel = new JLabel("Image Quality:");
        qualityLabel.setForeground(Color.WHITE);
        qualityLabel.setBounds(10, 10, 100, 25); // Adjust bounds as needed

        // ComboBox for image quality options
        String[] qualities = { "Low", "Medium", "High" };
        JComboBox<String> qualityComboBox = new JComboBox<>(qualities);
        qualityComboBox.setBounds(120, 10, 100, 25); // Adjust bounds as needed

        // Add components to settingsPanel
        settingsPanel.add(qualityLabel);
        settingsPanel.add(qualityComboBox);

        JLabel operationColorsLabel = new JLabel("Select Operation Colors:");
        operationColorsLabel.setForeground(Color.WHITE);
        operationColorsLabel.setBounds(10, 50, 200, 25);

        settingsPanel.add(operationColorsLabel);

        // Color options
        String[] colors = { "Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Cyan", "Magenta", "Gray", "Black" };

        // Create operation color settings with default values
        addOperationColorSetting(settingsPanel, "Deleted", colors, 80, "Red", true);
        addOperationColorSetting(settingsPanel, "Added", colors, 110, "Green", true);
        addOperationColorSetting(settingsPanel, "Font Name", colors, 140, "Magenta", true);
        addOperationColorSetting(settingsPanel, "Font Size", colors, 80, "Blue", false);
        addOperationColorSetting(settingsPanel, "Font Style", colors, 110, "Cyan", false);
        addOperationColorSetting(settingsPanel, "Multiple Operation", colors, 140, "Black", false);

        mainContent.add(settingsPanel);


        JTextArea console = new JTextArea();
        console.setBackground(darkGray);
        console.setForeground(textColor);
        console.setEditable(false);
        int consoleHeight = h - mainContentHeight;


        // Wrap the JTextArea in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setPreferredSize(new Dimension(w, consoleHeight));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Ensure the caret always scrolls to the bottom
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        jFrame.getContentPane().add(mainContent, BorderLayout.CENTER);
        jFrame.getContentPane().add(scrollPane, BorderLayout.SOUTH);

        // Redirect System.out to JTextArea console
        PrintStream printStream = new PrintStream(new CustomOutputStream(console));
        System.setOut(printStream);

        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        print();
    }

    private void addOperationColorSetting(JPanel panel, String operation, String[] colors, int yPosition, String defaultColor, boolean isLeft) {
        int xOffset = isLeft ? 10 : 230;
        JLabel label = new JLabel(operation + ":");
        label.setForeground(Color.WHITE);
        label.setBounds(xOffset, yPosition, 100, 25);

        JComboBox<String> comboBox = new JComboBox<>(colors);
        comboBox.setSelectedItem(defaultColor);
        comboBox.setBounds(xOffset + 110, yPosition, 100, 25);

        panel.add(label);
        panel.add(comboBox);
    }

    private void print(){
        try {
            Thread.sleep(2000);
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
            System.out.println("Hi console!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
