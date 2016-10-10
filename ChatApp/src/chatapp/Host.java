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
public class Host
{
    private InetAddress hostIP;
    private String hostName;

    public Host(InetAddress hostIP, String hostName)
    {
        this.hostIP = hostIP;
        this.hostName = hostName;
    }

    public InetAddress getHostIP()
    {
        return hostIP;
    }

    public void setHostIP(InetAddress hostIP)
    {
        this.hostIP = hostIP;
    }

    public String getHostName()
    {
        return hostName;
    }

    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }
    
   public String getIPString()
   {
       String str= hostIP.toString();
       str=str.substring(1);
       
       return str;
   }
    
    
    
}
