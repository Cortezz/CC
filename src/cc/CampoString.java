package cc;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *  Campo que vai transportar uma string
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class CampoString extends Campo implements Serializable{
    private String valor;
    
    
                    /** Construtores **/
    
    /**
     * Construtor vazio
     */
    public CampoString (){
        super();
        this.valor = "";
    }
    
    /**
     * Construtor parametrizado
     * @param tipo  Tipo de Campo
     * @param tamanho   Tamanho da string
     * @param s String a ser armazenada
     */
    public CampoString (byte tipo, byte tamanho, String s){
        super(tipo,tamanho);
        valor = s;
    }
    
    /**
     * Construtor de cópia
     * @param cs  CampoString a ser copiado
     */
    public CampoString (CampoString cs){
        super (cs.getTipoCampo(),cs.getTamanho());
        this.valor = cs.getValor();
    }
    
    
                /** Métodos Get e Set **/
    public String getValor() {return this.valor;}
    public void setValor (String s) { this.valor = s;}
    
    /**
     * Utilizado somente quando o tipo do campo é 4 (Data).
     * 
     * Transforma uma string no formato "yyMMdd" para "dd/MM/yy"
     * @return String que representa a data no formato Little-endian.
     */
    public String rawDataToFormatted () {
        String ano = this.valor.substring(0,2);
        String mes = this.valor.substring(2,4);
        String dia = this.valor.substring(4);
        String res = dia.concat("/").concat(mes).concat("/").concat("20").concat(ano);
        return res;
    }
    
    
    /**Utilizado somente quando o tipo do campo é 5 (Hora).
     * 
     * Transforma uma string no formato "hhmmss" para "hh'h':mm'm':ss's'"
     * @return String que representa a hora no formato em cima descrito.
     */
    public String rawHourToFormatted () {
        String hora = this.valor.substring(0,2);
        String min = this.valor.substring(2,4);
        String seg = this.valor.substring(4);
        String res = hora.concat("h:").concat(min).concat("m:").concat(seg).concat("s");
        return res;
    }
    
    
                        /** Métodos equals, clone e toString **/
    @Override
    public boolean equals (Object o){
        if (this==o) return true;
        if (this==null || this.getClass()!=o.getClass())return false;
        CampoString cs = (CampoString)o;
        return (super.equals(cs) && this.valor.equals(cs.getValor()));
    }
    @Override
    public CampoString clone () {
        return new CampoString (this);
    }
    @Override
    public String toString () {
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        s.append (this.valor);
        s.append ("\"");
        return s.toString();
    }
   
}
