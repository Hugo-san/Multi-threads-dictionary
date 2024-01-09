/*
 * description:
 * @author:
 */
package server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerClientThread {
    private Socket clientSocket;
    private int clientNumber;
    private String command;
    private String word;
    private String meanings;
    private String feedback;
    public ServerClientThread(Socket clientSocket, int clientNumber) {
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
        this.feedback="";
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeanings() {
        return meanings;
    }

    public void setMeanings(String meanings) {
        this.meanings = meanings;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public void query(ConcurrentHashMap<String, String> dictionaryData)
    {
        if(dictionaryData.containsKey(this.word))
        {
            setMeanings(dictionaryData.get(this.word));
            setFeedback(getMeanings());
        }
        else
        {
            setFeedback("the input word does not exist in the dictionary");
        }
    }
    public void add(ConcurrentHashMap<String, String> dictionaryData)
    {
        if(!dictionaryData.containsKey(this.word))
        {
            dictionaryData.put(this.word, this.meanings);
            setFeedback(this.word + " has been added successfully!");
        }
        else
        {
            setFeedback("this word has already been added before");
        }
    }
    public void delete(ConcurrentHashMap<String, String> dictionaryData)
    {
        if(dictionaryData.containsKey(this.word))
        {
            dictionaryData.remove(this.word);
            setFeedback(this.word + " has been deleted successfully!");
        }
        else
        {
            setFeedback("the input word does not exist in the dictionary");
        }
    }
    public void update(ConcurrentHashMap<String, String> dictionaryData)
    {
        if(dictionaryData.containsKey(this.word))
        {
            dictionaryData.replace(this.word, this.meanings);
            setFeedback(this.word + " has been updated successfully!");
        }
        else
        {
            setFeedback("the input word does not exist in the dictionary");
        }
    }
    //write the concurrentHashMap data structure
    public void writeDictionaryToFile(String filePath, ConcurrentHashMap<String, String> dictionaryData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File jsonFile = new File(filePath);
            objectMapper.writeValue(jsonFile, dictionaryData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
