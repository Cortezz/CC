package cc;

import java.util.ArrayList;
import java.io.Serializable;



/** Classe que representa uma questão.
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class Questao implements Serializable{
    
    private String imagem;
    private String musica;
    private String pergunta;
    private ArrayList<String> respostas;
    private int respostaCerta;
    
            
    
                            /** Construtores **/
    
    /**
     * Construtor vazio
     */
    public Questao(){
        this.imagem = this.musica = this.pergunta = "";
        this.respostas = new ArrayList<>();
        this.respostaCerta = 0;
    }
    
    /**
     * Construtor parametrizado.
     * @param img Nome do ficheiro da imagem 
     * @param mus Nome do ficheiro de música
     * @param perg Pergunta
     * @param resp Respostas
     * @param certa Resposta certa
     */
    public Questao (String img, String mus, String perg, ArrayList<String> resp, int certa)
    {
        this.imagem = img;
        this.musica = mus;
        this.pergunta = perg;
        this.respostaCerta = certa;
        ArrayList<String> aux = new ArrayList<>();
        for (String s: resp)
            aux.add(s);
        this.respostas = aux;
    }
    
    /**
     * Construtor de cópia
     * @param q  Questão a ser copiada
     */
    public Questao (Questao q) {
        this.imagem = q.getImagem();
        this.musica = q.getMusica();
        this.pergunta = q.getPergunta();
        this.respostaCerta = q.getRespostaCerta();
        this.respostas = q.getRespostas();
    }
    
    
    
                        /** Métodos Get e Set **/
    public String getImagem() {return this.imagem;}
    public String getMusica() {return this.musica;}
    public String getPergunta() {return this.pergunta;}
    public int getRespostaCerta () {return this.respostaCerta;}
    public ArrayList<String> getRespostas() {
        ArrayList<String> aux = new ArrayList<>();
        for (String s: respostas)
            aux.add(s);
        return aux;
    }
    
    public void setImagem (String img) {this.imagem = img;}
    public void setMusica (String mus) {this.musica = mus;}
    public void setPergunta (String perg) { this.pergunta = perg;}
    public void setRespostaCerta (int certa) {this.respostaCerta = certa;}
    public void setRespostas (ArrayList<String> respostas) {
        ArrayList<String> aux = new ArrayList<>();
        for (String s: respostas)
            aux.add(s);
        this.respostas = aux;
    }
    
    
                            /** Métodos equals, clone e toString **/
    
    public boolean respostas_equals (ArrayList<String> respostas) {
        if (respostas.size()!=this.respostas.size()) return false;
        int i;
        for (i=0;i<respostas.size();i++)
            if (this.respostas.get(i)!=respostas.get(i)) return false;
        return true;
    }
    
    @Override
    public boolean equals (Object o ) {
        if (this==o ) return true;
        if (this==null || this.getClass()!=o.getClass()) return false;
        Questao q = (Questao)o;
        return (this.imagem.equals(q.getImagem()) && this.musica.equals(q.getMusica()) && this.pergunta.equals(q.getPergunta())
                && this.respostaCerta==q.getRespostaCerta() && respostas_equals(q.getRespostas()));
    }
    @Override
    public Questao clone () {
        return new Questao(this);
    }
    
    @Override
    public String toString () {
        int i=0;
        StringBuilder s = new StringBuilder ();
        s.append ("Pergunta: ");
        s.append (this.pergunta);
        s.append ("\n");
        s.append ("Respostas:\n");
        for (String r: this.respostas)
        {
            s.append(i).append(" - ").append(r);
            i++;
        }
        s.append ("\nResposta certa: ");
        s.append (this.respostaCerta).append("\n");
        return s.toString();
    }
}
