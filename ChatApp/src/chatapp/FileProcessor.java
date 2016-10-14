/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.net.InetAddress;

/**
 *
 * @author aussi
 */
public class FileProcessor 
{
    private MessageProcessor msgProcessor;
    private FileServer server;
    public FileProcessor(MessageProcessor msgProcessor)
    {
        server= new FileServer(this);
        server.start();
        this.msgProcessor=msgProcessor;
    }

    public void sendFile(InetAddress ip,String filename)
    {
        FileSender sender= new FileSender(ip, filename);
        System.out.println("Reached sendFile" + ip + filename);
        sender.start();
    }
    
    public void recievedFile(InetAddress ip,String filename)
    {
        String ipAdd = Utility.getStringFromInet(ip);
        msgProcessor.messageProcessorRecievFile(ipAdd, filename);
    }
    
}
