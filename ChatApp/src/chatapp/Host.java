/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.net.InetAddress;

/**
 * Class which models the host
 * @author Sri, Ibrahim
 */
public class Host
{
    private InetAddress hostIP;
    private String hostName;

    /**
     * Create a new instance of host 
     * @param hostIP
     * @param hostName
     */
    public Host(InetAddress hostIP, String hostName)
    {
        this.hostIP = hostIP;
        this.hostName = hostName;
    }

    /**
     * Returns the host IP
     * @return
     */
    public InetAddress getHostIP()
    {
        return hostIP;
    }

    /**
     * Set host IP
     * @param hostIP
     */
    public void setHostIP(InetAddress hostIP)
    {
        this.hostIP = hostIP;
    }

    /**
     * Returns the host name
     * @return
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * set host name
     * @param hostName
     */
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }
    
    /**
     * Returns the IP in String 
     * @return
     */
    public String getIPString()
   {
       String str= hostIP.toString();
       str=str.substring(1);
       
       return str;
   } 
    

}
