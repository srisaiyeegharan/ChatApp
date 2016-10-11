/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

/**
 *
 * @author Srisaiyeegharan
 */
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Utility {
    
    //Returns String of IP address of a given InetAddress IP 
    public static String getStringFromInet(InetAddress pIp)
    {
        //InetAddress to String
        String ip = pIp.toString();
        //Removing / from the String IP address
        String stringIp = ip.replace("/", "");
        //Return ip
        return stringIp;
    }
    
    //Return InetAddress of a givent String IP address
    public static InetAddress getInetAddress (String ip)
    {
        
        InetAddress address = null;
        try {
            //Get by name of the given IP address
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Return the IP Address
        return address;
    }
}
