/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aussi
 */
public class ChatApp
{
    private static final String grpCode="IFFY";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
       srisTest();        
        //ibisTest();
    }

    static void srisTest()
    {
        Thread s = new CommandLine();
        s.start();
        
        try
        {
            s.join();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        Thread m = new MessageProcessor();
        m.start();
        
        try
        {
            m.join();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    static void ibisTest()


    {
        ServerSocket socket=null;
        
        MessageProcessServer ser = new MessageProcessServer();
        ser.start();
        
        String msg="{ALL=lolll}";
        String msg2="{PM=personal}";
        
        Thread ms1=null;
        Thread ms2=null;
        try {
            ms1 = new MessageSendUDP(InetAddress.getByName("136.186.14.85"), msg, 4002);
            ms2= new MessageSendUDP(InetAddress.getByName("136.186.14.84"), msg2, 4002);
            ms1.start();
            ms2.start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        Discovery dis = new Discovery();
//            dis.start();
//            
//            Thread peer= new PeerCommunicationServer(dis,grpCode);
//            peer.start();
        
        try
        {
//            dis.join();
//            peer.join();
            ms1.join();
            ms2.join();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
