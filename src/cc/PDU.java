
package cc;

import java.util.ArrayList;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/** Classe que representa um PDU.
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class PDU implements Serializable{
    
    private byte versao;
    private byte seguranca;
    private short label;
    private byte tipo;
    private byte numeroCampos;
    private short tamanhoCampos;
    private ArrayList<Campo> campos;
    
    
                                /** Construtores **/

    /**
     * Construtor vazio
     */
    public PDU(){
        versao = seguranca = tipo = numeroCampos = (byte)0;
        label = tamanhoCampos = (short)0;
        campos = new ArrayList<>();
    }
    
    /**Construtor parametrizado (Versão e segurança a 0 e sem campos).
     * 
     * @param label Label to PDU
     * @param tipo Tipo do PDU
     * @param nCampos Número de campos
     * @param tamCampos Tamanho dos campos
     */
    public PDU(short label, byte tipo, byte nCampos, short tamCampos){
        versao = seguranca = (byte)0;
        this.label = label;
        this.tipo = tipo;
        this.numeroCampos = nCampos;
        this.tamanhoCampos = tamCampos;
    }
        
    /** Construtor parametrizado (Versão e segurança a 0, com campos)
     * 
     * @param label Label do PDU 
     * @param tipo Tipo do PDU
     * @param nCampos Número de campos 
     * @param tamCampos Tamanho dos campos
     * @param c Lista com os campos
     */
    public PDU(short label, byte tipo, byte nCampos, short tamCampos, ArrayList<Campo> c){
        versao = seguranca = (byte)0;
        this.label = label;
        this.tipo = tipo;
        this.numeroCampos = nCampos;
        this.tamanhoCampos = tamCampos;
        ArrayList<Campo> aux = new ArrayList<>();
        for (Campo ca: c)
            aux.add(ca);
        this.campos = aux;
        
    }
    
    /** Construtor parametrizado
     * 
     * @param versao Versão
     * @param seg Segurança
     * @param label Label do PDU
     * @param tipo Tipo do PD U
     * @param nCampos Número de campos
     * @param tamCampos Tamanho dos campos
     * @param c Lista de campos
     */
    public PDU(byte versao, byte seg,short label, byte tipo, byte nCampos, short tamCampos, ArrayList<Campo> c){
        this.versao = versao;
        this.seguranca = seg;
        this.label = label;
        this.tipo = tipo;
        this.numeroCampos = nCampos;
        this.tamanhoCampos = tamCampos;
        ArrayList<Campo> aux = new ArrayList<>();
        for (Campo ca: c)
            aux.add(ca);
        this.campos = aux;
        
    }
    
    /**
     * Construtor parametrizado (com um só campo)
     * @param versao Versão
     * @param seg Segurança 
     * @param label Label do PDU
     * @param tipo Tipo do PDU
     * @param nCampos Número de campos 
     * @param tamCampos Tamanho dos campos
     * @param c1 Único campo
     */
    public PDU(byte versao, byte seg,short label, byte tipo, byte nCampos, short tamCampos, Campo c1){
        this.versao = versao;
        this.seguranca = seg;
        this.label = label;
        this.tipo = tipo;
        this.numeroCampos = nCampos;
        this.tamanhoCampos = tamCampos;
        this.campos = new ArrayList<>();
        campos.add(c1);
        
    }
    
    //Com dois campos
    /** Construtor parametrizado (com dois campos)
     * 
     * @param versao Versão 
     * @param seg Segurança
     * @param label Label do PDU
     * @param tipo Tipo do PDU
     * @param nCampos Número de campos
     * @param tamCampos Tamanho dos campos
     * @param c1 Primeiro campo
     * @param c2 Segundo campo
     */
    public PDU(byte versao, byte seg,short label, byte tipo, byte nCampos, short tamCampos, Campo c1, Campo c2){
        this.versao = versao;
        this.seguranca = seg;
        this.label = label;
        this.tipo = tipo;
        this.numeroCampos = nCampos;
        this.tamanhoCampos = tamCampos;
        this.campos = new ArrayList<>();
        campos.add(c1);campos.add(c2);
        
    }
    
    /**Construtor parametrizado (com três campos [máximo permitido])
     * 
     * @param versao Versão
     * @param seg Segurança
     * @param label Label do PDU
     * @param tipo Tipo do PDU
     * @param nCampos Número de campos
     * @param tamCampos Tamanho dos campos
     * @param c1 Primeiro campo 
     * @param c2 Segundo campo
     * @param c3 Terceiro cmapo
     */
    public PDU(byte versao, byte seg,short label, byte tipo, byte nCampos, short tamCampos, Campo c1, Campo c2, Campo c3){
        this.versao = versao;
        this.seguranca = seg;
        this.label = label;
        this.tipo = tipo;
        this.numeroCampos = nCampos;
        this.tamanhoCampos = tamCampos;
        this.campos = new ArrayList<>();
        campos.add(c1);campos.add(c2);campos.add(c3);
        
    }
    
    /**
     * Construtor de cópia
     * @param p PDU a ser copiado
     */
    //Constructor de cópia
    public PDU (PDU p){
        this.versao = p.getVersao();
        this.seguranca = p.getSeguranca();
        this.label = p.getLabel();
        this.tipo = p.getTipo();
        this.numeroCampos = p.getNumeroCampos();
        this.tamanhoCampos = p.getTamanhoCampos();
        if(p.getCampos()!=null)this.campos = p.getCampos();
    }
    
    /**
     * Construtor a partir dum array de bytes
     * @param data Array de bytes a partir do qual será construído o PDU
     */
    public PDU (byte[] data)
    {
        short svalor;
        int tamanho, tipoCampo;
        int i,j,k,n=0;
        
        //Versão
        this.versao = data[0];
        //Segurança
        this.seguranca = data[1];
        //Label
        this.label = (short)gUtils.convertTwoBytesToInt2(data[2],data[3]);
        //Tipo
        this.tipo = data[4];
        //Número de campos
        this.numeroCampos = data[5];
        //Tamanho de campos
        this.tamanhoCampos = (short)gUtils.convertTwoBytesToInt2(data[6],data[7]);
        if (numeroCampos!=(byte)0) // se houver campos
        {
            try {
                this.campos = new ArrayList<Campo>();;
                j=8;
                while (n<(numeroCampos & 0xff)){
                    tipoCampo= (data[j] & 0xff);
                    tamanho = data[j+1] & 0xff;
                    //CampoString
                    if (tipoCampo == 1 || tipoCampo == 2 || tipoCampo == 3 || tipoCampo == 4 
                            || tipoCampo == 5 || tipoCampo==7 || tipoCampo == 11 
                            || tipoCampo == 13 || tipoCampo == -1)
                    {
                        byte[] valor = new byte[tamanho];
                        for (k=0,j=j+2;k<tamanho;j++,k++)
                            valor[k] = data[j];
                        String stringvalor = new String (valor, "UTF-8");
                        CampoString cs = new CampoString((byte)tipoCampo,(byte)tamanho,stringvalor);
                        campos.add(cs);
                        n++;
                    }
                    //CampoByte
                    if (tipoCampo == 6|| tipoCampo ==10 || tipoCampo == 17 || tipoCampo == 12 
                            || tipoCampo == 14 || tipoCampo == 15 || tipoCampo == 254)
                    {
                        CampoByte cb = new CampoByte (data[j], data[j+1], data[j+2]);
                        campos.add(cb);
                        j+=3;
                        n++;
                    }
                    //CampoShort
                    if (tipoCampo == 20)
                    {
                        svalor = (short)gUtils.convertTwoBytesToInt2 (data[j+2],data[j+3]);
                        CampoShort cs = new CampoShort (data[j],data[j+1],svalor);
                        campos.add(cs);
                        j+=4;
                        n++;
                    }
                    //CampoMultimedia
                    if (tipoCampo == 16 || tipoCampo == 18) 
                    {
                        byte[] mm = new byte[tamanho];
                        for (i=0,j=j+2;i<tamanho;j++,i++)
                            mm[i]=data[j];
                        CampoMultimedia cm = new CampoMultimedia ((byte)tipoCampo, (byte)tamanho, mm);
                        campos.add(cm);
                        n++;                       

                    }
                }
            }
            catch (UnsupportedEncodingException e) {System.out.println (e.getMessage());}
        }
            
        
    }
    
                                /** Métodos Get e Set**/
    public byte getVersao () { return this.versao;}
    public byte getSeguranca () { return this.seguranca;}
    public short getLabel () {return this.label;}
    public byte getTipo () {return this.tipo;}
    public byte getNumeroCampos () {return this.numeroCampos;}
    public short getTamanhoCampos () { return this.tamanhoCampos;}
    public ArrayList<Campo> getCampos() { 
        ArrayList<Campo> aux = new ArrayList<>();
        for (Campo c: this.campos)
            aux.add(c);
        return aux;
    }
    
    public void setVersao (byte versao) {this.versao = versao;}
    public void setSeguranca (byte seg) {this.seguranca = seg;}
    public void setLabel (short label) {this.label = label;}
    public void setTipo (byte tipo) {this.tipo = tipo;}
    public void setNumeroCampos (byte nCampos) { this.numeroCampos = nCampos;}
    public void setTamanhoCampos (short tamCampos) {this.tamanhoCampos = tamCampos;}
    public void setCampos (ArrayList<Campo> c) {
        ArrayList<Campo> aux = new ArrayList<>();
        for (Campo ca : c)
            aux.add(ca);
        this.campos = aux;
    }
    
    /**  Retorna o tamanho total do PDU.
     * @return Tamanho total do PDU
     */
    public int tamanhoTotalPDU () {
        return (8+(this.tamanhoCampos & 0xffff));
    }
    
    /** Transforma um PDU no array de bytes.
     * @return Array de bytes que corresponde ao PDU
     */
    public byte[] PDUtoByteArray () {
        int offset,i,j;
        byte[] mm;
                                    /**Parte Fixa**/
        byte[] data = new byte[this.tamanhoTotalPDU()];   
        //Versão
        data[0]=this.versao;
        //Segurança
        data[1]=this.seguranca;
        //Label
        data[2]=(byte)this.label;
        data[3]=(byte)((this.label >> 8) & 0xff);
        //Tipo
        data[4]=this.tipo;
        //Número de campos
        data[5]=this.numeroCampos;
        //Tamanho dos campos
        data[6]=(byte)this.tamanhoCampos;
        data[7]=(byte)((this.tamanhoCampos >> 8) & 0xff);
        if (this.campos==null) return data;
        offset = 8;
                                   /** Lista de campos**/
        for (Campo c: campos)
        {
            data[offset] = c.getTipoCampo();
            data[offset+1] = c.getTamanho();
            //CampoString
            if (c instanceof CampoString)
            {
                CampoString cs = (CampoString)c;
                byte[] valor = cs.getValor().getBytes();
                for (i=0,offset=offset+2;i<(c.getTamanho() & 0xff);i++,offset++)
                    data[offset] = valor[i];
            }
            //CampoShort
            if (c instanceof CampoShort)
            {
                CampoShort csh = (CampoShort) c;
                data[offset+2] = (byte)csh.getValor();
                data[offset+3] = (byte)((csh.getValor() >> 8) & 0xff);
                offset = 4 + offset;
            }
            //CampoByte
            if (c instanceof CampoByte)
            {
                CampoByte cb = (CampoByte) c;
                data[offset+2] = cb.getValor();
                offset += 3;
            }
            //CampoMultimedia
            if (c instanceof CampoMultimedia) 
            {
                CampoMultimedia cm = (CampoMultimedia)c;
                mm = cm.getMultimedia();
                for (i=0,offset=offset+2;i<(c.getTamanho() &0xff);i++,offset++)
                    data[offset] = mm[i];
            }
        }
        return data;
    }
    
    
                                        /** Métodos clone, equals e toString **/
    public boolean campos_equals (ArrayList<Campo> ca){
        int i;
        if (ca.size()!=this.campos.size()) return false;
        for (i=0; i<ca.size();i++)
            if (!ca.get(i).equals(this.campos.get(i))) return false;
        return true;
    }
    
    @Override
    public PDU clone () {
        return new PDU(this);
    }
    
    
    @Override
    public boolean equals (Object o) {
        if (this==o) return true;
        if (this==null || this.getClass()!=o.getClass()) return false;
        PDU p = (PDU)o;
        return (this.versao == p.getVersao() && this.seguranca == p.getSeguranca() && this.label == p.getLabel()
                && this.tipo == p.getTipo() && this.numeroCampos == p.getNumeroCampos() 
                && this.tamanhoCampos==p.getTamanhoCampos() && campos_equals(p.getCampos())); 
    }
    
    @Override
    public String toString () {
        int i=1;
        StringBuilder sb = new StringBuilder(">>PDU[ver=");
        sb.append(this.versao);
        sb.append(",seg=");
        sb.append(this.seguranca);
        sb.append(",label=");
        sb.append(this.label);
        sb.append(",tipo=");
        sb.append(this.tipo);
        sb.append("]\n");
        if (this.numeroCampos!=0){
            sb.append("Lista de Campos: [");
            for (Campo c: campos)
            {
                if (c instanceof CampoString) {
                    CampoString cs = (CampoString)c;
                    sb.append(cs.toString());
                }
                if (c instanceof CampoShort) {
                    CampoShort cs = (CampoShort)c;
                    sb.append(cs.toString());
                }
                if (c instanceof CampoByte) {
                    CampoByte cb = (CampoByte) c;
                    sb.append (cb.toString());
                }
                if (c instanceof CampoMultimedia){
                    CampoMultimedia cm = (CampoMultimedia) c;
                    sb.append(cm.toString());
                }
                if (i!=(int)this.numeroCampos)
                    sb.append (", ");
                i++;
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
