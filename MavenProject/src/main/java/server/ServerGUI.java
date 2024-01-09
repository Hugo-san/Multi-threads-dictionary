/*
 * description:
 * @author:
 */
package server;

import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {
    private JTextArea messageArea;

    public ServerGUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Server");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false); // 设置为只读
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void displayMessage(String message) {
        messageArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI serverGUI = new ServerGUI();
            serverGUI.setVisible(true);
        });
    }
}
