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
public class MessageProcessor extends Thread  {
   
    private final int MSG_SEND_PORT = 4002;
    private CommandLine commandLine;
    private Discovery discovery;
    
    
    MessageProcessor(Discovery dis) {
        
     commandLine=new CommandLine(this);
     
     discovery=dis;
    }
    
    public void messageProcessorSendAll(String pRecievedMode, String pRecievedMessage)
    {
        //get all connected hosts
        HashMap<String,InetAddress> group= discovery.getGroupChatHosts();
        ArrayList<InetAddress> sendIps=new ArrayList<>();
        
        //add hosts to Ip list and send it
        for(HashMap.Entry<String,InetAddress> entry :group.entrySet())
        {
           sendIps.add(entry.getValue());
        }
        
        StringBuilder builder= new StringBuilder();
        builder.append("{"+pRecievedMessage.toUpperCase()+"=");
        builder.append(pRecievedMessage+"}");
        String msgToSend=builder.toString();
        
        System.out.println("Sending ");
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
    
    public void messageProcessorSendFile(String pRecievedMode, String pRecievedIp, String pRecievedFileName)
    {
        
    }
    
    public void recieveMessage(String pmode, InetAddress pip, String pmessage)
    {
        String ip;
        ip = Utility.getStringFromInet(pip);
        commandLine.writeRecievedMessage(pmode,ip,pmessage);
    }
    
}
