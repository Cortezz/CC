package cc;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/** Classe que vai tratar de todos os pedidos que cheguem ao servidor.
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class UDPClientHandler extends Thread {
    
    private static Gestor gestor;
    private static DatagramSocket socket;
    private static PDU p;
    private static DatagramPacket packet;
    
    private static final int PACKETSIZE = 300;
    private static final int PDU_SIZE = 255;
    private static final int BLOCO = 50000;
    
    
    /**
     * Construtor parametrizado
     * @param c DatagramSocket fornecida pelo servidor
     * @param g Gestor partilhado pelos vários ClientHandlers
     * @param d DatagramPacket enviado pelo Utilizador
     */
    public UDPClientHandler (DatagramSocket c, Gestor g, DatagramPacket d)
    {
        this.socket = c;
        this.gestor = g;
        this.p = new PDU (d.getData());
        this.packet = d;
    }
    
    @Override
    public void run ()
    {
        System.out.println(p);
        int tipo = p.getTipo();
            switch (tipo) {
                case 1:
                    replyHello (p,packet);
                    System.out.println( packet.getAddress() + " " + packet.getPort() + ": Received Hello!");
                    break;
                case 2:
                    replyRegister (p,packet);
                    break;
                case 3:
                    replyLogin (p,packet);
                    break;
                case 4:
                    replyLogout (p,packet);
                    break;
                case 7:
                    replyListChallenges (p,packet);
                    break;
                case 8:
                    replyMakeChallenge (p,packet);
                    break;
                case 9:
                    replyAcceptChallenge (p,packet);
                    break;    
                case 11:
                    replyAnswer(p,packet);
                    break;
                case 13:
                    replyListRanking (p,packet);
                    break;
                default:
                    System.out.println ("Tipo desconhecido!");
                    break;   
            }
    }
    
    /** Resposta ao pedido HELLO (Tipo 01).
     * @param p PDU enviado pelo utilizador
     * @param packet DatagramPacket enviado pelo utilizador
     */
    public static void replyHello (PDU p, DatagramPacket packet) {
        byte[] data;
        PDU novo = new PDU (p.getLabel(),(byte)0,(byte)0,(short)0);
        
        try {
            data = novo.PDUtoByteArray();
            DatagramPacket packet2 = new DatagramPacket (data, data.length, packet.getAddress(),packet.getPort());
            socket.send(packet2);
        }
        catch (Exception e) {System.out.println (e.getMessage());}
    }
    
    
    /**
     * Resposta ao pedido REGISTER (Tipo 02).
     * @param p PDU enviado pelo utilizador
     * @param packet  DatagramPacket enviado pelo utilizador
     */
    public static void replyRegister (PDU p, DatagramPacket packet) {
        String nome,alcunha,pwd;
        byte[] data;
        
        //Se houver três campos, está bem constituído.
        ArrayList<Campo> c = p.getCampos();
        if (c.size()==3)
        {
            //Nome
            CampoString cs = (CampoString)c.get(0);
            nome = cs.getValor();
            //Alcunha
            CampoString ca = (CampoString)c.get(1);
            alcunha = ca.getValor();
            //Password
            CampoString cp = (CampoString)c.get(2);
            pwd = cp.getValor();
            
            gestor.registaUtilizador(nome, alcunha, pwd); 
            gestor.addUtilizador(alcunha);
        }
        try {
            //Construção e envio do PDU
            PDU novo = new PDU (p.getLabel(),(byte)0,(byte)0,(short)0);
            data = novo.PDUtoByteArray();
            DatagramPacket packet2 = new DatagramPacket (data, data.length, packet.getAddress(),packet.getPort());
            socket.send(packet2);
        }
        catch (Exception e) {System.out.println (e.getMessage());}
    }
    
    
    /**
     * Resposta ao pedido LOGIN (Tipo 03).
     * @param p PDU enviado pelo utilizador.
     * @param packet DatagramPacket enviado pelo utilizador.
     */
    public static void replyLogin (PDU p, DatagramPacket packet) {
        String alcunha="",pwd="", nome="";
        String erro="";
        byte[] data;
        
        //Se houver dois campos, quer dizer que está bem constituído
        ArrayList<Campo> c = p.getCampos();
        if (c.size()==2)
        {
            //Alcunha
            CampoString ca = (CampoString)c.get(0);
            alcunha = ca.getValor();
            //Password
            CampoString cp = (CampoString)c.get(1);
            pwd = cp.getValor();
        }
            //Verifica se está registado
            if (!gestor.existeUtilizador(alcunha))
                erro = "Ainda nao esta registado";
            else {
                //Verifica se os dados estão correctos
                if (!gestor.validaUtilizador(alcunha, pwd))
                    erro = "Alcunha e/ou password erradas";
                else {
                    //Verifica se tem sessão activa
                    if (gestor.utilizadorEstaActivo(alcunha))
                        erro = "Ja esta com a sessao iniciada";
                    else 
                    {
                        gestor.utilizadorIniciaSessao(alcunha,packet.getAddress());
                        nome = gestor.getNomeUtilizador(alcunha);
                        System.out.println(alcunha);
                    }
                }
            }
        try {
            PDU novo;
            //Foi detectado algum tipo de erro
            if (!erro.equals("")){  
                CampoString csErro = new CampoString ((byte)255, (byte)erro.length(),erro);
                novo = new PDU ((byte)0, (byte)0, p.getLabel(),(byte)0,(byte)1,(short)(2+erro.length()),csErro);
            }
            //Sem erros
            else {
                CampoString cs = new CampoString ((byte)1,(byte)nome.length(),nome);
                CampoShort csh = new CampoShort ((byte)20,(byte)2,(short)gestor.getScore(alcunha));
                novo = new PDU ((byte)0, (byte)0,p.getLabel(),(byte)0,(byte)2,(short)(4+nome.length()+2),cs,csh);
            }
            //Envio do PDU
            data = novo.PDUtoByteArray();
            DatagramPacket packet2 = new DatagramPacket (data, data.length, packet.getAddress(),packet.getPort());
            socket.send(packet2);
        }
        catch (Exception e) {System.out.println (e.getMessage());}
    }
    
    
    /**
     * Resposta ao pedido LOGOUT (Tipo 04).
     * @param p PDU enviado pelo utilizador
     * @param packet DatagramPacket enviado pelo utilizador
     */
    public static void replyLogout (PDU p, DatagramPacket packet) {
        byte[] data;
        PDU novo = new PDU (p.getLabel(),(byte)0,(byte)0,(short)0);
        Utilizador  u = gestor.procuraUtilizadorPorIP(packet.getAddress());
        u.setSessaoInactiva();
        
        try {
            //construção e envio do PDU
            data = novo.PDUtoByteArray();
            DatagramPacket packet2 = new DatagramPacket (data, data.length, packet.getAddress(),packet.getPort());
            socket.send(packet2);
        }
        catch (Exception e) {System.out.println (e.getMessage());}
    }
    
    /**
     * Resposta ao pedido LIST_CHALLENGE(Tipo 07).
     * @param p PDU enviado pelo utilizador
     * @param packet DatagramPacket enviado pelo utilizador
     */
    public static void replyListChallenges (PDU p, DatagramPacket packet){
        byte[]resp;
        PDU r;
        int tamanho=0;
        ArrayList<Desafio> desafios = gestor.getListaDesafios();
        ArrayList<Campo> campos = new ArrayList<>();
        
        //Construção dos campos com todos os desafios
        for (Desafio d: desafios)
        {
            CampoString nome = new CampoString ((byte)7,(byte)d.getNome().length(),d.getNome());
            tamanho += 2 + d.getNome().length();
            CampoString data = new CampoString ((byte)4,(byte)6,d.getData());
            CampoString hora = new CampoString ((byte)5,(byte)6,d.getHora());
            campos.add(nome);campos.add(data);campos.add(hora); 
            tamanho += 16;
        }
        //Construção do PDU
        r = new PDU (p.getLabel(),(byte)0,(byte)campos.size(),(byte)tamanho,campos);
        
        try {
            //Envio do PDU
            resp = r.PDUtoByteArray();
            DatagramPacket packet2 = new DatagramPacket (resp, resp.length, packet.getAddress(),packet.getPort());
            socket.send(packet2);
        }
        catch (IOException e) {System.out.println (e.getMessage());}
    }
    
    /**
     * Resposta ao pedido MAKE_CHALLENGE (Tipo 08).
     * @param p PDU enviado pelo utilizador
     * @param packet DatagramPacket enviado pelo utilizador
     */
    public static void replyMakeChallenge (PDU p, DatagramPacket packet){
        byte[] data,resp;
        CampoString dataJogo=null, horaJogo=null, nomeJogo;
        PDU r= null;
        
        ArrayList<Campo> c = p.getCampos();
        nomeJogo = (CampoString) c.get(0);
        
        //Se só houver um tipo, quer dizer que a data e hora são as pré-definidas, por defeito.
        if (p.getNumeroCampos()==1){
                //data do desafio
                DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                Date d = new Date();
                String s = dateFormat.format(d);
                dataJogo = new CampoString ((byte)4,(byte)6,s);
                
                //hora do desafio
                DateFormat hourFormat = new SimpleDateFormat("HHmmss");
                Date h = new Date(System.currentTimeMillis()+5*60*1000);
                String ho = hourFormat.format(h);
                horaJogo = new CampoString ((byte)5,(byte)6,ho);
                r = new PDU ((byte)0,(byte)0,p.getLabel(),(byte)0,(byte)3,(short)(18+nomeJogo.getValor().length()),
                    nomeJogo, dataJogo, horaJogo);
                
                gestor.criaDesafio(nomeJogo.getValor(), dataJogo.getValor(), horaJogo.getValor());
        }
        
        //Data e hora escolhidas pelo utilizador
        if (p.getNumeroCampos()==3){
            dataJogo = (CampoString)c.get(1);
            horaJogo = (CampoString)c.get(2);
            r = new PDU (p);
            r.setTipo((byte)0);
            gestor.criaDesafio(nomeJogo.getValor(),dataJogo.getValor(), horaJogo.getValor());
            System.out.println (r.toString());
        }
        
        try {
            //Envio do PDU
            data = r.PDUtoByteArray();
            DatagramPacket packet2 = new DatagramPacket (data, data.length, packet.getAddress(),packet.getPort());
            socket.send(packet2);
        }
        catch (IOException e) {System.out.println (e.getMessage());}
    }
    
    
    /**
     * Resposta ao pedido ACCEPT_CHALLENGE (Tipo 09).
     * @param p PDU enviado pelo utilizador.
     * @param packet DatagramPacket enviado pelo utilizador
     */
    public static void replyAcceptChallenge (PDU p, DatagramPacket packet) {
        byte[] resp;
        PDU r=null;
        String erro="";
        ArrayList<Campo> campos = p.getCampos();
        CampoString cs = (CampoString) campos.get(0);
        
        //Verifica se desafio existe
        if (gestor.existeDesafio(cs.getValor()))
        {
            Desafio e = gestor.getDesafios().get(cs.getValor());
            Utilizador u = gestor.procuraUtilizadorPorIP(packet.getAddress());
            if (u!=null){
                //Verifica se utilizador está inscrito
                if (!gestor.utilizadorEstaInscrito(u.getAlcunha(),cs.getValor()))
                {
                    gestor.registaConcorrente(u.getAlcunha(),cs.getValor());
                    r = new PDU (p.getLabel(),(byte)0,(byte)0,(short)0);
                    
                    //É Lançado um timer com a data do desafio
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                    
                    @Override
                    public void run() {
                       startChallenge(p,packet,e);
                       //gestor.updateRanking(e);

                    }

                    }, gUtils.stringToDate(e.getData(), e.getHora()));
                }
                else erro = "Utilizador esta inscrito";
            }
            else erro = "Utilizador nao existe";
        }
        else erro = "Desafio nao existe";
        
        //Se algum erro ocorrer
        if (!erro.equals("")){
            CampoString cse = new CampoString ((byte)255,(byte)erro.length(),erro);
            r = new PDU ((byte)0,(byte)0,p.getLabel(),(byte)0,(byte)1,(short)(2+erro.length()),cse);
        }
        System.out.println(r.toString());
        try {
            //envio de PDU
            resp = r.PDUtoByteArray();
            DatagramPacket packet2 = new DatagramPacket (resp, resp.length, packet.getAddress(), packet.getPort());
            socket.send(packet2);
        }
        catch (IOException e) {System.out.println (e.getMessage());}
    }
    
    
    /**
     * Resposta ao pedido ANSWER (Tipo 11).
     * @param p PDU enviado pelo utilizador
     * @param packet DatagramPacket enviado pelo utilizador
     */
    public static void replyAnswer (PDU p , DatagramPacket packet) {
        DatagramPacket packet2;
        PDU pdu;
        Utilizador u;
        Desafio e;
        String desafio,alcunha;
        int numQuestao, escolha,tamanho;
        boolean respostaCerta = false;
        ArrayList<Campo> campos = new ArrayList<>();
        CampoString de;
        CampoByte nQ, es, certa, pontos;
        byte[] data;
        
        //Vai buscar o nome do desafio, o número da pergunta a resposta ao PDU
        u = gestor.procuraUtilizadorPorIP(packet.getAddress());
        alcunha = u.getAlcunha();
        campos = p.getCampos();
        de = (CampoString)campos.get(0);
        nQ = (CampoByte)campos.get(1);
        es = (CampoByte)campos.get(2);
        
        
        desafio = de.getValor();
        numQuestao = (nQ.getValor() & 0xff);
        escolha = (es.getValor() & 0xff);
        
        
        respostaCerta = gestor.validaResposta (desafio, numQuestao, escolha);//Verifica se resposta está certa ou não
        //Se estiver certa, o utilizador passa a ter mais dois pontos
        if (respostaCerta) {
            gestor.incrementaPontuacao(desafio,alcunha,2);
            gestor.addPontos(alcunha, 2);
        }
        //Se estiver errada, passa a ter menos um ponto
        else {
            gestor.incrementaPontuacao(desafio, alcunha,-1);
            gestor.addPontos(alcunha, -1);
        }
        
        //Construção e envio do PDU
        campos = new ArrayList<>();
        certa = new CampoByte ((byte)14, (byte)1, (byte)((respostaCerta)?1:0));
        pontos = new CampoByte ((byte)15, (byte)1, (byte)gestor.pontosDoConcorrente(desafio,alcunha));
        campos.add(de); campos.add(nQ); campos.add(certa); campos.add(pontos);
        tamanho = 11+ desafio.length();
        pdu = new PDU (p.getLabel(),(byte)0, (byte)4, (short)tamanho, campos);
        data = pdu.PDUtoByteArray();
        packet2 = new DatagramPacket (data, data.length, packet.getAddress(), packet.getPort());
        try {
            socket.send(packet2);
            System.out.println ("Enviei!");
        }
        catch (IOException ex) {System.out.println (ex.getMessage());}
        
        
    }
    
    public static void replyListRanking (PDU p, DatagramPacket packet) {
        PDU resp;
        byte[] data;
        CampoString alc;
        CampoShort pontos;
        int score;
        DatagramPacket packet2;
        int tamanho = 0;
        ArrayList<Campo> c = new ArrayList<>();
        ArrayList<InfoServidor> servidores;
        Packet pa;
        HashMap<String,Integer> rankingGlobal = new HashMap<String,Integer>();
        HashMap<String, Integer> s;
        
        
        servidores = gestor.getListaServidores();
        if (servidores.size()!=0) {
        for (InfoServidor svd : servidores) {
                try {
                    Socket sock = new Socket (svd.getIP(), svd.getPort());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());

                    pa = new Packet ("RANKING",null);
                    oos.writeObject(pa);
                    pa = (Packet)ois.readObject();
                    HashMap<String,Integer> hm = (HashMap<String,Integer>) pa.getChicha();
                    gUtils.concatenaMaps(rankingGlobal,hm);
                    oos.close();
                    ois.close();
                    sock.close();
                }
            catch (IOException | ClassNotFoundException e) {System.out.println(e.getMessage());}
            }

            gUtils.concatenaMaps(rankingGlobal, gestor.getRanking());
            
            s = gUtils.sortByValues(rankingGlobal);
        }
        
        else s = gUtils.sortByValues(gestor.getRanking());
        
        
        
        
        for (Map.Entry<String, Integer> e : s.entrySet()) {
            alc = new CampoString ((byte)2, (byte)e.getKey().length(), e.getKey()); 
            tamanho += 2 + e.getKey().length();
            score = e.getValue();
            pontos = new CampoShort ((byte)20, (byte)2, (short)score);
            tamanho += 4;
            c.add(alc); c.add(pontos);
        }
        tamanho += 8;
        System.out.println(c.size());
        
        resp = new PDU (p.getLabel(), (byte)0, (byte)c.size(), (short)tamanho, c);
        data = resp.PDUtoByteArray();
        packet2 = new DatagramPacket (data, data.length, packet.getAddress(), packet.getPort());
        try {
                socket.send(packet2);
        }
        catch (IOException e) {System.out.println (e.getMessage());}
    }
    
    
    
    
    /** 
     * Método auxiliar invocado pelo Timer, que enviará as perguntas, respostas, imagens e trechos de música ao utilizador.
     * @param p PDU enviado pelo utilizador (relativo ao pedido ACCEPT_CHALLENGE)
     * @param packet DatagramPacket enviado pelo utilizador (relativo ao pedido ACCEPT_CHALLENGE)
     * @param de Desafio, aos quais estão associados os elementos multimédia, aceite pelo utilizador (relativo ao pedido ACCEPT_CHALLENGE)
     */
    public static void startChallenge (PDU p, DatagramPacket packet, Desafio de) {
        byte[] data, imagem, data2, musica;
        ArrayList<Questao> questoes;
        int tam,i=1;
        DatagramPacket perg, img, mus;
        
        try {
            questoes = de.getQuestoes();
            for (Questao q: questoes)
            {
                //Envio do PDU com o nome do desafio, as perguntas e respostas
                ArrayList<Campo> perguntas = gUtils.perguntasToArrayList(q,de,i);
                tam = gUtils.calculaTamanhoCampos(perguntas);
                PDU pdu = new PDU (p.getLabel(),(byte)0, (byte)perguntas.size(),(short)(tam+2*perguntas.size()),perguntas);
                data = pdu.PDUtoByteArray();
                perg = new DatagramPacket (data, data.length, packet.getAddress(), packet.getPort());
                socket.send(perg);
                
                //Envio do PDU com a imagem
                System.out.println ("A enviar imagem");
                tam = 0;
                imagem = gUtils.imageToByteArray(q.getImagem());
                ArrayList<Campo> c = gUtils.multimediaToArrayList(0,imagem);
                tam = gUtils.calculaTamanhoCampos(c);
                tam += c.size()*2;
                pdu = new PDU (p.getLabel(),(byte)0,(byte)c.size(),(short)tam,c);
                data2 = pdu.PDUtoByteArray();
                img = new DatagramPacket (data2, data2.length, packet.getAddress(), packet.getPort());
                socket.send(img);
                
                //Envio dos blocos de música
                Thread.sleep(400);
                File f = new File (q.getMusica());
                musica = gUtils.readBytesFromFile(f);
                ArrayList<PDU> blocos = gUtils.songToPDUs (musica, p.getLabel());
                int countf = 1;
                for (PDU bloco: blocos)
                {
                    musica = bloco.PDUtoByteArray();
                    mus = new DatagramPacket (musica, musica.length, packet.getAddress(), packet.getPort());
                    socket.send(mus);
                    System.out.println(countf);
                    countf++;
                    Thread.sleep(50);
                }
                i++;
                Thread.sleep(1000*25);
            }
        } catch (InterruptedException |IOException e) {System.out.println (e.getMessage());}
    }
}
