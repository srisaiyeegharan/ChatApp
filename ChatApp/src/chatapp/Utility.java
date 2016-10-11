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
    
    public static String getStringFromInet(InetAddress pIp)
    {
        String ip = pIp.toString();
        String stringIp = ip.replace("/", "");
        return stringIp;
    }
    
    public static InetAddress getInetAddress (String ip)
    {
        InetAddress address=null;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return address;
    }
}
