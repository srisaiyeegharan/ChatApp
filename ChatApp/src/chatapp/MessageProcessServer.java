
package chatapp;
import chatapp.MessageSendUDP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ibi
 */
public class MessageProcessServer extends Thread{
    //Message format accepted- {ALL=message...}
    private int port;
    private MessageProcessor messageProcessor;
    private DatagramSocket socket;
    private final String BYE_MESSAGE="*BYE*";
    private volatile boolean running=true;
    public MessageProcessServer(MessageProcessor pmessageProcessor)
    {
        port=4002;
        messageProcessor = pmessageProcessor;
    }
    public synchronized void terminate()
    {
        running=false;
        socket.close();
    }
    @Override
    public void run() {
        try {
             socket = new DatagramSocket(port);
             startListening(socket);
        } catch (Exception ex) {
            Logger.getLogger(MessageProcessServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            socket.close();
        }
        System.out.println("Message Process Server exiting");
    }
    
    public void startListening(DatagramSocket socket) throws IOException
    {
        DatagramPacket recievePacket;
        while(running)
        {
            byte[] buff= new byte[1024];
            recievePacket=new DatagramPacket(buff,buff.length);
            socket.receive(recievePacket);
            
            //recieving message data
            System.out.println("recieving Message packet");
            String message = new String(recievePacket.getData());
            //santize message
            message=message.trim();
            System.out.println("Message Data rec from IP:"+recievePacket.getAddress()+"data"+message);
            
            //validate and extract message from packet data
            if(!message.matches("\\{.+\\}"))
                continue;
            
            //remove curly brackets
            message=message.substring(1, message.length()-1);
            //check message type
            if(message.contains("="))
            {
                String msgValue=null;
                String[] split=message.split("\\=");
                //extract type to check
                if(split[0].equalsIgnoreCase("all"))
                {
                    //message to all
                    msgValue=split[1];
                    //call UDPchat with message from all
                    System.out.println("Message from all"+msgValue);
                    
                    //check if its a BYE message
                    if(msgValue.equals(BYE_MESSAGE))
                    {
                        System.out.println("Host Removed "+recievePacket.getAddress());
                        messageProcessor.removeHost(recievePacket.getAddress());
                    }
                    else
                    //if not sending normal message to the MessageProcessor
                    messageProcessor.recieveMessage(split[0], recievePacket.getAddress(), msgValue);
                    
                    
                }
                    
                else if(split[0].equalsIgnoreCase("pm"))
                {
                    //message from single person
                    msgValue=split[1];
                    //call UDPchat with message from single person
                    System.out.println("Message from single"+msgValue);
                       //sending message to the MessageProcessor
                    messageProcessor.recieveMessage(split[0], recievePacket.getAddress(), msgValue);
                }
                else
                    continue;
                
                
                    
            }
            
        }
    }
}
