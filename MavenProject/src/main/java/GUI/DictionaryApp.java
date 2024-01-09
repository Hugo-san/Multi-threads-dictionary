/*
 * description:
 * @author:
 */
package GUI;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DictionaryApp extends JFrame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dictionary App");
        // ...
        if (args.length != 2 && args.length != 0) {
            System.err.println("Usage: java -jar Server.jar <port> <file_path>");
            System.exit(1);
        }
        String server_address = "localhost";
        int server_port = 3005;
        if (args.length == 2)
        {
            server_address = args[0];
            server_port = Integer.parseInt(args[1]);
        }

        ClientGUI clientGUI= new ClientGUI(server_address,server_port);
        frame.setContentPane(clientGUI.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clientGUI.disconnect();
            }
        });
        frame.pack();
        frame.setVisible(true);
    }
}
