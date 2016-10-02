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
import sun.security.pkcs11.wrapper.Functions;
//using subnet utils provided by apache
import  org.apache.commons.net.util.SubnetUtils;


/**
 *
 * @author aussi
 * @see http://stackoverflow.com/questions/8462498/how-to-determine-internet-network-interface-in-java
 */
public class Discovery extends Thread
{
    private ArrayList<IP4Address> connectedHosts;
    private HashMap<String,IP4Address> groupChatHosts;
    private Host localHost;

    public Host getLocalHost()
    {
        return localHost;
    }
    private final int IP_RANGE=10;
    private final int FILE_TRANSFER_PORT=4009;
    public Discovery()
    {
        connectedHosts= new ArrayList<>();
        try
        {
            
          //set correct network interface for correct local ip            
          localHost= new Host(getCorrectLocalIP(), "me");
        } catch (Exception e)
        {
            System.err.println("Error Getting LocalHost");
            System.err.println(e.getMessage());
        }
        
    }

    @Override
    public void run()
    {
        try
        {
            discoverHost(); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception ex)
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
                if(name.contains("Virtual"))
                    continue;
                
                //iterate through IPs
                for(InetAddress address :Collections.list(netint.getInetAddresses()))
                {
                    if(!(address instanceof Inet4Address))
                        continue;
                    //return a valid IP
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
        IP4Address myAddress= new IP4Address(localHost.getHostIP().getHostName(), String.valueOf(subnetBits));
        
        //check hosts for subnet in a loop every T seconds
        checkHosts(myAddress);
        //checkHost(myAddress);
    }
    public synchronized void addToChatGroup(String hostname,IP4Address address)
    {
        //TO DO updating the host lists for ARE U ONLINE confirmation
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
                System.out.println("pingig"+nextAdd.toString());
              if(pingHostApplication(nextAdd))
              {
                  connectedHosts.add(nextAdd);
                  System.out.println(nextAdd.toString()+"Reachable");
              }
                
            }
            
            if(previousAdd.hasPrevious())
            {
                //get previous host
                previousAdd=previousAdd.previous();
                System.out.println("pingig"+previousAdd.toString());
                if(pingHostApplication(previousAdd))
                {
                    System.out.println(previousAdd.toString()+"Reachable");
                }
            }
                
        }
        
        System.out.println(localAddress);
        
        
        
        System.out.println(localAddress.previous());
      
        
//        for (int i=1;i<255;i++)
//        {
//            String host=subnet + "." + i;
//            try
//            {
//                if (InetAddress.getByName(host).isReachable(timeout))
//            {
//                System.out.println(host + " is reachable");
//            }
//            } catch (Exception e)
//            {
//                System.err.println("Exception Finding host");
//                System.err.println(e.getMessage());
//            }
//            
//        }
    }
    private void checkHost(IP4Address address)
    {
         if(pingHostApplication(address))
              {
                  connectedHosts.add(address);
                  System.out.println(address.toString()+" Reachable");
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
            System.out.println(e.getMessage());
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
