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

import static chatapp.Utility.getStringFromInet;
import java.net.InetAddress;
public class MessageProcessor extends Thread  {
   
    private final int MSG_SEND_PORT = 4002;
    private CommandLine commandLine;
    private Discovery discovery;
    
    MessageProcessor(CommandLine commandl,Discovery dis) {
        
     commandLine=commandl;
     discovery=dis;
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
        String ip;
        ip = getStringFromInet(pip);
        l.writeRecievedMessage(pmode,ip,pmessage);
    }
    
}
