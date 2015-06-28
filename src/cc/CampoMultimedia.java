
package cc;

/**
 * Campo que vai transportar um array de bytes que corresponde a uma porção dum ficheiro multimédia (.jpg ou .mp3)
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class CampoMultimedia extends Campo {
    
    private byte[] multimedia;
    
    
                                    /** Construtores **/
    
    /**
     * Construtor vazio
     */
    public CampoMultimedia (){
        super();
        this.multimedia = new byte[0];
    }
    
    /**
     * Construtor parametrizado
     * @param tipo  Tipo de campo
     * @param tamanho tamanho do array de bytes armazenado
     * @param dados  Array de bytes
     */
    public CampoMultimedia (byte tipo, byte tamanho, byte[] dados){
        super(tipo,tamanho);
        this.multimedia = dados;
    }
    
    /**
     * Construtor de cópia
     * @param cm CampoMultimedia a ser copiado
     */
    public CampoMultimedia (CampoMultimedia cm){
        super (cm.getTipoCampo(),cm.getTamanho());
        this.multimedia= cm.getMultimedia();
    }
    
                                    /** Métodos Get e Set */
    public byte[] getMultimedia() {return this.multimedia;}
    public void setMultimedia (byte[] m) { this.multimedia = m;}
    
    
                                /** Métodos equals, clone e toString**/
    public boolean equals (Object o){
        if (this==o) return true;
        if (this==null || this.getClass()!=o.getClass())return false;
        CampoMultimedia cm = (CampoMultimedia)o;
        return (super.equals(cm) && this.multimedia.equals(cm.getMultimedia()));
    }
    
    public CampoMultimedia clone () {
        return new CampoMultimedia (this);
    }
    
    @Override
    public String toString () {
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        s.append ("Bloco de imagem de");
        s.append ("\"");
        return s.toString();
    }
    
    
}