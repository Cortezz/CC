package cc;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** Classe utilizado para agrupar e tratar dos blocos de música.
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class SongBuilder extends Thread {
    
    private HashMap<Integer,PDU> blocos;
    private DatagramPacket packet;
    private int index;
    
    private Lock lock = new ReentrantLock();

    
    /** 
     * Construtor parametrizado
     * @param blocos HashMap que irá conter os blocos de música
     * @param packet DatagramPacket com os blocos enviados pelo servidor
     * @param index Índice do bloco
     */
    public SongBuilder (HashMap<Integer,PDU> blocos, DatagramPacket packet, int index){
        this.blocos = blocos;
        this.packet = packet;
        this.index = index;
    }
    
    public void run () {
        
        PDU p;
        int tipo,limite;
        byte[] buffer;
        
        buffer = packet.getData();
        p = new PDU (buffer);
        ArrayList<Campo> auxiliar = p.getCampos();
        tipo = (auxiliar.get(auxiliar.size()-1).getTipoCampo() & 0xff);
        System.out.println ("Tipo: "+tipo);
        if (tipo!=254) 
        {
            CampoByte cb = (CampoByte)auxiliar.get(0);
            limite = (cb.getValor() & 0xff);
            
        }
        lock.lock();
        try { blocos.put(index,p);}
        finally {lock.unlock();}
    }
}
