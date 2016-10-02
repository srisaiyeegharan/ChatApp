/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aussi
 */
public class ChatApp
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ibisTest();
    }
    
    static void ibisTest() 
    {
        ServerSocket socket=null;
        try
        {
            socket = new ServerSocket(4009);
        } catch (IOException ex)
        {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
           if(socket!=null)
           {
               try
               {
                   socket.close();
               } catch (IOException ex)
               {
                   Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
        }
        Thread t = new Discovery();
        t.start();
        
        try
        {
            t.join();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}