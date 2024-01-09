package server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    // Declare the port number
    private static int port = 3005;
    private static String filePath = "src/main/resources/data.json";
    private static ConcurrentHashMap<String, String> dictionaryData;
    // Identifies the user number connected
    private static int counter = 0;
    private static ServerGUI serverGUI = new ServerGUI();
    public Server(int port, String filePath) {

    }

    public static void main(String[] args)
    {

        if (args.length != 2 && args.length != 0) {
            System.err.println("Usage: java -jar Server.jar <port> <file_path>");
            System.exit(1);
        }
        if(args.length == 2)
        {
            port = Integer.parseInt(args[0]);
            filePath = args[1];
        }

        serverGUI.setVisible(true);

        try
        {
            ServerSocket server = new ServerSocket(port);
            server.setReuseAddress(true);
            //System.out.println("Waiting for client connection-");
            //read json file data into the concurrentHashMap structure
            File file = new File(filePath);

            // check the existence of the dictionary file
            if (!file.exists()) {
                try {
                    // if there is no such dictionary file, create an empty one
                    System.out.println("no such file in the given path, a new empty file has been created!");
                    serverGUI.displayMessage("no such file in the given path, a new empty file has been created!");
                    file.createNewFile();
                } catch (IOException e) {
                    System.err.println("file creation fail" + e.getMessage());
                    serverGUI.displayMessage("file creation fail" + e.getMessage());
                }
            }

            dictionaryData = readDictionaryFromFile(filePath);
            // Wait for connections.
            serverGUI.displayMessage("The server is running and waiting for a connection now! Port Number: " + port + "Time: " + displayCurrentTime());
            while(true)
            {
                Socket client = server.accept();
                counter++;
                System.out.println("Client "+counter+": Applying for connection!");
                System.out.println(client.getPort());
                System.out.println(client.getLocalPort());
                System.out.println(server.getLocalPort());

                serverGUI.displayMessage("Client "+counter+": Connection accepted at " + displayCurrentTime());
                serverGUI.displayMessage("Client port: " + client.getPort());
                serverGUI.displayMessage("Local port: "+client.getLocalPort());
                // Start a new thread for a connection
                Thread t = new Thread(() -> serveClient(client,counter));
                t.start();
            }

        }
//		}
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    //read the json file
    private static ConcurrentHashMap<String, String> readDictionaryFromFile(String filePath) {
        File jsonFile = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, String> dictionaryData = objectMapper.readValue(jsonFile, new TypeReference<Map<String, String>>() {});
            return new ConcurrentHashMap<>(dictionaryData);
        } catch (MismatchedInputException e) {
            // If the file is empty, return an empty ConcurrentHashMap.
            System.err.println("Warning: The JSON file is empty. Initializing an empty dictionary.");
            serverGUI.displayMessage("Warning: The JSON file is empty. Initializing an empty dictionary.");
            return new ConcurrentHashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Unable to read the JSON file.");
            serverGUI.displayMessage("Error: Unable to read the JSON file.");
            return null;
        }
    }


    //the following function create the threadClient class and process the command for each client thread, get the feedback of each operation
    private static void serveClient(Socket client, int number)
    {
        try(Socket clientSocket = client)
        {
            ServerClientThread serverClientThread = new ServerClientThread(client,number);
            // Input stream
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            // Output Stream
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            while (true)
            {
                String request = input.readUTF();
                if ("DISCONNECT".equals(request))
                {
                    serverGUI.displayMessage("Client "+serverClientThread.getClientNumber()+" disconnected.");
                    System.out.println("Client "+serverClientThread.getClientNumber()+" disconnected.");
                    break;
                }
                String[] parts = request.split(":",3);
                serverClientThread.setCommand(parts[0]);
                serverClientThread.setWord(parts[1]);
                String meanings = "";
                if(parts.length>2)
                {
                    meanings = parts[2];
                }
                switch (serverClientThread.getCommand()) {
                    case "QUERY":
                        // do the query operation
                        serverClientThread.query(dictionaryData);
                        serverGUI.displayMessage("request from client "+number+" : " + "QUERY : " +serverClientThread.getWord());
                        break;
                    case "ADD":
                        // do the add operation
                        serverClientThread.setMeanings(meanings);
                        serverClientThread.add(dictionaryData);
                        serverClientThread.writeDictionaryToFile(filePath, dictionaryData);
                        serverGUI.displayMessage("request from client "+number+" : " + "ADD : " +serverClientThread.getWord() + ":"+serverClientThread.getMeanings());
                        break;
                    case "DELETE":
                        // do the delete operation
                        serverClientThread.delete(dictionaryData);
                        serverClientThread.writeDictionaryToFile(filePath, dictionaryData);
                        serverGUI.displayMessage("request from client "+number+" : " + "DELETE : " +serverClientThread.getWord());
                        break;
                    case "UPDATE":
                        // do the update operation
                        serverClientThread.setMeanings(meanings);
                        serverClientThread.update(dictionaryData);
                        serverClientThread.writeDictionaryToFile(filePath, dictionaryData);
                        serverGUI.displayMessage("request from client "+number+" : " + "UPDATE : "+serverClientThread.getWord() + ":"+serverClientThread.getMeanings());
                        break;
                    default:
                        // invalid commands
                        break;
                }
                if(!serverClientThread.getFeedback().isEmpty())
                {
                    output.writeUTF(serverClientThread.getFeedback());
                    serverGUI.displayMessage("response sent!");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static String displayCurrentTime()
    {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);
        return formattedNow;
    }
}
