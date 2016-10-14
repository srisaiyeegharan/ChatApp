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
    //Global variable stream
    private PrintStream stream;
    //Global variable m 
    private MessageProcessor m;
    
    
    public CommandLine(MessageProcessor msgProcessor) 
    {
        stream = System.out;
        m=msgProcessor;
    }

    @Override
    public void run() {
        
        //Welcome Message 
        stream.println("Welcome to Chat Messages");  
        //Testing purpose
        String sendAllMessage = "<ALL>Hello how are you";
        String sendPeerMessage = "<PM,136.186.14.85>Hello Peer How are you";
        String sendPeerFile = "<FILE,192.168.45.1,sri.jpg>";
        //Calling the userInput method
        userInput();
        
        
    }
    
    //Takes in the user input from command line 
    public void userInput()
    {
        while(true)
        {
            Scanner input = new Scanner(System.in);
        //scanner moves down after returning the current line.
        String line = input.nextLine();
        //passing the read String to stripMessage method
        stripMessage("<FILE,136.186.14.88,test.jpg>");
        }
        
    }
    
    public void stripMessage(String pMessage)
    { 
        
       stream.println("Lets Strip the Send Message");
       //regex that finds everything from < to >
       String regex = "<.+>";
       //regex that finds everything from > onwards
       String reg =">.+";
       
       String con = null;
       String message = null;
       //initialise messageString
       String messageString = "";
       
       Pattern pattern = Pattern.compile(regex);
       Matcher connection = pattern.matcher(pMessage);
      
       while (connection.find()) {
           //finding the string based on given regex
            con = connection.group();
            //prints the found string to console
            stream.println(con);
        }
       
       //getting rid of "<" from the string
       String conString = con.replace("<","");
       //getting rid of ">" from the string 
       String connectionString = conString.replace(">", "");
       //printing the string to console
       stream.println("Stripped"+connectionString);
       
       //ConnectionArray is a string array which contains 
       //the details from user input which has the mode,ip and filename
       String[] connectionArray;
       
       //split everything between <> by comma
       connectionArray = connectionString.split(",");
       //finding the length of array
       int connectionArrayLength = connectionArray.length;
       //looping through the connectionArray
       //for testing purpose
       for (int i = 0; i<connectionArrayLength; i++)
       {
           stream.println(connectionArray[i]);
       }
       //assigning mode the value
       String mode=connectionArray[0];
       
       //If the user input mode is file
       if (!"file".equals(mode.toLowerCase()))
       {
            Pattern msgPattern = Pattern.compile(reg);  
            Matcher stringMsg = msgPattern.matcher(pMessage);
             while (stringMsg.find())
             {
            message = stringMsg.group();
            stream.println(message);
         }
            messageString = message.replace(">", "");
           
       }
      
       //switch to call methods based on the mode from user input
       switch (mode.toLowerCase())
       {
           case "all":
               sendAll(mode,messageString);
               break;
           case "pm":
               sendPm(mode,connectionArray[1],messageString);
               break;
           case "file":
               sendFile( connectionArray[1], connectionArray[2]);
               break; 
           default:
               stream.println("Invalid Mode");
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
    public void sendFile( String pIp, String pFileName)
    {
        stream.println("Sending a File");
        stream.println("File" + " and " + pIp + " and " + pFileName);
        m.messageProcessorSendFile( pIp, pFileName);
    }
    public synchronized void writeRecievedMessage(String mode, String ip, String message)
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
