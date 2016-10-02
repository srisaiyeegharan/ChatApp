/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.net.util.SubnetUtils;
/**
 *
 * @author aussi
 * @see http://stackoverflow.com/questions/13792784/incrementing-through-ip-addresses-in-string-format
 */
public class IP4Address 
{
     private final int value;
     private final String address;
     private final String subNet;

    private IP4Address(int value,String subNet) {
        this.value = value;
        this.subNet=subNet;
        this.address=getAddressString(value);
    }

    public IP4Address(String stringValue,String subNetMask) {
        
        //extract 4 parts of IP string
        String[] parts = stringValue.split("\\.");
        if( parts.length != 4 ) {
            throw new IllegalArgumentException();
        }
        subNet=subNetMask;
        address=stringValue;
        //use bit operations to convert
        value = 
                (Integer.parseInt(parts[0], 10) << (8*3)) & 0xFF000000 | 
                (Integer.parseInt(parts[1], 10) << (8*2)) & 0x00FF0000 |
                (Integer.parseInt(parts[2], 10) << (8*1)) & 0x0000FF00 |
                (Integer.parseInt(parts[3], 10) << (8*0)) & 0x000000FF;
    }

    public int getOctet(int i) {
        //retrieve in octets
        if( i<0 || i>=4 ) throw new IndexOutOfBoundsException();

        return (value >> (i*8)) & 0x000000FF;
    }

    public String toString()
    {
        return address;
    }

    private String getAddressString(int value)
    {
        StringBuilder sb = new StringBuilder();
        //get string representation of IP
        for(int i=3; i>=0; --i) {
            sb.append(getOctet(i));
            if( i!= 0) sb.append(".");
        }

        return sb.toString();
    }
    @Override
    public boolean equals(Object obj) 
    {
        if( obj instanceof IP4Address ) {
            return value==((IP4Address)obj).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }

    public int getValue() {
        return value;
    }

    public IP4Address next() {
        return new IP4Address(value+1,subNet);
    }
    
    public IP4Address previous() {
        return new IP4Address(value-1,subNet);
    }
    
    public boolean hasNext()
    {
        SubnetUtils info = new SubnetUtils(getCIDR(address, subNet));
        IP4Address high= new IP4Address(info.getInfo().getHighAddress(),subNet);
        return this.value<high.value;
          
    }
    
     public boolean hasPrevious()
    {
        SubnetUtils info = new SubnetUtils(getCIDR(address, subNet));
        IP4Address high= new IP4Address(info.getInfo().getLowAddress(),subNet);
        return this.value>high.value;
          
    }

    private String getCIDR(String IP,String subNet)
    {
        return IP+"/"+subNet;
    }
    
    public InetAddress getInetAddress() throws UnknownHostException
    {
        return InetAddress.getByName(address);
    }
}

