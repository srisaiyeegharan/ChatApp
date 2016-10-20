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
public class MessageProcessor  extends Thread{
   
    private final int MSG_SEND_PORT = 4002;
    private CommandLine commandLine;
    private MessageProcessServer messageProcessServer;
    private FileProcessor fileProcessor;
    private PeerCommunicationServer peerComm;
    private Discovery discovery;
    private String username;
    private String grpCode;
    
    
    MessageProcessor(String uname,String code)  {
      username=uname;
      grpCode=code;
     
    }
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
    
    public void removeHost(InetAddress hostIP)
    {
        discovery.removeFromChatGroup(hostIP);
    }
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
    
    public void messageProcessorSendPm(String pRecievedMode, String pRecievedIp, String pRecievedMessage)
    {
        InetAddress ip;
        ip = Utility.getInetAddress(pRecievedIp);        
        StringBuilder builder= new StringBuilder();
        builder.append("{"+pRecievedMode.toUpperCase()+"=");
        builder.append(pRecievedMessage+"}");
        
        String msgToSend=builder.toString();
        
        MessageSendUDP sendMsg= new MessageSendUDP(ip, msgToSend, MSG_SEND_PORT);
        sendMsg.start();
       
        
    }
    
    public void messageProcessorSendFile(String pSendIP, String pSendFile)
    {
        ChatApp.logger.info("Reached messageProcessorSendFile "+pSendIP+pSendFile);
        
        fileProcessor.sendFile(Utility.getInetAddress(pSendIP), pSendFile);
    }
    
    public void messageProcessorRecievFile(String IP, String filename)
    {
        commandLine.writeRecievedFile(IP, filename);
    }
    
    public void recieveMessage(String pmode, InetAddress pip, String pmessage)
    {
        String ip;
        ip = Utility.getStringFromInet(pip);
        commandLine.writeRecievedMessage(pmode,ip,pmessage);
    }
    
}
