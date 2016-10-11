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
public class MessageProcessor extends Thread  {
   
    private final int PORT = 4002;
    CommandLine l = new CommandLine();
    
    MessageProcessor() {
     
    }
    
    public void messageProcessorSendAll(String pRecievedMode, String pRecievedMessage)
    {
        
    }
    
    public void messageProcessorSendPm(String pRecievedMode, String pRecievedIp, String pRecievedMessage)
    {
        
    }
    
    public void messageProcessorSendFile(String pRecievedMode, String pRecievedIp, String pRecievedFileName)
    {
        
    }
    
    public void recieveMessage(String pmode, InetAddress pip, String pmessage)
    {
        
        
        l.writeRecievedMessage(pmode,ip,pmessage);
    }
}
