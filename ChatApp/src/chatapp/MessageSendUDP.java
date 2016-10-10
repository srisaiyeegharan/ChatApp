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
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aussi
 */
public class MessageSendUDP extends Thread
{
    private InetAddress ip;
    private String msg;
    private int port;
    public MessageSendUDP(InetAddress IPAddress,String message,int port)
    {
       ip=IPAddress;
       msg=message;
       this.port=port;
    }

    @Override
    public void run()
    {
        DatagramSocket socket=null;
        try
        {
            socket= new DatagramSocket();
            sendPacket(socket);
        } catch (SocketException ex)
        {
            Logger.getLogger(MessageSendUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(MessageSendUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
          if(socket!=null)
              socket.close();
        }
    }
    
    public void sendPacket(DatagramSocket socket) throws SocketException, IOException 
    {
        //send message using datagram
        DatagramPacket packet;
        byte[] buff= new byte[1024];
        
        //get bytes from string and send packet
        buff=msg.getBytes();
        String st= new String(buff);
        System.out.println("Buffer"+st);
        packet= new DatagramPacket(buff,buff.length,ip,port);
        
        socket.send(packet);
        
        System.out.println("Packet Sent:"+packet.getData());
        Logger.getLogger(MessageSendUDP.class.getName()).log(Level.FINE, msg+"sent in packet");
    }
    
}
