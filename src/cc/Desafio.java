/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;
import java.util.Map;
import java.util.Scanner;


/**
 * Classe que representa um desafio.
 * @author José Cortez, Miguel Zenha, André Costa
 */
public class Desafio implements Serializable {
    
    private String data;
    private String hora;
    private String nome;
    private HashMap<String,Integer> concorrentes;
    private ArrayList<Questao> questoes;
    private  String path;
    
                    /** Construtores **/
    
    /**
     * Construtor vazio
     */
    public Desafio ()
    {
        data = hora = nome =  "";
        concorrentes = new HashMap<String,Integer>();
        questoes = new ArrayList<>();
    }
    
    /**
     * Construtor parametrizado
     * @param nome Nome do desafio
     * @param data Data do desafio
     * @param hora Hora do desafio
     */
    public Desafio (String nome, String data, String hora)
    {
        this.nome = nome;
        this.data = data;
        this.hora = hora;
        this.concorrentes = new HashMap<>();
        this.questoes = new ArrayList<>();
        this.path = (System.getProperty("user.dir")+"\\resources");
        questoes = preencheQuestoes();
        
    }
    
    /**
     * Construtor parametrizado
     * @param data Nome do desafio
     * @param hora Data do desafio
     * @param nome Hora do desafio
     * @param u Mapeamento dos concorrentes
     */
    public Desafio (String data, String hora,String nome, HashMap<String,Integer> u)
    {
        this.data = data;
        this.hora = hora;
        this.nome = nome;
        HashMap<String,Integer> aux = new HashMap<>();
        for (Map.Entry<String,Integer> ut: u.entrySet())
            aux.put(ut.getKey(),ut.getValue());
        this.concorrentes = aux;
    }
    
    /**
     * Construtor de cópia
     * @param d Desafio a ser copiado
     */
    public Desafio (Desafio d)
    {
        this.data = d.getData();
        this.hora = d.getHora();
        this.nome = d.getNome();
        this.concorrentes = d.getConcorrentes();
    }
    
    
                    /** Métodos Get e Set **/
    public String getData () { return this.data;}
    public String getHora () { return this.hora;}
    public String getNome () { return this.nome;}
    public HashMap<String,Integer> getConcorrentes () {
        HashMap<String,Integer> aux = new HashMap<>();
        for (Map.Entry<String,Integer> ut: concorrentes.entrySet())
            aux.put(ut.getKey(),ut.getValue());
        
       return aux;
    }
    public ArrayList<Questao> getQuestoes () {
        ArrayList<Questao> aux = new ArrayList<>();
        for (Questao q: questoes)
            aux.add(q.clone());
        return aux;
    }
    
    public void setNome (String nome) {this.nome = nome;}
    public void setData (String data) {this.data = data;}
    public void setHora (String hora) {this.hora = hora;}
    public void setConcorrentes (HashMap<String,Integer> aux) { this.concorrentes = aux;}
    
    
    
    /**
     * Método que inscreve um concorrente num desafio.
     * @param alcunha Alcunha do concorrente
     */
    public void increveConcorrente (String alcunha) {
        if (!this.concorrentes.containsKey(alcunha))
            concorrentes.put(alcunha,0);
    }
    
    /**
     * Incrementa a pontuação dum concorrente.
     * @param alcunha Alcunha dum concorrente
     * @param pontos Incremento de pontos
     */
    public void incrementaPontuacao (String alcunha, int pontos) {
        int actual;
        if (this.concorrentes.containsKey(alcunha))
        {
            actual = concorrentes.get(alcunha);
            concorrentes.remove(alcunha);
            concorrentes.put (alcunha, actual+pontos);
        }
    }
    
    /**
     * Retorna os pontos dum determinado concorrente.
     * @param alcunha Alcunha do concorrente.
     * @return Número de pontos do concorrente
     */
    public int getPontosDoConcorrente (String alcunha) {
        if (concorrentes.containsKey(alcunha))
            return concorrentes.get(alcunha);
        return 0;
    }
    
    /**
     * Verifica se um concorrente está inscrito.
     * @param alcunha Alcunha do utilizador
     * @return Valor booleano relativo à existência do concorrente no desafio
     */
    public boolean concorrenteEstaInscrito (String alcunha) { return this.concorrentes.containsKey(alcunha);}
    
    /**
     * A partir da directoria, preenche todas as questões existentes do desafio.
     * @return As questões que acabaram de ser preenchidas
     */
    public ArrayList<Questao> preencheQuestoes () {
        ArrayList<Questao> q = new ArrayList<>();
        int i, respostaCerta;
        String[] partes;
        String imagem, musica, pergunta;
        ArrayList<String> respostas;
        ArrayList<String> linhas = leLinhas(path+"\\desafio-000001.txt");
        for (String s : linhas)
        {
            partes = s.split(",");
            if (partes.length!=1) 
            {
                respostas = new ArrayList<>();
                musica = (path+"\\musica\\"+partes[0]);
                imagem = (path+"\\imagens\\"+partes[1]);
                pergunta  = partes[2];
                respostas.add(partes[3]);
                respostas.add(partes[4]);
                respostas.add(partes[5]);
                respostaCerta = Integer.parseInt(partes[6]);
                Questao aux = new Questao (imagem,musica,pergunta,respostas,respostaCerta);
                q.add(aux);
            }
        }
        
        return q;
    }
    
    /**
     * Lê e armazena um ficheiro de texto linha a linha.
     * @param fich Directoria do ficheiro.
     * @return As linhas do ficheiro de texto lido
     */
    public ArrayList<String> leLinhas (String fich)
    {
        ArrayList<String> linhas = new ArrayList<String>();
        Scanner scan = null;
        try
        {
            scan = new Scanner (new FileReader (fich));
            scan.useDelimiter (System.getProperty("line.separator"));
            while (scan.hasNext()) linhas.add (scan.next());
        }
        catch (IOException e) {System.out.println (e.getMessage());}
        return linhas;
    }
    
                        /** Métodos clone e toString **/
    @Override
    public Desafio clone () {
        return new Desafio(this);
    }
    
    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder ();
        sb.append ("Nome do desafio: ");
        sb.append (this.nome);
        sb.append ("\n-----------\n");
        for (Map.Entry<String,Integer> e: concorrentes.entrySet())
        {
            sb.append ("Concorrente: ");
            sb.append (e.getKey());
            sb.append ("| Pontos: ");
            sb.append (e.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }
}
