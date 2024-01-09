/*
 * description:
 * @author:
 */
package GUI;

import client.Client;
import common.NoMeaningsAddedException;
import common.NoWordInputException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;

public class ClientGUI {
    private static final int PORT = 3005;
    private JPanel mainPanel;
    private JTextField input;
    private JButton queryButton;
    private JTextArea output;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;

    private Client client;
    public JPanel getMainPanel() {
        return mainPanel;
    }
    private String server_address;
    private int server_port;

    public String getServer_address() {
        return server_address;
    }

    public void setServer_address(String server_address) {
        this.server_address = server_address;
    }

    public int getServer_port() {
        return server_port;
    }

    public void setServer_port(int server_port) {
        this.server_port = server_port;
    }

    public ClientGUI(String server_address,int server_port)
    {
        //each launch of GUI will have a corresponding client instance
        try {
            client =new Client(server_address,server_port);
        } catch (ConnectException connectException)
        {
            output.setText("cannot connect to the given server! Maybe the server is not running! Please exit and retry after making sure the server is alive!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        output.setEditable(false);

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    String[] parts = generateRequest("QUERY").split(":", 3);
                    if(parts.length<2)
                        throw new NoWordInputException("No word input");
                    try
                    {
                        if (client.isSocketConnected()) {
                            client.getOutput().writeUTF(client.getRequest());
                            client.getOutput().flush();
                            output.setText(client.getInput().readUTF());
                        } else {
                            System.out.println("The socket is not connected. Please reconnect and try again.");
                        }
                    }
                    catch (IOException IOexception)
                    {
                        IOexception.printStackTrace();
                    }
                }
                catch (NoWordInputException noWordInputException)
                {
                    //show the error message to the user
                    output.setText(noWordInputException.getMessage());
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check the validity of input first
                try
                {
                    String[] parts = generateRequest("ADD").split(":", 3);
                    if(parts.length<3)
                        throw new NoMeaningsAddedException("You should also input the meanings for this word to add and use : to interval the meanings with the word itself");
                    try
                    {
                        client.getOutput().writeUTF(client.getRequest());
                        client.getOutput().flush();
                        output.setText(client.getInput().readUTF());
                    }
                    catch (IOException IOexception)
                    {
                        IOexception.printStackTrace();
                    }
                }
                catch (NoMeaningsAddedException noMeaningsAddedException)
                {
                    //show the error message to the user
                    output.setText(noMeaningsAddedException.getMessage());
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    String[] parts = generateRequest("DELETE").split(":", 3);
                    if(parts.length<2)
                        throw new NoWordInputException("No word input");
                    try
                    {
                        client.getOutput().writeUTF(client.getRequest());
                        client.getOutput().flush();
                        output.setText(client.getInput().readUTF());
                    }
                    catch (IOException IOexception)
                    {
                        IOexception.printStackTrace();
                    }
                }
                catch (NoWordInputException noWordInputException)
                {
                    //show the error message to the user
                    output.setText(noWordInputException.getMessage());
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check the validity of input first
                try
                {
                    String[] parts = generateRequest("UPDATE").split(":", 3);
                    if(parts.length<3)
                        throw new NoMeaningsAddedException("You should also input the meanings for this word to add and use : to interval the meanings with the word itself");
                    try
                    {
                        client.getOutput().writeUTF(client.getRequest());
                        client.getOutput().flush();
                        output.setText(client.getInput().readUTF());
                    }
                    catch (IOException IOexception)
                    {
                        IOexception.printStackTrace();
                    }
                }
                catch (NoMeaningsAddedException noMeaningsAddedException)
                {
                    //show the error message to the user
                    output.setText(noMeaningsAddedException.getMessage());
                }
            }
        });
    }

    public String generateRequest(String action)
    {
        String inputWord = this.input.getText();
        if(!inputWord.isEmpty())
        {
            String request = action + ":" + inputWord;
            this.client.setRequest(request);
            return request;
        }
        return "";
    }

    public void disconnect()
    {
        try
        {
            client.disconnect();
        }
        catch (NullPointerException nullPointerException)
        {
            //do nothing just end
        }
    }

}
