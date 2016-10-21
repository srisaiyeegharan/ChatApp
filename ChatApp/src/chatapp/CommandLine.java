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
 * 
 */

public class CommandLine extends Thread {
    //Global variable stream
    private PrintStream stream;
    //Global variable m 
    private MessageProcessor msgProcsr;
    private volatile boolean running=true;
    private volatile Scanner cmdInput;
    
    /**
     * Create a new instance of CommandLine
     * @param msgProcessor
     */
    public CommandLine(MessageProcessor msgProcessor) 
    {
        cmdInput= new Scanner(System.in);
        stream = System.out;
        msgProcsr=msgProcessor;
    }
    
    /**
     * Terminate File Server 
     */
    public void terminate()
    {
        running=false;
    }

    /**
     * Start running Thread
     */
    @Override
    public void run() {
        
        //Welcome Message 
        stream.println("Welcome to Chat Messages");  
        //Testing purpose
        String sendAllMessage = "Send <ALL>Hello how are you";
        String sendPeerMessage = "Send <PM,192.168.45.1>Hello Peer How are you";
        String sendPeerFile = "Send <FILE,192.168.45.1,sri.jpg>";
        String getIPS="View <IPs>";
        String quit="Quit<>";
        String viewCommands="View <Commands>";
        //Calling the userInput method
        userInput();   
    }
    
    

    /**
     * Takes in the user input from command line and sends it to respective methods to strip the command
     */
        public void userInput()
    {
        while(running)
        {
         
        //scanner moves down after returning the current line.
        String line = cmdInput.nextLine();
        //passing the read String to stripMessage method
        
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
        
        
            ChatApp.logger.info("command to execute "+command);
            ChatApp.logger.info("prefix "+prefix);
        
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
        ChatApp.logger.info("Command Line exiting");
        
    }
    
    /**
     * Strips the "View" Command to its respective parts
     * @param pCommand
     */
    public void stripViewCmd(String pCommand)
    {
        pCommand=pCommand.trim();
        String checkedString=stripCmd(pCommand);
        checkedString = checkedString.trim();
        stream.println(checkedString);
        checkedString = checkedString.replaceAll("\\s", "");
        
        stream.println(checkedString);
        
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
        else if(checkedString.toLowerCase().equals("commands"))
        {
            showCommands();
        }
        
    }

    /**
     * Strips the "Send" Command to its respective parts
     * @param pMessage
     */
    public void stripSendCmd(String pMessage)
    { 
       pMessage=pMessage.trim();
       ChatApp.logger.info("Lets Strip the Send Message");
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
           ChatApp.logger.info(connectionArray[i]);
       }
       //assigning mode the value
       String mode=connectionArray[0].replaceAll("\\s","");
       
       //If the user input mode is not a file then get the message
       if (!"file".equals(mode.toLowerCase()))
       {
            Pattern msgPattern = Pattern.compile(reg);  
            Matcher stringMsg = msgPattern.matcher(pMessage);
            while (stringMsg.find()){
            message = stringMsg.group();
            ChatApp.logger.info(message);
            }
            if (message == null){
                stream.println("Enter a message to be sent");
                return;
            }
            else {
                messageString = message.replace(">", "");
            }
       }
     
       if ("file".equals(mode.toLowerCase()))
       {
            Pattern msgPattern = Pattern.compile(reg);
            Matcher stringMsg = msgPattern.matcher(pMessage);
            stream.println(stringMsg.find());
            boolean stringFind = stringMsg.find();
            if (stringFind)
            {
                stream.println("Message not supported for File Transfer");
                return;  
            }
            else
            {
                stream.println("Good work");
            }
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

    /**
     * Method which finds the user input between < > 
     * @param pCommand
     * @return
     */
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
            ChatApp.logger.info(con);
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
       ChatApp.logger.info("Stripped "+connectionString); 
       
       return connectionString;
    }
    
    /**
     * Method which finds the user input prefix before <..>
     * @param pCommand
     * @return
     */
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
            ChatApp.logger.info("extracted"+con);
            break;
        }
       if(con==null || con.equals(""))
       {
           return new String("");
       }
       
       return con;
    }
    
    private void showCommands()
    {
        stream.println("Commands supported");
        StringBuilder builder= new StringBuilder();
        builder.append("Send <ALL>Hello how are you   -Send Message to All in Group\n");
        builder.append("Send <PM,136.186.14.88>Hello Peer How are you   -Send Message to a single host\n");
        builder.append("Send <FILE,192.168.45.1,sri.jpg>   -Send File to a single host\n");
        builder.append("View <IPs>   -View all IPs and names connected\n");
        builder.append("Quit <>   -Quit Application\n");
        builder.append("View <Commands>   -View all commands supported\n");
        stream.println(builder.toString());
        
    }
    
    /**
     *  Terminates the client from ChatApp 
     */
    public void quitApp()
    {
        //send BYE message to all peers
        String byeMessage="*BYE*";
        msgProcsr.messageProcessorSendAll("all", byeMessage);
        //terminate app
        stream.println("Exiting App");
        msgProcsr.terminateApp();
         
    }

    /**
     * Propagates the Send ALL user input to messageProcessor
     * @param pMode
     * @param pMessage
     */
    public void sendAll(String pMode, String pMessage)
    {
        pMode=pMode.trim();
        pMessage=pMessage.trim();
        ChatApp.logger.info("Sending Everyone a message");
        ChatApp.logger.info(pMode + " and " + pMessage);
        msgProcsr.messageProcessorSendAll(pMode, pMessage);
    }

    /**
     * Propagates the Send PM user input to messageProcessor
     * @param pMode
     * @param pIp
     * @param pMessage
     */
    public void sendPm(String pMode, String pIp, String pMessage) 
    {
        pMode=pMode.trim();
        pIp=pIp.trim();
        pIp = pIp.replaceAll("\\s", "");
        pMessage=pMessage.trim();
        ChatApp.logger.info("Sending a private message");
        ChatApp.logger.info(pMode + " and " + pIp + " and " + pMessage);
        msgProcsr.messageProcessorSendPm(pMode, pIp, pMessage);
    }

    /**
     * Propagates the Send FILE user input to messageProcessor
     * @param pIp
     * @param pFileName
     */
    public void sendFile( String pIp, String pFileName)
    {
        pIp=pIp.trim();
        pFileName=pFileName.trim();
        pIp = pIp.replaceAll("\\s", "");
        ChatApp.logger.info("Sending a File");
        ChatApp.logger.info("File" + " and " + pIp + " and " + pFileName);
        msgProcsr.messageProcessorSendFile( pIp, pFileName);
    }

    /**
     * Synchronized method which allows for messages to be displayed from multiple threads
     * @param mode
     * @param ip
     * @param message
     */
    public synchronized void writeRecievedMessage(String mode, String ip, String message)
    {
       String recievedMessage;
       switch (mode.toLowerCase())
       {
           case "all":
               recievedMessage = "//MESSAGE TO ALL FROM " + ip + ": " + message;
               stream.println(recievedMessage);
               break;
           case "pm":
               recievedMessage = "//PM FROM " + ip + ": " + message;
               stream.println(recievedMessage);
               break;
        }
    }

    /**
     * Method which writes the Received file to the console 
     * @param ip
     * @param file
     */
    public void writeRecievedFile(String ip, String file)
    {
        stream.println("RECIEVED FILE:"+file+" FROM "+ip);
    }

   
}
