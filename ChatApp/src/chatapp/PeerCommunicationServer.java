/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread which is used for communication between hosts
 * @author Sri,Ibrahim
 * @see http://stackoverflow.com/questions/4364434/let-two-udp-servers-listen-on-the-same-port
 */
public class PeerCommunicationServer extends Thread
{
    private int port;
    private MulticastSocket multsocket;
    private Discovery discoveryThread;
    private final String MULTICAST_ADD="239.255.142.99";
    private String grpCode;
     private volatile boolean running=true;
    
    /**
     * Create new Instance of PeerCommunicationServer
     * @param discovery discovery thread
     * @param grpCode the group code used by hosts
     */
    public PeerCommunicationServer(Discovery discovery,String grpCode)
    {
       port=4003;
       discoveryThread=discovery;
       this.grpCode =grpCode;
    }

    /**
     * Terminate Thread safely
     */
    public synchronized void terminate()
    {
        running=false;
        try {
            multsocket.leaveGroup(InetAddress.getByName(MULTICAST_ADD));
            multsocket.close();
        } catch (Exception ex) {
            Logger.getLogger(PeerCommunicationServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Start running the Thread <br/>
     * Creates multi socket and joins group<br/>
     * Start listening on socket
     */
    @Override
    public void run()
    {
        try
        {
            multsocket= new MulticastSocket(port);
            //join mulicast group
            multsocket.joinGroup(InetAddress.getByName(MULTICAST_ADD));
            multsocket.setLoopbackMode(true);
            startListening(multsocket);
        } catch (Exception ex)
        {
            Logger.getLogger(PeerCommunicationServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if(multsocket!=null)
                multsocket.close();
        }
        ChatApp.logger.info("Peer Comm exiting");
    }
    
    /**
     * Start Listening on socket
     * @param socket
     * @throws IOException
     */
    public void startListening(MulticastSocket socket) throws IOException
    {
        DatagramPacket recievePacket;
        while(running)
        {
            
            byte[] buff= new byte[1024];
            recievePacket=new DatagramPacket(buff,buff.length);
            socket.receive(recievePacket);
            //recieving communication data
            ChatApp.logger.info("recieving packet");
            String command = new String(recievePacket.getData());
            //santize command
            command=command.trim();
            ChatApp.logger.info("Data rec from IP:"+recievePacket.getAddress()+"data"+command);

            //validate and extract command from packet data
            if(!command.matches("\\[.+\\]"))
                continue;
            
            //remove sqr brackets
            command=command.substring(1, command.length()-1);
            //check online request
            if(command.contains("?"))
            {
                String value=null;
                String[] split=command.split("\\?");
                //extract grpCode to check
                if(split[0].equalsIgnoreCase("online"))
                    value=split[1];
                else
                    continue;
                
                //if code matches send a response saying [ONLINE=TRUE]
                if(value.equalsIgnoreCase(grpCode))
                {
                    //build message and send
                    StringBuilder build=new StringBuilder();
                    build.append("[ONLINE=");
                    build.append(discoveryThread.getLocalHost().getHostName());
                    build.append("]");
                    Thread msgSend= new MessageSendUDP(recievePacket.getAddress(), build.toString(), port);
                    msgSend.start();
                }
                    
            }
            //check if response to online request
            else if(command.contains("="))
            {
                String value=null;
                String[] split=command.split("\\=");
                //extract grpCode to check
                if(split[0].equalsIgnoreCase("online"))
                    value=split[1];
                else
                    continue;
                //if response from request update hostlist
                String hostname=value;
                InetAddress add=recievePacket.getAddress();
                discoveryThread.addToChatGroup(hostname, add);
               
                
                
            }
        
        }
    }
    
}
