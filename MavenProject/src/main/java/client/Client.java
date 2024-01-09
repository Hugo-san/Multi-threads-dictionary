/*
 * description:
 * @author:
 */
package client;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    // IP and port
    private static String ip = "localhost";
    private int port = 3005;
    private String request = "";
    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataInputStream getInput() {
        return input;
    }

    public void setInput(DataInputStream input) {
        this.input = input;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public void setOutput(DataOutputStream output) {
        this.output = output;
    }

    public Client(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        try{
            this.socket = new Socket(ip, port);
        }
        catch (UnknownHostException unknownHostException)
        {

        }
        //create the TCP link when being initialized

        // Output and Input Stream
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());

    }


    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
    public boolean isSocketConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void disconnect() {
        try {
            if (output != null) {
                output.writeUTF("DISCONNECT");
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
