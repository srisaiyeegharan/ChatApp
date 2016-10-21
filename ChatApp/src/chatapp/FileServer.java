/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Thread to run File Server
 * @author Ibrahim, Sri
 * @see http://stackoverflow.com/questions/6298479/listen-to-port-via-a-java-socket
 */
public class FileServer extends Thread
{
    private final int FILE_SERVER_PORT=4009;
    private ServerSocket socket;
    private FileProcessor fProcess;
    private final String FILE_RECIEVE_lOCATION="FilesRecieved/";
    private volatile boolean running=true;
    /**
     * Creates an instance of File Server
     * @param processor FileProcessor caller
     */
    public FileServer(FileProcessor processor)
    {
        socket=null;
        fProcess=processor;
    }

    /**
     * terminate the File Server Thread
     */
    public synchronized void terminate()
    {
        running=false;
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Start running the thread
     */
    @Override
    public void run()
    {
        try
        {
            socket= new ServerSocket(FILE_SERVER_PORT);
            startListening(socket);
        } catch (IOException ex)
        {
            Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if(socket!=null)try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ChatApp.logger.info("File Server exiting");
    }
    
    /**
     * Start lIstening for file connections on socket
     * @param socket
     */
    public void startListening(ServerSocket socket) 
    {
        while (running)
        {
            try {
                //accept a new client connection
               Socket clienSocket=socket.accept();

               FileOutputStream fos= null;
               DataOutputStream dos=null;
               DataInputStream dis=null;

               //read filename sent from peer
               dis=new DataInputStream(clienSocket.getInputStream());
               String fileToRecieve=dis.readUTF();
               if(!fileToRecieve.equals(""))
               {
                   File file = new File(FILE_RECIEVE_lOCATION+fileToRecieve);
                   file.getParentFile().mkdirs();
                   fos= new FileOutputStream(file);

                   //start reading bytes into file
                   int bytesRead=0;
                   byte[] buffer= new byte[1024];
                   while((bytesRead=dis.read(buffer))!=-1)
                   {

                    fos.write(buffer,0,bytesRead);
                   }

                   //send confirmation

                   //to chatapp 
                   fProcess.recievedFile(clienSocket.getInetAddress(), fileToRecieve);
               }

               fos.close();
               //close clientsocket
               clienSocket.close();
            } catch (Exception e) {
                Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, e);
            }
           
        }
    }
    
    
}
