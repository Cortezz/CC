/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class InfoServidor {
    private InetAddress ip;
    private int port;
    
    
    
    
    
    
    
                /***************************
                 *      Construtores       *
                 ***************************/
    
    public InfoServidor (InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    
    public InfoServidor () {
        this.port = 50000;
        try {
            this.ip = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {System.out.println(e.getMessage());}
    }
    
    public InfoServidor (InfoServidor is) {
        this.ip = is.getIP();
        this.port = is.getPort();
    }
    
    
    
    
    //Métodos get e set
    public InetAddress getIP () { return this.ip;}
    public int getPort() { return this.port;}
    
    public void setIP (InetAddress ip ){this.ip = ip;}
    public void setPort (int p) {this.port = p;}
    
    @Override
    public boolean equals (Object o) {
        if (this==o) return true;
        if (this==null || this.getClass()!=o.getClass()) return false;
        InfoServidor s = (InfoServidor)o;
        return (this.ip.equals(s.getIP()) && this.port == s.getPort());
    }
    
    @Override
    public String toString () {
        StringBuilder s = new StringBuilder ();
        s.append("IP: ").append(this.ip);
        s.append("Port: ").append(this.port);
        return s.toString();
    }
                
    public InfoServidor clone () {
        return new InfoServidor(this);
    }
    
}
