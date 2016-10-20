/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import static java.util.logging.Logger.getLogger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author aussi
 */
public class ChatApp
{
    private static final String grpCode="IFFY";
    private static FileHandler fileTxt;
    public static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    /** initiate chat program
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        //read username from command line
       
        //logger setup
        SimpleFormatter formatter= new SimpleFormatter();
        
        Logger globalLogger = Logger.getLogger("");
        Handler[] handlers = globalLogger.getHandlers();
        for(Handler handler : handlers) {
        globalLogger.removeHandler(handler);
}
       
       
       String username="Ibrahim";
        MessageProcessor msgproc= new MessageProcessor(username,grpCode);
        msgproc.start();

        try {
             fileTxt= new FileHandler("logs.txt");
             
             logger.addHandler(fileTxt);
             logger.warning("Log File Created");
            msgproc.join();      
           
            //ibisTest();
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex)
        {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    

}
