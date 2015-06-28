package cc;
import java.io.Serializable;
/** Classe abstracta que representa um qualquer campo.
 *
 * @author José Cortez, André Costa e Miguel Zenha
 */
public abstract class Campo implements Serializable {
    
    private byte tipoCampo;
    private byte tamanho;
    
    
                  
                        /**Construtores **/
    
    /**
     * Construtor vazio
     */
    public Campo (){
        tipoCampo = tamanho = (byte)0;
    }
    
    /**
     * Construtor parametrizado
     * @param tipo Tipo do campo
     * @param tam tamanho do valor armazenado no campo
     */
    public Campo (byte tipo, byte tam) {
        this.tipoCampo = tipo;
        this.tamanho = tam;
    }
    
    /**
     * Construtor de cópia
     * @param c Campo a ser copiado
     */
    public Campo (Campo c) {
        this.tipoCampo = c.getTipoCampo();
        this.tamanho = c.getTamanho();
    }
    
                                 /**Métodos get e set**/
    public byte getTipoCampo() { return this.tipoCampo;}
    public byte getTamanho() { return this.tamanho;}
    
    public void setTipoCampo (byte tipo) {this.tipoCampo=tipo;}
    public void setTamanho (byte tamanho) {this.tamanho=tamanho;}
    
                                /** Métodos equals e toString**/
    @Override
    public boolean equals (Object o) {
        if (this==o) return true;
        if (this==null || this.getClass()!=o.getClass()) return false;
        Campo c = (Campo)o;
        return (this.tipoCampo == c.getTipoCampo() && this.tamanho==c.getTamanho());
    }
    
    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append (tipoCampo & 0xFF);
        sb.append("=\"");
        return sb.toString();
    }
    
     
}
