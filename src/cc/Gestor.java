
package cc;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.*;
import java.net.InetAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
       
/**
 * Classe onde serão armazenados os utilizadores e desafios do sistema.
 * @author José Cortez, André Costa e Miguel Zenha.
 */
public class Gestor implements Serializable {
    
    private HashMap<String, Utilizador> utilizadores; 
    private HashMap<String, Desafio> desafios;
    private ArrayList<InfoServidor> servidores;
    private HashMap<String, Integer> ranking;
    
    
    final String dir = "resources/imagens/000001.jpg";
    
    private Lock lock = new ReentrantLock();

    
                    /** Construtores**/
    /**
     * Construtor vazio
     */
    public Gestor (){
        this.utilizadores = new HashMap<>();
        this.desafios = new HashMap<>();
        this.servidores = new ArrayList<>();
        this.ranking = new HashMap<>();
    }
    
    /**
     * Construtor de cópia
     * @param g Gestor a ser copiado
     */
    public Gestor (Gestor g) {
        this.utilizadores = g.getUtilizadores();
        this.desafios = g.getDesafios();
        this.ranking = g.getRanking();
    }
    
    
                        /** Métodos Get e Set **/
    public HashMap<String, Utilizador> getUtilizadores() { 
        HashMap<String,Utilizador> ut = new HashMap<>();
        lock.lock();
        try {
            for(Map.Entry<String,Utilizador> e : utilizadores.entrySet()){
                ut.put(e.getKey(),e.getValue());
            }  
            return ut;
        }
        finally { lock.unlock();}
        
    }
    public HashMap<String,Desafio> getDesafios () {
        lock.lock();
        try {
            HashMap<String,Desafio> ev = new HashMap<>();
            for (Map.Entry<String,Desafio> e : desafios.entrySet())
                ev.put(e.getKey(), e.getValue());
            return ev;
        }
        finally {lock.unlock();}
    }
    
    public HashMap<String,Integer> getRanking () {
        lock.lock();
        try {
            HashMap<String,Integer> rank = new HashMap<>();
            for (Map.Entry<String, Integer> e : ranking.entrySet())
                rank.put (e.getKey(),e.getValue());
            return rank;
        }
        finally { lock.unlock();}
    }
    
    
    
                        /**********************************
                                      Desafios 
                        **********************************/
    /**
     * Método utilizado para criar desafios.
     * @param desafio  Nome do desafio
     * @param data Data do desafio
     * @param hora  Hora do desafio
     */
    public void criaDesafio (String desafio, String data, String hora) {
        lock.lock();
        try {
            Desafio d = new Desafio (desafio,data,hora);
            this.desafios.put(desafio,d);
        }
        finally { lock.unlock();}
    }
    
    /**
     * Retorna a lista dos desafios
     * @return Um ArrayList com os desafios existentes
     */
    public ArrayList<Desafio> getListaDesafios () {
        lock.lock();
        try {
            ArrayList<Desafio> r = new ArrayList<>();
            for (Desafio s: desafios.values())
                r.add(s.clone());
            return r;
        }
        finally { lock.unlock();}
    }
    
    /**
     * Verifica se um desafio existe
     * @param desafio Nome do desafio
     * @return Verdadeiro se desafio existe, falso caso contrário
     */
    public boolean existeDesafio (String desafio) {
        return (this.desafios.containsKey(desafio));
    }
    
    /**
     * Regista um concorrente num evento
     * @param alcunha Alcunha do concorrente
     * @param desafio Nome do desafio
     */
    public void registaConcorrente (String alcunha, String desafio) {
        lock.lock();
        try {
            if (desafios.containsKey(desafio))
                desafios.get(desafio).increveConcorrente(alcunha);
        }
        finally { lock.unlock();}
    }
    
    /**
     * Averigua se um utilizador está inscrito num desafio
     * @param alcunha Alcunha do utilizador
     * @param desafio Nome do desafio
     * @return Verdadeiro se estiver inscrito, falso caso contrário
     */
    public boolean utilizadorEstaInscrito (String alcunha, String desafio) {
        if (this.desafios.containsKey(desafio)){
            Desafio e = desafios.get(desafio);
            return e.concorrenteEstaInscrito(alcunha);
        }
        return false;
    }
    
    
    /**
     *  Incrementa a pontuação dum concorrente, para um dado desafio.
     * @param desafio Nome do desafio
     * @param alcunha Alcunha do concorrente
     * @param incremento Incremento de pontos
     */
    public void incrementaPontuacao (String desafio, String alcunha, int incremento) {
        if (desafios.containsKey(desafio)) {
            desafios.get(desafio).incrementaPontuacao(alcunha, incremento);
        }
    }
    
    
    /**
     * Devolve os pontos de um determinado concorrente, para um dado desafio
     * @param desafio Nome do desafio
     * @param alcunha Alcunha do concorrente
     * @return Número de pontos no determinado desafio
     */
    public int pontosDoConcorrente (String desafio, String alcunha) {
        if (desafios.containsKey(desafio))
            return desafios.get(desafio).getPontosDoConcorrente(alcunha);
        return 0;
    }
    
    /**
     * Valida a resposta a uma questão dum dado desafio.
     * @param nomeDesafio Nome do desafio
     * @param numQuestao Número da questão
     * @param escolha Resposta à questão
     * @return Verdadeiro se a resposta estiver correcta, falso caso contrário
     */
    public boolean validaResposta (String nomeDesafio, int numQuestao, int escolha) {
        Desafio e = desafios.get(nomeDesafio);
        if (e.getQuestoes().get(numQuestao-1).getRespostaCerta() == escolha) return true;
        else return false;
    }
    
    
    
    
    
    
                    /**********************************
                                Utilizador 
                    **********************************/
 
    /**
     * Regista um utilizador
     * @param nome Nome do utilizador
     * @param alcunha Alcunha do utilizador
     * @param pwd Password do utilizador
     */
    public void registaUtilizador (String nome, String alcunha, String pwd) {
        lock.lock();
        try {
            if (!this.utilizadores.containsKey(alcunha))
            {
                Utilizador u = new Utilizador (nome,alcunha,pwd);
                this.utilizadores.put (alcunha,u);
            }
        }
        finally {lock.unlock();}
    }
    
    /**
     * Procura um utilizador, dado um IP.
     * @param ip IP sobre o qual se vai efectuar a pesquisa
     * @return Utilizar assoaciado ao IP. Se não exisiter, é retornado o valor nulo.
     */
    public Utilizador procuraUtilizadorPorIP (InetAddress ip) {
        for (Map.Entry<String,Utilizador> e: utilizadores.entrySet())
            if (e.getValue().getIP().equals(ip))
                return e.getValue().clone();
        return null;
    }
    
    /**
     * Valida as credenciais dum utilizador
     * @param alcunha Alcunha do utilizador
     * @param pwd Password do utilizador
     * @return Verdadeiro se as credenciais forem validadas, falso caso contrário.
     */
    public boolean validaUtilizador (String alcunha, String pwd){
        boolean r;
        String pwd2;
        pwd2 = this.utilizadores.get(alcunha).getPwd();
        if (pwd.equals(pwd2)) return true;
        else return false;
    }
    
    /**
     * Inicia a sessão dum utilizador e actualiza o seu IP
     * @param alcunha Alcunha do utilizador
     * @param ip Endereço IP do utilizador
     */
    public void utilizadorIniciaSessao (String alcunha, InetAddress ip) { 
        this.utilizadores.get(alcunha).setSessaoActiva();
        this.utilizadores.get(alcunha).setIP(ip);
    }
    
    /**
     * Averigua se o utilizador está activo
     * @param alcunha Alcunha do utilizador
     * @return Verdadeiro se utilizador está activo, falso caso contrário
     */
    public boolean utilizadorEstaActivo (String alcunha) {return this.utilizadores.get(alcunha).getSessao();}
    
    /**
     * Verifica se um utilizador existe.
     * @param alcunha Nome do utilizador
     * @return Verdadeiro se existir, falso caso contrário.
     */
    public boolean existeUtilizador (String alcunha) { return this.utilizadores.containsKey(alcunha);}
    
    /**
     * Retorna o nome do utilizador
     * @param alcunha Alcunha do utilizador
     * @return Nome do utilizador
     */
    public String getNomeUtilizador (String alcunha) {return this.utilizadores.get(alcunha).getNome();}
    
    
    /**
     * Retorna a pontuação dum utilizador
     * @param alcunha Alcunha do utilizador
     * @return Pontos do utilizador
     */
    public int getScore (String alcunha) {return this.utilizadores.get(alcunha).getPontos();}
    
    
    
                            /**********************************
                                         Servidores
                            **********************************/
    /** Adiciona um servidor à lista de servidores.
     * 
     * @param ip IP do servidor.
     * @param port Porta do servidor.
     */
    public void adicionaServidor (InetAddress ip, int port) {
        InfoServidor s = new InfoServidor(ip,port);
        servidores.add(s);
    }
    
    /** Obtém uma lista dos servidores com os quais está "conectado".
     * 
     * @return String a representar todos os servidores
     */
    public String listaServidores (){
        StringBuilder sb = new StringBuilder("Lista de Servidores\n");
        int i = 1;
        for (InfoServidor s : this.servidores){
            sb.append("#").append(i).append(" - ");
            sb.append("IP: ").append(s.getIP());
            sb.append("| Port: ").append(s.getPort()).append("\n");
            i++;
        }
        return sb.toString();
    }
    
    public ArrayList<InfoServidor> getListaServidores () {
        ArrayList<InfoServidor> novo = new ArrayList<>();
        for (InfoServidor s : this.servidores)
            novo.add(s);
        return novo;
    }
    
    
                            /******************************
                             *         RANKING            *
                             *****************************/
    
    /**
     * Dado um desafio, actualiza o ranking local com as pontuações desse mesmo jogo.
     * @param d Um desafio.
     */
    public void updateRanking (Desafio d) {
        HashMap<String,Integer> c = d.getConcorrentes();
        for (Map.Entry<String,Integer> e : c.entrySet()){
            if (this.ranking.containsKey(e.getKey())) 
                addPontos (e.getKey(),e.getValue());
            else ranking.put(e.getKey(), e.getValue());
        }
        
    }
    
    /**
     * Adiciona os pontos a um determinado utilizador.
     * @param nome Alcunha do utilizador.
     * @param pontos Pontos a ser adicionados.
     */
    public void addPontos (String nome, int pontos) {
        
            int actual; 
            actual = ranking.get(nome) + pontos;
            ranking.remove(nome);
            ranking.put(nome,actual);
        
    }
    
    public void addUtilizador (String alc) {
        if (!this.ranking.containsKey(alc)){
            ranking.put(alc, 0);
        }
    }
    
    
    
    
    
                            /** Métodos para gravar objectos **/
    
    
    
    public void guardarGestor () throws FileNotFoundException, IOException {
        String d;
        FileOutputStream fos;
        ObjectOutputStream oos;
        
        d = System.getProperty("user.dir");
        fos = new FileOutputStream (d+"\\gestor.obj");
        oos = new ObjectOutputStream (fos);
        
        try {
            oos.writeObject(this);
            oos.close();
        }
        catch (IOException e) { System.out.println ("Erro a gravar!\n");}
    }
    public Gestor carregaGestor () throws IOException, ClassNotFoundException{
        String d;
        FileInputStream fis;
        ObjectInputStream ois;
        
        d = System.getProperty ("user.dir");
        try {
            FileInputStream fin=new FileInputStream(d+"\\gestor.obj");
            ObjectInputStream oin=new ObjectInputStream(fin);
            Gestor g = (Gestor)oin.readObject();
            if(g!=null) return g;
            else return new Gestor();
        }
        catch (IOException e){
            System.out.println("\nerror loading file!\n");
            return new Gestor();
        }  
        
    }
    
    
                            /** Método toString **/
    @Override
    public String toString () {
        StringBuffer sb = new StringBuffer("--Utilizadores--");
        for (Utilizador u: utilizadores.values())
            sb.append(u.toString());
        return sb.toString();
    }
    
}
