package chatapp;
import java.io.InputStream;
import java.io.PrintStream;
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
    private InputStream inpStream;
    
    public CommandLine() 
    {
        stream=System.out;
        inpStream=System.in;
        
    }

    @Override
    public void run() {
      stream.println("Welcome to Chat Messages");  
        String sendAllMessage = "<ALL>Hello how are you";
        String sendPeerMessage = "<PM,192.168.45.1>Hello Peer How are you";
        String sendPeerFile = "<FILE,192.168.45.1,sri.jpg>";
        stripMessage(sendPeerFile);
    }
    
    public void stripMessage(String pMessage)
    { 
       System.out.println("Lets Strip the Send Message");
       String regex = "<.+>";
       String reg =">.+";
       String con = null;
       String messageString = "";
       //String reg = "\\<(.*?)\\>";
       String message = null;
       Pattern pattern = Pattern.compile(regex);
       Matcher connection = pattern.matcher(pMessage);
      
       while (connection.find()) {
            con = connection.group();
            System.out.println(con);
        }
       String conString = con.replace("<","");
       String connectionString = conString.replace(">", "");
       System.out.println(connectionString);
       
       String[] connectionArray;
       
       connectionArray = connectionString.split(",");
       int connectionArrayLength = connectionArray.length;
       for (int i = 0; i<connectionArrayLength; i++)
       {
           System.out.println(connectionArray[i]);
       }
       
       if (!"file".equals(connectionArray[0].toLowerCase()))
       {
            Pattern msgPattern = Pattern.compile(reg);
            Matcher stringMsg = msgPattern.matcher(pMessage);
             while (stringMsg.find()) {
            message = stringMsg.group();
            System.out.println(message);
         }
            messageString = message.replace(">", "");
           
       }
       switch (connectionArray[0].toLowerCase())
       {
           case "all":
               sendAll(connectionArray[0],messageString);
               break;
           case "pm":
               sendPm(connectionArray[0],connectionArray[1],messageString);
               break;
           case "file":
               sendFile(connectionArray[0], connectionArray[1], connectionArray[2]);
               break;    
       }
    }   
    
    public void sendAll(String pMode, String pMessage)
    {           
        System.out.println("Sending Everyone a message");
        System.out.println(pMode + " and " + pMessage);
    }
    public void sendPm(String pMode, String pIp, String pMessage) 
    {
        System.out.println("Sending a private message");
        System.out.println(pMode + " and " + pIp + " and " + pMessage);
    }
    public void sendFile(String pMode, String pIp, String pFileName)
    {
        System.out.println("Sending a File");
        System.out.println(pMode + " and " + pIp + " and " + pFileName);
    }
    public void recieveMessage(String mode, String ip,String message)
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
    public void recieveMessage(String ip, String file)
    {
      
    }
}
