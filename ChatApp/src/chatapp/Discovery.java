/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package chatapp;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

//using subnet utils provided by apache



/**
 *
 * @author aussi
 * @see http://stackoverflow.com/questions/8462498/how-to-determine-internet-network-interface-in-java
 */
public class Discovery extends Thread
{
    private ArrayList<IP4Address> connectedHosts;

    
    private HashMap<InetAddress,String> groupChatHosts;
    private Host localHost;
    private final String BROADCAST_CODE_MESSAGE="[ONLINE?IFFY]";
    private final int COM_PORT=4003;

    /**
     * Method which returns the local host
     * @return
     */
    public Host getLocalHost()
    {
        return localHost;
    }
    
    private final int IP_RANGE=10;
    private final int FILE_TRANSFER_PORT=4009;
    private final String MULTICAST_ADD="239.255.142.99";
    private final long BCAST_INTERVAL=10000;
    private volatile boolean running=true;

    /**
     * Method which stores the discovered clients
     * @param username
     */
    public Discovery(String username)
    {
        connectedHosts= new ArrayList<>();
        groupChatHosts= new HashMap<>();
        try
        {
            
          //set correct network interface for correct local ip            
          localHost= new Host(getCorrectLocalIP(), username);
           
        } catch (Exception e)
        {
            System.err.println("Error Getting LocalHost");
            System.err.println(e.getMessage());
        }
        
    }

    /**
     * Terminate Discovery
     */
    public void terminate()
    {
        running=false;
    }
    
    /**
     * Start running thread
     */
    @Override
    public void run()
    {
        try
        {
            
            
            while(running )
            {
                
                discoverHost();
                Thread.sleep(BCAST_INTERVAL);
                
            }
            ChatApp.logger.info("Discovery exiting");
            
        }
        catch(InterruptedException i)
        {
            System.err.println("Interupted thread discovery");
            Thread.currentThread().interrupt();
        }
        catch (Exception ex)
        {
            System.err.println("Failed to run discovery");
            Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Inet4Address getCorrectLocalIP()
    {
        Inet4Address localLan=null;
        try
        {
            //get all interfaces            
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets))
            {
                //filter invalid interfaces
                if (netint.isLoopback() || !netint.isUp())
                    continue;
                String name=netint.getDisplayName();
                if(name.contains("Virtual") )
                    continue;
                
                ChatApp.logger.info(name);
                //iterate through IPs
                for(InetAddress address :Collections.list(netint.getInetAddresses()))
                {
                    if(!(address instanceof Inet4Address))
                        continue;
                    //return a valid IP
                    ChatApp.logger.info(Utility.getStringFromInet(address));
                    return (Inet4Address)address;
                }
            }
            
        } catch (Exception ex)
        {
            Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return localLan;
    }
    private void discoverHost() throws Exception
    {
        //get bits of network address
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost.getHostIP());
        short subnetBits=networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
        
        //create IP4 class with address annd subnet
       
        IP4Address myAddress= new IP4Address(localHost.getIPString(), String.valueOf(subnetBits));
        //IP4Address temp= new IP4Address("10.1.46.136", String.valueOf(subnetBits));
        //check hosts for subnet in a loop every T seconds
       // checkHosts(myAddress);
        
        
        broadcastCode();
        ChatApp.logger.info(groupChatHosts.toString());
        
        //TO DO- Send ARE YOU ONLINE REQUEST to all connected hosts
        
    }

    /**
     *
     * @param hostname
     * @param address
     */
    public synchronized void addToChatGroup(String hostname,InetAddress address)
    {
        //TO DO updating the host lists for ARE U ONLINE confirmation
        if(!groupChatHosts.containsKey(address))
        {
            groupChatHosts.put(address,hostname);
            ChatApp.logger.info(hostname+" Added with ip "+address);
        }
    }
    
    /**
     *
     * @param hostIP
     */
    public synchronized void removeFromChatGroup(InetAddress hostIP)
    {
        if(groupChatHosts.containsKey(hostIP))
        {
            groupChatHosts.remove(hostIP);
        }
    }
    private void broadcastCode() throws UnknownHostException
    {
        ChatApp.logger.info("broadcasting code");
        //Multicast UDP packet to all hosts in subnet on port 4003
        Inet4Address broadcAddress=(Inet4Address) Inet4Address.getByName(MULTICAST_ADD);
        Thread t= new MessageSendUDP(broadcAddress, BROADCAST_CODE_MESSAGE, COM_PORT);
        t.start();
        
        
        
    }

    /**
     *
     * @return
     */
    public synchronized HashMap<InetAddress,String> getGroupChatHosts()
    {
        return groupChatHosts;
    }
    private void checkHosts(IP4Address localAddress) throws Exception
    {
        //iterate through IP RANGE to check if connected
        IP4Address nextAdd=localAddress;
        IP4Address previousAdd=localAddress;
        int timeout=1000;
        for(int i=0;i<IP_RANGE;i++)
        {
            if(nextAdd.hasNext())
            {
              //get next host to ping
              nextAdd=nextAdd.next(); 
                ChatApp.logger.info("pingig"+nextAdd.toString());
              if(pingHostApplication(nextAdd))
              {
                  connectedHosts.add(nextAdd);
                  ChatApp.logger.info(nextAdd.toString()+"Reachable");
              }
                
            }
            
            if(previousAdd.hasPrevious())
            {
                //get previous host
                previousAdd=previousAdd.previous();
                ChatApp.logger.info("pingig"+previousAdd.toString());
                if(pingHostApplication(previousAdd))
                {
                    ChatApp.logger.info(previousAdd.toString()+"Reachable");
                }
            }
                
        }
        
        ChatApp.logger.info(localAddress.toString());
        
        
        
        ChatApp.logger.info(localAddress.previous().toString());
      
        
        
    }
    private void checkHost(IP4Address address)
    {
         if(pingHostApplication(address))
              {
                  connectedHosts.add(address);
                  ChatApp.logger.info(address.toString()+" Reachable");
              }
    }
    
    private boolean pingHostApplication(IP4Address address) 
    {
        //tries to create a connection to FILE_TRANFER_PORT
        //host using this application has to keep this port open
        Socket testSock=null;
        try
        {
            InetAddress inet=address.getInetAddress();
             testSock= new Socket();
             testSock.connect(new InetSocketAddress(inet,FILE_TRANSFER_PORT), 1000);
        } catch (UnknownHostException ex)
        {
            Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException e)
        {
            ChatApp.logger.info(e.getMessage());
            return false;
        }
        finally
        {
            try
            {
                if(testSock!=null)
                testSock.close();
            } catch (IOException ex)
            {
                Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
