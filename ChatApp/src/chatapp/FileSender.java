/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread to Send Files sent by user
 * @author aussi
 */
public class FileSender extends Thread
{
    private final int FILE_SERVER_PORT=4004;
    private Socket peerfileServer;
    private InetAddress IP;
    private String fName;
    
    /**
     *Create instance of file sender Thread
     * <br/>
     * 
     * @param sendIP IP to send to
     * @param filename file name
     */
    public FileSender(InetAddress sendIP,String filename)
    {
        IP=sendIP;
        fName=filename;
    }

    /**
     * Start running Thread
     */
    @Override
    public void run()
    {
        try
        {
            peerfileServer=new Socket(IP, FILE_SERVER_PORT);
            
            ChatApp.logger.info("Running Send");
            sendFile(peerfileServer);
        } catch (IOException ex)
        {
            ChatApp.logger.info("Error sending file!");
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if(peerfileServer!=null)try {
                peerfileServer.close();
            } catch (IOException ex) {
                Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Send file on specified socket
     * @param fileServer
     * @throws IOException
     */
    public void sendFile(Socket fileServer) throws IOException
    {
        DataInputStream dis= null;
        DataOutputStream dos= null;
        FileInputStream fis=null;
        
                
        //open file
        File sendFile= new File(fName);
        if(!sendFile.exists())
        {
            System.out.println("File not found");
           throw new FileNotFoundException(fName+"not found");
        }
        
        //send filename to be sent
        dos= new DataOutputStream(fileServer.getOutputStream());
        ChatApp.logger.info("Send File"+IP+"name"+fName);
        dos.writeUTF(fName);
        
        
        
        //start writing the file accross the stream
        fis= new FileInputStream(sendFile);
        int bytesRead=0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = fis.read(buffer)) != -1)
            {
                
                dos.write(buffer, 0, bytesRead);
                Arrays.fill( buffer, (byte) 0 );
            }
        dos.flush();
        
        
        System.out.println("Transfer Complete");
        
        //send to chat app file sent
        
        //close sockets
       
        
    }
    
    
    
}
