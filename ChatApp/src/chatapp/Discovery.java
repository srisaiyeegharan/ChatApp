/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import sun.security.pkcs11.wrapper.Functions;


/**
 *
 * @author aussi
 * @see http://stackoverflow.com/questions/8462498/how-to-determine-internet-network-interface-in-java
 */
public class Discovery extends Thread
{
    private Host localHost;
    
    public Discovery()
    {
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
            
        
               
        

        
    }
    private void checkHosts(String subnet)
    {
        int timeout=1000;
        for (int i=1;i<255;i++)
        {
            String host=subnet + "." + i;
            try
            {
                if (InetAddress.getByName(host).isReachable(timeout))
            {
                System.out.println(host + " is reachable");
            }
            } catch (Exception e)
            {
                System.err.println("Exception Finding host");
                System.err.println(e.getMessage());
            }
            
        }
    }
    
}
