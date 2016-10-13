/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aussi
 */
public class FileServer extends Thread
{
    private final int FILE_SERVER_PORT=4009;
    private ServerSocket socket;
    private FileProcessor fProcess;
    private final String FILE_RECIEVE_lOCATION="/FilesRecieved";
    
    public FileServer(FileProcessor processor)
    {
        socket=null;
        fProcess=processor;
    }

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
    }
    
    public void startListening(ServerSocket socket) throws IOException
    {
        while (true)
        {            
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
                fos= new FileOutputStream(FILE_RECIEVE_lOCATION+fileToRecieve);

                //start reading bytes into file
                int bytesRead=0;
                byte[] buffer= new byte[1024];
                while((bytesRead=dis.read(buffer))!=-1)
                {
                 fos.write(buffer,0,bytesRead);
                }

                //send confirmation
                dos=new DataOutputStream(clienSocket.getOutputStream());
                dos.writeUTF("Transfer=True");

                //send result to chatapp 
            }
            
            
            //close clientsocket
            clienSocket.close();
        }
    }
    
    
}