/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;    

/**
 *
 * @author Work
 */
public class Packet implements Serializable {
    
    private String msg;
    private Object chicha;
    
    public Packet () {
        this.msg = "";
        this.chicha = null;
    }
    
    public Packet (String m, Object obj) {
        this.msg = m;
        this.chicha = obj;
    }
    public Packet (Packet p) {
        this.msg = p.getMsg();
        this.chicha = p.getChicha();
    }
    
    public String getMsg() { return this.msg;}
    public Object getChicha () { return chicha;}
    
    public Packet clone () { return new Packet(this);}
    public String toString () {
        return ("Mensagem: "+this.msg);
    }
}
