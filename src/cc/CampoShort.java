package cc;

import java.io.Serializable;

/**
 * Campo que vai transportar um short
 * @author José Cortez, André Costa, Miguel Zenha
 */
public class CampoShort extends Campo implements Serializable{
    private short valor;
    
            
            
                            /** Construtores **/
    
    /**
     * Construtor vazio
     */
    public CampoShort (){
        super();
        this.valor = (short)0;
    }
    
    /**
     * Construtor parametrizado
     * @param tipo Tipo de campo
     * @param tamanho Tamanho do short
     * @param s Valor do short
     */
    public CampoShort (byte tipo, byte tamanho, short s){
        super(tipo,tamanho);
        valor = s;
    }
    
    
    /**
     * Construtor de cópia
     * @param cs CampoShort a ser copiado.
     */
    public CampoShort (CampoShort cs){
        super (cs.getTipoCampo(),cs.getTamanho());
        this.valor = cs.getValor();
    }
    
    
                         /** Métodos Get e Set **/
   
    public short getValor() {return this.valor;}
    public void setValor (short s) { this.valor = s;}
    
                    /** Métodos equals, clone e toString **/
    public boolean equals (Object o){
        if (this==o) return true;
        if (this==null || this.getClass()!=o.getClass())return false;
        CampoShort cs = (CampoShort)o;
        return (super.equals(cs) && this.valor==cs.getValor());
    }
    
    public CampoShort clone () {
        return new CampoShort (this);
    }
    public String toString () {
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        s.append (this.valor);
        s.append ("\"");
        return s.toString();
    }
    
}
