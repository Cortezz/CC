package cc;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/** Class que fornece métodos estáticos auxiliares relativos ao trabalho de elementos multimédia e afins
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class gUtils {
    
    private static final int BLOCO = 50000;
    private static final int PACKETSIZE = 300;
    private static final int PDU_SIZE = 255;

    
    
    /**
     * Converte um short num inteiro.
     * @param b1 Primeiro byte do short
     * @param b2 Segundo byte do short
     * @return Inteiro final
     */
    public static int convertTwoBytesToInt2(byte b1, byte b2) {
    return (int) (( (b2 & 0xFF) << 8) | (b1 & 0xFF));
    }
    
    /**
     * Converte um PDU, no qual esteja armazenada uma imagem, num array de bytes
     * @param p PDU a ser transformado
     * @return Array de bytes finais
     */
    public static byte[] mountImage (PDU p)
    {
        byte[] imagem;
        byte[] data;
        int tamanho = (p.getTamanhoCampos() & 0xffff)-((p.getNumeroCampos()&0xff)*2);
        System.out.println(tamanho);
        imagem = new byte[tamanho];
        int i=0,j;
        ArrayList<Campo> c = p.getCampos();
        
        while (i<tamanho)
            for (Campo ca: c)
            {
                CampoMultimedia cm = (CampoMultimedia)ca;
                data = cm.getMultimedia();
                for (j=0;j<(cm.getTamanho() & 0xff);j++,i++)
                    imagem[i]=data[j];
                
            }
        return imagem;
    }
    
    /**
     * Junta os vários blocos de música, dando origem ao trecho inicial
     * @param blocos HashMap com os vários blocos de música.
     * @return Array de bytes com a sequência inicial do trecho musical.
     */
    public static byte[] mountSong (HashMap<Integer,PDU> blocos) {
        int i=1, tamanho,j;
        byte[] musica;
        byte[] bloco;
        ArrayList<Campo> campos = new ArrayList<>();
        
        //Retirar blocos multimédia dos PDUs
        while (i<= blocos.size())
        {
            PDU p = blocos.get(i);
            for (Campo c: p.getCampos())
                if (c instanceof CampoMultimedia) campos.add(c);
            i++;
        }
        //Calcular tamanho do array
        tamanho  = calculaTamanhoCampos(campos);
        musica = new byte[tamanho];
        
        //Escrever para o array
        i=0;
        for (Campo c: campos){
            CampoMultimedia cm = (CampoMultimedia)c;
            bloco = cm.getMultimedia();
            for (j=0;j<bloco.length;j++,i++)
                musica[i]=bloco[j];
        }
        
        return musica;
        
    }
    
    /** 
     * Calcula o tamanho total dos campos 
     * @param campos ArrayList de campos
     * @return O tamanho total
     */
    public static int calculaTamanhoCampos (ArrayList<Campo> campos) {
        int tam = 0;
        for (Campo c: campos)
            tam += (c.getTamanho() & 0xff);
        
        return tam;
    }
    
    /**
     * Calcula a diferença, em milisegundos, da hora actual e uma determinada data
     * @param data Data 
     * @param hora Hora
     * @return Diferença em milisegundos
     */
    public static long stringToDate (String data, String hora) {
        try {
            DateFormat h = new SimpleDateFormat ("yyMMdd,HHmmss");
            String s = data.concat(",").concat(hora);
            Date date = h.parse(s);
            Date dateNow = new Date();
            return (date.getTime()-dateNow.getTime());
        }
        catch (ParseException e ) {System.out.println (e.getMessage());}
        return 0;
    }
    
    /**
     * Converte uma imagem num array de bytes
     * @param path Caminho no qual está a imagem
     * @return Array de bytes com a imagem
     */
    public static byte[] imageToByteArray (String path)
    {
        byte[] data = null;
        try {
            BufferedImage img = ImageIO.read(new File(path));           
            ByteArrayOutputStream baos = new ByteArrayOutputStream();        
            ImageIO.write(img, "jpg", baos);
            baos.flush();
            data = baos.toByteArray();
            return data;
        }
        catch (IOException e) {System.out.println (e.getMessage());}
        return data;
        
    }
    
    /**
     * Converte um array de bytes (correspondente a um trecho musical) em vários PDUs.
     * @param data Array de bytes com o trecho
     * @param label Label para o PDU
     * @return ArrayList com os vários PDUs (cada um correspondendo a um bloco).
     */
    public static ArrayList<PDU> songToPDUs (byte[] data, short label){
        byte [] bloco;
        int tamanho,i,j,limite, seq=1;
        tamanho = data.length; i=0;
        ArrayList<Campo> campos;
        ArrayList<PDU> res = new ArrayList<>();  
        while (i<tamanho)
        {
            if ((tamanho-i)<BLOCO) limite = tamanho-i;
            else limite = BLOCO;
            bloco = new byte[limite];
            for (j=0;j<limite;j++,i++)
                bloco[j] = data[i];
            campos = multimediaToArrayList(1,bloco);
            
            CampoByte numeroBloco = new CampoByte ((byte)17,(byte)1, (byte)seq);
            campos.add(0,numeroBloco);
            if (limite==BLOCO){
                CampoByte listaNaoAcabou = new CampoByte ((byte)254, (byte)1, (byte)0);
                campos.add(listaNaoAcabou);
            }
            System.out.println((campos.get(campos.size()-1).getTipoCampo()) & 0xff);
            System.out.println (campos.size());
              
            int tamanhoCampos = calculaTamanhoCampos(campos) + 2*campos.size();
            PDU pdu = new PDU (label,(byte)0,(byte)campos.size(),(short)(tamanhoCampos),campos);
            res.add(pdu);
            seq++;
            
        }
        return res;
    }
    
    
    /** Converte um array de bytes em Campos.
     * opt == 0 [Imagem ] || opt == 1 [Música]
     * @param opt Flag para diferenciar imagem de música. 
     * @param data Array de bytes 
     * @return ArrayList com os vários campos
     */
    public static ArrayList<Campo> multimediaToArrayList (int opt, byte[] data)
    {   
        int i=0,j, size;
        byte[] frag;
        ArrayList<Campo> campos = new ArrayList<>();
        CampoMultimedia cm = null;
        while (i<data.length)
            {
                if ((data.length-i)<PDU_SIZE) size = data.length-i;
                else size = PDU_SIZE;
                frag = new byte[size];
                for (j=0;j<size;j++,i++)
                    frag[j] = data[i];
                if (opt==0)  cm = new CampoMultimedia ((byte)16,(byte)size,frag);
                if (opt==1)  cm = new CampoMultimedia ((byte)18,(byte)size,frag);
                Campo c = (Campo) cm;
                campos.add(c);
            }
        return campos;
    }
    
    
    
    public static byte[] songToByteArray (String path)
    {
        File source = new File(path);
        byte[] data = new byte[0];
        try
        {
            AudioInputStream is = AudioSystem.getAudioInputStream(source);
            DataInputStream dis = new DataInputStream(is);      //So we can use readFully()
            AudioFormat format = is.getFormat();
            data = new byte[(int)(is.getFrameLength() * format.getFrameSize())];
            dis.readFully(data);
            dis.close();
        }
        catch (IOException | UnsupportedAudioFileException e) {System.out.println (e.getMessage());}
        
        return data;
    }
    
    public static ArrayList<Campo> perguntasToArrayList (Questao q, Desafio ev, int i){
        ArrayList<Campo> campos = new ArrayList<>();
        int n=1;
        CampoString nomeJogo = new CampoString ((byte)7,(byte)ev.getNome().length(),ev.getNome());
        campos.add(nomeJogo);
        CampoByte numPergunta = new CampoByte ((byte)10,(byte)1,(byte)i);
        campos.add(numPergunta);
        CampoString pergunta = new CampoString ((byte)11,(byte)q.getPergunta().length(),q.getPergunta());
        campos.add(pergunta);
        for (String s: q.getRespostas())
        {
            CampoByte id = new CampoByte ((byte)12,(byte)1,(byte)n);
            CampoString r = new CampoString ((byte)13, (byte)s.length(),s);
            campos.add(id);campos.add(r);
            n++;
        }
        return campos;
    }
    
    public static byte[] readBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
      
        // Get the size of the file
        long length = file.length();
  
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
          throw new IOException("Could not completely read file " + file.getName() + " as it is too long (" + length + " bytes, max supported " + Integer.MAX_VALUE + ")");
        }
  
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
  
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
      
    public static void writeBytesToFile(File theFile, byte[] bytes) throws IOException {
        BufferedOutputStream bos = null;
      
        try {
            FileOutputStream fos = new FileOutputStream(theFile);
            bos = new BufferedOutputStream(fos); 
            bos.write(bytes);
        }finally {
            if(bos != null) {
              try  {
                //flush and close the BufferedOutputStream
                bos.flush();
                bos.close();
              } catch(Exception e){}
            }
        }
    }
    
    public static ArrayList<String> leFicheiroIP (String filename){
        
        ArrayList<String> linhas = new ArrayList<String>();
        Scanner scan = null;
        try
        {
            scan = new Scanner (new FileReader (filename));
            scan.useDelimiter (System.getProperty("line.separator"));
            while (scan.hasNext()) linhas.add (scan.next());
        }
        catch (IOException e) {System.out.println (e.getMessage());}
        return linhas;
        
    }
    
    public static HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return ((Comparable) ((Map.Entry) (o2)).getValue())
                  .compareTo(((Map.Entry) (o1)).getValue());
            }
       });

       // Here I am copying the sorted list in HashMap
       // using LinkedHashMap to preserve the insertion order
       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
  }
    
    public static void concatenaMaps (HashMap<String,Integer> global, HashMap<String,Integer> local) {
        int pontos;
        for (Map.Entry<String,Integer> e : local.entrySet()) {
            if (global.containsKey(e.getKey())){
                pontos = global.get(e.getKey());
                global.remove(e.getKey());
                pontos += e.getValue();
                global.put (e.getKey(), pontos);
            }
            else global.put(e.getKey(), e.getValue());
        }
                
    
    
    }
}
