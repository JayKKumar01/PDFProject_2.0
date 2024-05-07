package pdfproject.window;

import pdfproject.window.utils.CustomOutputStream;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.PrintStream;

public class Window {
    public Window(int w, int h) {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.getContentPane().setLayout(new BorderLayout());

        // Set dark mode colors
        Color darkGray = new Color(40, 40, 40);
        Color lightGray = new Color(70, 70, 70);
        Color textColor = Color.WHITE;

        JPanel mainContent = new JPanel();
        mainContent.setBackground(lightGray);
        int mainContentHeight = h * 3 / 5;
        mainContent.setPreferredSize(new Dimension(w, mainContentHeight));

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
