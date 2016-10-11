package chatapp;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Srisaiyeegharan
 */

public class CommandLine extends Thread {
    private PrintStream stream;
    MessageProcessor m = new MessageProcessor();
    
    public CommandLine() 
    {
        stream = System.out;
        
    }

    @Override
    public void run() {
        stream.println("Welcome to Chat Messages");  
        String sendAllMessage = "<ALL>Hello how are you";
        String sendPeerMessage = "<PM,192.168.45.1>Hello Peer How are you";
        String sendPeerFile = "<FILE,192.168.45.1,sri.jpg>";
         userInput();
        
        
    }
    
    //Takes in the user input from command line 
    public void userInput()
    {
        Scanner input = new Scanner(System.in);
        String line = input.nextLine();
        stripMessage(line);
    }
    
    public void stripMessage(String pMessage)
    { 
       stream.println("Lets Strip the Send Message");
       String regex = "<.+>";
       String reg =">.+";
       String con = null;
       String message = null;
       String messageString = "";
       Pattern pattern = Pattern.compile(regex);
       Matcher connection = pattern.matcher(pMessage);
      
       while (connection.find()) {
            con = connection.group();
            stream.println(con);
        }
       String conString = con.replace("<","");
       String connectionString = conString.replace(">", "");
       stream.println(connectionString);
       
       String[] connectionArray;
       
       connectionArray = connectionString.split(",");
       int connectionArrayLength = connectionArray.length;
       for (int i = 0; i<connectionArrayLength; i++)
       {
           stream.println(connectionArray[i]);
       }
       
       if (!"file".equals(connectionArray[0].toLowerCase()))
       {
            Pattern msgPattern = Pattern.compile(reg);
            Matcher stringMsg = msgPattern.matcher(pMessage);
             while (stringMsg.find()) {
            message = stringMsg.group();
            stream.println(message);
         }
            messageString = message.replace(">", "");
           
       }
       switch (connectionArray[0].toLowerCase())
       {
           case "all":
               sendAll(connectionArray[0].toLowerCase(),messageString);
               break;
           case "pm":
               sendPm(connectionArray[0].toLowerCase(),connectionArray[1],messageString);
               break;
           case "file":
               sendFile(connectionArray[0].toLowerCase(), connectionArray[1], connectionArray[2]);
               break;    
       }
    }   
    
    public void sendAll(String pMode, String pMessage)
    {           
        stream.println("Sending Everyone a message");
        stream.println(pMode + " and " + pMessage);
        m.messageProcessorSendAll(pMode, pMessage);
    }
    public void sendPm(String pMode, String pIp, String pMessage) 
    {
        stream.println("Sending a private message");
        stream.println(pMode + " and " + pIp + " and " + pMessage);
        m.messageProcessorSendPm(pMode, pIp, pMessage);
    }
    public void sendFile(String pMode, String pIp, String pFileName)
    {
        stream.println("Sending a File");
        stream.println(pMode + " and " + pIp + " and " + pFileName);
        m.messageProcessorSendFile(pMode, pIp, pFileName);
    }
    public void writeRecievedMessage(String mode, String ip, String message)
    {
       String recievedMessage;
       switch (mode.toLowerCase())
       {
           case "all":
               recievedMessage = "Broadcast From " + ip + ": " + message;
               stream.print(recievedMessage);
               break;
           case "pm":
               recievedMessage = "PM From " + ip + ": " + message;
               stream.print(recievedMessage);
               break;
        }
    }
    public void writeRecievedFile(String ip, String file)
    {
        stream.println("Recieving a File");
    }
}
