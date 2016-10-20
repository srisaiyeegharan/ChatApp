/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.net.InetAddress;

/**
 * Class to Manage File Handling in App
 * @author aussi
 */
public class FileProcessor 
{
    private MessageProcessor msgProcessor;
    private FileServer server;

    /**
     *Create new instance of File Processor
     * @param msgProcessor the parent message processor
     */
    public FileProcessor(MessageProcessor msgProcessor)
    {
        server= new FileServer(this);
        server.start();
        this.msgProcessor=msgProcessor;
    }

    /**
     * Terminate File Server
     * @throws InterruptedException
     */
    public void terminate() throws InterruptedException 
    {
        server.terminate();
        server.join();
    }

    /**
     * Send a File given its name to an IP Address
     * @param ip
     * @param filename
     */
    public void sendFile(InetAddress ip,String filename)
    {
        FileSender sender= new FileSender(ip, filename);
        ChatApp.logger.info("Reached sendFile" + ip + filename);
        sender.start();
        
    }
    
    /**
     * Propagate receive file to message processor
     * @param ip received IP
     * @param filename file name
     */
    public void recievedFile(InetAddress ip,String filename)
    {
        String ipAdd = Utility.getStringFromInet(ip);
        msgProcessor.messageProcessorRecievFile(ipAdd, filename);
    }
    
}
