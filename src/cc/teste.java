/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Work
 */
public class teste {
    
    private static final int PDU_SIZE = 255;
    private static final String path = (System.getProperty("user.dir")+"\\resources");
    private static Object lock = new Object();
    
    
    public static void main (String[] args) {
       
        HashMap<String,Integer> g = new HashMap<>();
        HashMap<String,Integer> l = new HashMap<>();
        
        g.put("Laura", 10);
        g.put("Jhonny", 20);
        l.put("Jhonny",1);
        l.put("Mike",5);
        gUtils.concatenaMaps (g,l);
        
        for (Map.Entry<String,Integer> e: g.entrySet())
            System.out.println ("KEY: "+e.getKey() + "| Value: "+e.getValue());

    }
    
    public static byte[] imageToByteArray (String path)
    {
        byte[] data = new byte[0];
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
    
    public static byte[] InputStreamToByteArray (InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        try {
            int reads = is.read();

            while (reads !=-1){
                baos.write(reads);
                reads = is.read();
            }
        }
        catch (IOException e) {System.out.println (e.getMessage());}
        return baos.toByteArray();  
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
  
 /* private static  void playMp3(byte[] mp3SoundByteArray) {
    try {
        // create temp file that will hold byte array
        File f = new File (path+"\\musica");
        File tempMp3 = File.createTempFile("kurchina", "mp3", f);
        tempMp3.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempMp3);

        fos.close();
        Media m = new Media(path"\\musica");

        // Tried reusing instance of media player
        // but that resulted in system crashes...  
        MediaPlayer mediaPlayer = new MediaPlayer(tempMp3);

        // Tried passing path directly, but kept getting 
        // "Prepare failed.: status=0x1"
        // so using file descriptor instead
        FileInputStream fis = new FileInputStream(tempMp3);
        mediaPlayer.setDataSource(fis.getFD());

        mediaPlayer.prepare();
        mediaPlayer.start   ();
    } catch (IOException ex) {
        String s = ex.toString();
        ex.printStackTrace();
    }
    }*/
  
        

}


