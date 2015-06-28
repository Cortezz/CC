package cc;

import java.io.Serializable;
import java.net.InetAddress;

/** Classe que representa o utilizador.
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class Utilizador implements Serializable {
    private String nome;
    private String alcunha;
    private String pwd;
    private boolean sessao;
    private int pontos;
    private InetAddress ip;
    
    
                                /** Construtores **/

    /** Construtor parametrizado
     * 
     * @param nome Nome do utilizador
     * @param alcunha Alcunha do utilizador
     * @param pwd Password do utilizador
     */
    public Utilizador (String nome, String alcunha, String pwd) {
        this.nome = nome;
        this.alcunha = alcunha;
        this.pwd = pwd;
        sessao = false;
        this.pontos = 0;
        this.ip = null;
    }
    
    /**
     *  Construtor vazio
     */
    public Utilizador () {
        this.nome = "";
        this.alcunha = "";
        this.pwd = "";
        sessao = false;
        this.pontos = 0;
        this.ip = null;
    }
    
    /**
     * Construtor de cópia
     * @param u  Utilizador a ser copiado
     */
    public Utilizador (Utilizador u) {
        this.nome = u.getNome();
        this.alcunha = u.getAlcunha();
        this.pwd = u.getPwd();
        this.sessao = u.getSessao();
        this.pontos = u.getPontos();
        this.ip = u.getIP();
    }
    
    
                              /** Métodos Get e Set **/
    public String getNome () {return this.nome;}
    public String getAlcunha () { return this.alcunha;}
    public String getPwd() { return this.pwd;}
    public boolean getSessao() { return this.sessao;}
    public int getPontos() { return this.pontos;}
    public InetAddress getIP () { return this.ip;}
    
    public void setNome (String nome) {this.nome = nome;}
    public void setNomeUtilizador (String nomeU) {this.alcunha = nomeU;}
    public void setPwd (String pwd) { this.pwd = pwd;}
    public void setSessaoActiva () {this.sessao = true;}
    public void setSessaoInactiva () { this.sessao = false;}
    public void setPontos (int p ) { this.pontos = p;}
    public void setIP (InetAddress ip) {this.ip = ip;}
    
    
    /**
     * Incrementa a pontuação dum utilizador.
     * @param incremento Valor do incremento
     */
    public void incrementaPontuacao (int incremento ){this.pontos += incremento;}
    
   
    
    
                            /** Métodos toString, clone e equals **/
    
    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder ("--Utilizador--\n");
        sb.append ("Nome: "+this.nome+"\n");
        sb.append ("Nome Utilizador: "+this.alcunha+"\n");
        sb.append ("Password: "+this.pwd+"\n");
        return sb.toString();
    }
    
    @Override
    public Utilizador clone () { return new Utilizador(this);}
    
    @Override
    public boolean equals (Object o) {
        if (this==o) return true;
        if (this==null || this.getClass()!=o.getClass()) return false;
        Utilizador u = (Utilizador)o;
        return (this.nome.equals(u.getNome()) && this.alcunha.equals(u.getAlcunha()) 
                && this.pwd.equals(u.getPwd()));
    }
    
    
}
