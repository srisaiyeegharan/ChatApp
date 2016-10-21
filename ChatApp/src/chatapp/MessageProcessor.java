package chatapp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editopfgr.
 */

/**
 *
 * @author Srisaiyeegharan
 */


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Srisaiyeegharan
 */
public class MessageProcessor  extends Thread{
   
    private final int MSG_SEND_PORT = 4002;
    private CommandLine commandLine;
    private MessageProcessServer messageProcessServer;
    private FileProcessor fileProcessor;
    private PeerCommunicationServer peerComm;
    private Discovery discovery;
    private String username;
    private String grpCode;
    
    /**
     * Creates an instance of Message processor
     */
    MessageProcessor(String uname,String code)  {
      username=uname;
      grpCode=code;
     
    }
    
    /**
     * Start this thread
     */
    @Override
    public void run()
    {
        discovery= new Discovery(username);
        discovery.start();
        peerComm=new PeerCommunicationServer(discovery, grpCode);
        peerComm.start();
        commandLine=new CommandLine(this);
        commandLine.start();
        messageProcessServer= new MessageProcessServer(this);
        messageProcessServer.start();
        fileProcessor= new FileProcessor(this); 
        
        
        try {
            messageProcessServer.join();
            discovery.join();
            peerComm.join();
            commandLine.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Terminates this thread
     */
    public void terminateApp()
    {
        try {
            commandLine.terminate();
            messageProcessServer.terminate();
            fileProcessor.terminate();
            discovery.terminate();
            peerComm.terminate();     
            
            
            
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method returns the available chat members
     * @return
     */
    public String getGroupChatMembers()
    {
        //return all hosts with their ips to be displayed
        StringBuilder builder = new StringBuilder();
        builder.append("HostName IPAddress\n");
        
        //get all connected hosts
        HashMap<InetAddress,String> group= discovery.getGroupChatHosts();
        
        //iterate through all hosts building the string up
        for(HashMap.Entry<InetAddress,String> entry :group.entrySet())
        {
           builder.append(entry.getValue()+" "+Utility.getStringFromInet(entry.getKey())+"\n");
        }
        
        return builder.toString();
    }
    
    /**
     * This method is used to remove a host from the list 
     * @param hostIP
     */
    public void removeHost(InetAddress hostIP)
    {
        discovery.removeFromChatGroup(hostIP);
    }

    /**
     * Sends the message to all the chat members available on the list
     * @param pRecievedMode
     * @param pRecievedMessage
     */
    public void messageProcessorSendAll(String pRecievedMode, String pRecievedMessage)
    {
        //get all connected hosts
        HashMap<InetAddress,String> group= discovery.getGroupChatHosts();
        ArrayList<InetAddress> sendIps=new ArrayList<>();
        
        //add hosts to Ip list and send it
        for(HashMap.Entry<InetAddress,String> entry :group.entrySet())
        {
           sendIps.add(entry.getKey());
        }
        
        StringBuilder builder= new StringBuilder();
        builder.append("{"+pRecievedMode.toUpperCase()+"=");
        builder.append(pRecievedMessage+"}");
        String msgToSend=builder.toString();
        
        ChatApp.logger.info("Sending ");
        MessageSendUDP sendmsg= new MessageSendUDP(sendIps, msgToSend, MSG_SEND_PORT);
        
        sendmsg.start();
        
    }
    
    /**
     * Sends a private message to a given IP
     * @param pRecievedMode
     * @param pRecievedIp
     * @param pRecievedMessage
     */
    public void messageProcessorSendPm(String pRecievedMode, String pRecievedIp, String pRecievedMessage)
    {
       
        InetAddress ip;
        ip =getInetAddress(pRecievedIp);  
         //validate ip
        if(ip==null || !validateIP(ip))
        {
            System.out.println("Invalid IP specified");
            return;
        }
        StringBuilder builder= new StringBuilder();
        builder.append("{"+pRecievedMode.toUpperCase()+"=");
        builder.append(pRecievedMessage+"}");
        
        String msgToSend=builder.toString();
        
        MessageSendUDP sendMsg= new MessageSendUDP(ip, msgToSend, MSG_SEND_PORT);
        sendMsg.start();
       
        
    }
    
    /**
     * Sends a file to a given IP
     * @param pSendIP
     * @param pSendFile
     */
    public void messageProcessorSendFile(String pSendIP, String pSendFile)
    {
        ChatApp.logger.info("Reached messageProcessorSendFile "+pSendIP+pSendFile);
        //validate ip
        InetAddress ip;
        ip = getInetAddress(pSendIP);        
        if(ip==null || !validateIP(ip))
        {
            System.out.println("Invalid IP specified");
            return;
        }
        fileProcessor.sendFile(ip, pSendFile);
    }
    
    /**
     * Passes the received file to the commandLine class
     * @param IP
     * @param filename
     */
    public void messageProcessorRecievFile(String IP, String filename)
    {
        commandLine.writeRecievedFile(IP, filename);
    }
    
    /**
     * Passes the received message to the commandLine class
     * @param pmode
     * @param pip
     * @param pmessage
     */
    public void recieveMessage(String pmode, InetAddress pip, String pmessage)
    {
        String ip;
        ip = Utility.getStringFromInet(pip);
        commandLine.writeRecievedMessage(pmode,ip,pmessage);
    }
    
    private boolean validateIP(InetAddress ip)
    {
        //check if ip exist
        HashMap<InetAddress,String> group= discovery.getGroupChatHosts();
        if(group.containsKey(ip))
            return true;
        else
            return false;
    }
    
    private InetAddress getInetAddress(String nameORip)
    {
        InetAddress ip=null;
        ip=Utility.getInetAddress(nameORip);
        if(ip==null)
        {
            //check if username exist
            HashMap<InetAddress,String> group= discovery.getGroupChatHosts();
            
            int count=0;
            for(HashMap.Entry<InetAddress,String> entry :group.entrySet())
            {
                if(entry.getValue().equals(nameORip))
                {
                    count++;
                    ip=entry.getKey();
                }
            }
            if(count>1)
            {
               ip=null; 
                System.out.println("Duplicate Usernames exist, Please type IP");
            }

                
        }
        return ip;
    }
}
