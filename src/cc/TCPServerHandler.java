/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Work
 */
public class TCPServerHandler extends Thread {
    private Gestor gestor;
    private Socket socket;
    
    public TCPServerHandler (Socket s, Gestor g) {
        this.gestor = g;
        this.socket = s;
    }
    
    public void run () {
        
       
        String inputLine;
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                
            while (true){
                System.out.println ("Received an object!");
                Packet pa = (Packet) ois.readObject();
                inputLine = pa.getMsg();
                if (inputLine.equals("HELLO")) {
                    gestor.adicionaServidor(socket.getInetAddress(), 50000);
                    System.out.println(gestor.listaServidores());
                    oos.writeObject(new Packet("HELLO",null));
                }
                if (inputLine.equals("RANKING")){
                    pa = new Packet ("RANKING",gestor.getRanking());
                    oos.writeObject(pa);
                    
                }
                
                
                if (inputLine.equals("exit")) break;
            }
        }
        catch (IOException | ClassNotFoundException e) {System.out.println (e.getMessage());}
        
        
        
        
    }
}
