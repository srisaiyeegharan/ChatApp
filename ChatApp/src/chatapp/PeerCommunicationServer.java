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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *This port is used for communication between hosts
 * @author aussi
 */
public class PeerCommunicationServer extends Thread
{
    private int port;
    private MulticastSocket multsocket;
    private Discovery discoveryThread;
    private final String MULTICAST_ADD="239.255.142.99";
    private String grpCode;

    
    public PeerCommunicationServer(Discovery discovery,String grpCode)
    {
       port=4003;
       discoveryThread=discovery;
       this.grpCode =grpCode;
    }

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
    }
    
    public void startListening(MulticastSocket socket) throws IOException
    {
        DatagramPacket recievePacket;
        while(true)
        {
            
            byte[] buff= new byte[1024];
            recievePacket=new DatagramPacket(buff,buff.length);
            socket.receive(recievePacket);
            //recieving communication data
            System.out.println("recieving packet");
            String command = new String(recievePacket.getData());
            //santize command
            command=command.trim();
            System.out.println("Data rec from IP:"+recievePacket.getAddress()+"data"+command);

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
                System.out.println("Host Added to list");
                
                
            }
        
        }
    }
    
}
