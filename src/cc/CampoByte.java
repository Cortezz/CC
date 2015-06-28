package cc;

import java.io.Serializable;

/**
 * Campo que vai transportar um byte
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class CampoByte extends Campo implements Serializable{
    
    private byte valor;
    
    
    
                                /**Construtores **/
    
    /**
     * Construtor vazio
     */
    public CampoByte (){
        super();
        this.valor = (short)0;
    }
    
    /**
     * Construtor parametrizado
     * @param tipo  Tipo de campo
     * @param tamanho   Tamanho do byte (será sempre 1)
     * @param s Valor do byte
     */
    public CampoByte (byte tipo, byte tamanho, byte s){
        super(tipo,tamanho);
        valor = s;
    }
    
    /**
     * Construtor de cópia
     * @param cb    CampoByte a ser copiado
     */
    public CampoByte (CampoByte cb){
        super (cb.getTipoCampo(),cb.getTamanho());
        this.valor = cb.getValor();
    }
    
    
                            /** Métodos Get e Set**/    
    public byte getValor() {return this.valor;}
    public void setValor (byte s) { this.valor = s;}
    
    
                            /** Métodos Equals, clone e toString**/
    public boolean equals (Object o){
        if (this==o) return true;
        if (this==null || this.getClass()!=o.getClass())return false;
        CampoByte cb = (CampoByte)o;
        return (super.equals(cb) && this.valor==cb.getValor());
    }
    
    public CampoByte clone () {
        return new CampoByte (this);
    }
    public String toString () {
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        s.append (this.valor);
        s.append ("\"");
        return s.toString();
    }
    
}
