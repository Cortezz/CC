package cc;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/** Classe relativa ao servidor.
 *  Serão recebidos pedidos de utilizadores e ,posteriormente, serão invocadas threads para tratar desses pedidos.
 * @author José Cortez, André Costa e Miguel zenha
 */
public class Server{
    
     
    private static Gestor gestor;
    private static final int PACKETSIZE = 300;
    private static DatagramSocket socket;
    private static int port;
    
      
    public static void main (String[] args) throws IOException
    {
        String linha;
        try {gestor.carregaGestor();System.out.println("A carregar gestor...");}
        catch (ClassNotFoundException | NullPointerException e) { System.out.println ("A criar novo gestor!");
        gestor = new Gestor();}
        port = 50000;   
        
        ArrayList<String> linhasIP;
        String[] partes;
        System.out.println(InetAddress.getLocalHost());
            
        //Ler configuração do IP
        linhasIP = gUtils.leFicheiroIP((System.getProperty("user.dir")+"\\resources")+"\\ipconfig.txt");
        if (linhasIP.size()!=1) {
            for (String s: linhasIP)
            {
                    
                System.out.println(linhasIP.size());    
                partes = s.split(" ");
                InetAddress ip = InetAddress.getByName(partes[0]);
                int p = Integer.valueOf(partes[1]);
                try {  
                    Socket so = new Socket(partes[0],p);
                    so.setSoTimeout(300);
                    ObjectOutputStream oos = new ObjectOutputStream(so.getOutputStream());
                    Packet pa = new Packet("HELLO",null);
                    oos.writeObject(pa);
                    gestor.adicionaServidor(InetAddress.getByName(partes[0]), Integer.valueOf(partes[1]));
                }
                catch (ConnectException e) { System.out.println("Não consegui conectar-me!");} 
            }
        }
        appendAddress(linhasIP);
       
            
            
        try {
            
            /** TCP **/
            Thread tcpServ = new Thread (new TCPServer (port,gestor));
            tcpServ.start();
            
            /** UDP **/
            socket = new DatagramSocket(port);
            System.out.println ("The server is ready...");
            while (true) {
            
            DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
            socket.receive( packet ) ;
            
            Thread thread = new Thread (new UDPClientHandler (socket,gestor,packet));
            thread.start();
            }
            
        } catch (IOException  e ) {System.out.println(e);}
        
        try {
            gestor.guardarGestor();
        }
        catch (IOException e) {System.out.println (e.getMessage());}
  }
    
    
   static void appendAddress (ArrayList<String> linhasIP) {
    String path, address;
    FileWriter fw;
    BufferedWriter bw;
    File f;
    int flag;
    
    flag = 0;
    path = System.getProperty("user.dir")+"\\resources"+"\\ipconfig.txt";
    try {
        address = ("\r\n"+InetAddress.getLocalHost().getHostAddress()+" "+port);

        for (String s: linhasIP) 
            if (s.equals(address)){
                flag = 1;
                break;
            }
        
        if(flag==0){
            f = new File(path);
            fw = new FileWriter (f,true);
            bw = new BufferedWriter  (fw);
            bw.write(address);
            bw.close();    
        }
    }
    catch (IOException e ) {System.out.println (e.getMessage());}
  }
    
}