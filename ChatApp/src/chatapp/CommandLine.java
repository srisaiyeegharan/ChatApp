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
    private MessageProcessor msgProcsr;
    private volatile boolean running=true;
    
    public CommandLine(MessageProcessor msgProcessor) 
    {
        stream = System.out;
        msgProcsr=msgProcessor;
    }
    
    public void terminate()
    {
        running=false;
    }

    @Override
    public void run() {
        
        //Welcome Message 
        stream.println("Welcome to Chat Messages");  
        //Testing purpose
        String sendAllMessage = "Send <ALL>Hello how are you";
        String sendPeerMessage = "Send <PM,136.186.14.88>Hello Peer How are you";
        String sendPeerFile = "Send <FILE,192.168.45.1,sri.jpg>";
        String getIPS="View <IPs>";
        String quit="Quit<>";
        //Calling the userInput method
        userInput();
        
        
        
    }
    
    //Takes in the user input from command line 
    public void userInput()
    {
        while(running)
        {
            Scanner input = new Scanner(System.in);
        //scanner moves down after returning the current line.
        String line = input.nextLine();
        //passing the read String to stripMessage method
        line="Quit<>";
        //extract command  prefix
        String prefix =extractCmdPrefix(line);
        if(prefix.equals(""))
        {
            //invalid prefix format
            stream.println("Invalid Prefix format in Command");
            continue;
        }
        
        //trim any sapces
        prefix=prefix.replace("<", "");
        //command to execute
        String command=line.replace(prefix, "");
        
        
            System.out.println("command to execute "+command);
            System.out.println("prefix "+prefix);
        
        switch(prefix.toLowerCase().trim())
        {
            case "send":
                stripSendCmd(command);
                break;
            case "view":
                stripViewCmd(command);
                break;
            case "quit":
                quitApp();
                break;
            default:
                stream.println("Prefix Command not supported");
                    
        }
        //stripSendCmd("<ALL>Hello how are you");
        //stripViewCmd("<IPs>");

        }
        System.out.println("Command Line exiting");
        
    }
    
    public void stripViewCmd(String pCommand)
    {
        pCommand=pCommand.trim();
        String checkedString=stripCmd(pCommand);
        if(checkedString.equals(""))
        {
            stream.println("Invalid VIEW Command");
            return;
        }
        
        if(checkedString.toLowerCase().equals("ips"))
        {
            stream.println("Hosts Connected");
            String members=msgProcsr.getGroupChatMembers();
            stream.println(members);
        }
        
    }
    public void stripSendCmd(String pMessage)
    { 
        pMessage=pMessage.trim();
       stream.println("Lets Strip the Send Message");
      //check command matches format and process it
       String connectionString=stripCmd(pMessage);
       if(connectionString.equals(""))
       {
           stream.println("Invalid SEND Command");
           return;
       }
       //regex that finds everything from > onwards
       String reg =">.+";
       
       
       String message = null;
       //initialise messageString
       String messageString = "";
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
    public String stripCmd(String pCommand)
    {
        //regex that finds everything from < to >
       String regex = "<.+>";
       String con = null;
       
       
       Pattern pattern = Pattern.compile(regex);
       Matcher connection = pattern.matcher(pCommand);
      
       while (connection.find()) {
           //finding the string based on given regex
            con = connection.group();
            //prints the found string to console
            stream.println(con);
        }
       if(con==null || con.equals(""))
       {
           return new String("");
       }
       //getting rid of "<" from the string
       String conString = con.replace("<","");
       //getting rid of ">" from the string 
       String connectionString = conString.replace(">", "");
       //printing the string to console
       stream.println("Stripped "+connectionString); 
       
       return connectionString;
    }
    
    public String extractCmdPrefix(String pCommand)
    {
          //regex that finds everything to ... <
       String regex = ".+<";
       String con = null;
       
       
       Pattern pattern = Pattern.compile(regex);
       Matcher connection = pattern.matcher(pCommand);
      
       while (connection.find()) {
           //finding the string based on given regex
            con = connection.group();
            //prints the found string to console
            stream.println("extracted"+con);
            break;
        }
       if(con==null || con.equals(""))
       {
           return new String("");
       }
       
       return con;
    }
    
    
    public void quitApp()
    {
        //send BYE message to all peers
        String byeMessage="*BYE*";
        msgProcsr.messageProcessorSendAll("all", byeMessage);
        //terminate app
        stream.println("Exiting App");
        msgProcsr.terminateApp();
         
    }
    public void sendAll(String pMode, String pMessage)
    {           
        stream.println("Sending Everyone a message");
        stream.println(pMode + " and " + pMessage);
        msgProcsr.messageProcessorSendAll(pMode, pMessage);
    }
    public void sendPm(String pMode, String pIp, String pMessage) 
    {
        stream.println("Sending a private message");
        stream.println(pMode + " and " + pIp + " and " + pMessage);
        msgProcsr.messageProcessorSendPm(pMode, pIp, pMessage);
    }
    public void sendFile( String pIp, String pFileName)
    {
        stream.println("Sending a File");
        stream.println("File" + " and " + pIp + " and " + pFileName);
        msgProcsr.messageProcessorSendFile( pIp, pFileName);
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
        stream.println("File Reciept");
        stream.println("Recieving File:"+file+" from "+ip);
    }
}
