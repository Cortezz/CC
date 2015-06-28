/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Work
 */
public class TCPServer extends Thread{
    private int port;
    private Gestor gestor;
    
    public TCPServer (int port, Gestor g) {
        this.port = port;
        this.gestor = g;
    }
    
    public void run () {
        try {
            ServerSocket ss = new ServerSocket(port);
            Socket cliente;
            System.out.println ("TCP Server is ready...");
            while (true) {
                cliente = ss.accept();
                System.out.println("Aceitei um cliente!");
                Thread tcpHandler = new Thread (new TCPServerHandler (cliente,gestor));
                tcpHandler.start();
            }
        }
        catch(IOException e) { System.out.println(e.getMessage());}
            
    }
    
}
