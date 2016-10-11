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
        
        
    }

}
