package cc;

import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;
import java.util.HashMap;


/**Classe relativa ao cliente. É a partir desta classe que se realizará a comunicação (através de pedidos) com o servidor.
 * 
 * @author José Cortez, André Costa e Miguel Zenha
 */
public class Client {
    
    private static boolean conectado;
    private static final int PACKETSIZE = 300;
    private static Menu mInicial;
    private static final int port = 50000;
    public static DatagramSocket socket;
    public static InetAddress host;
    
    private static String nomeDesafio;
    private static Object lock = new Object();
        
    
    
    

    
    public static void main (String[] args)
    {
        // 172.26.41.78
        // 192.
        conectado = false;
        
        try {host = InetAddress.getByName("192.168.43.6");
                System.out.println(InetAddress.getLocalHost());}
        catch (UnknownHostException e) {System.out.println (e.getMessage());}
        
        
        carregaMenus();
        socket = null;
        
        do {
        System.out.println ("Comandos disponíveis:");
        mInicial.executa();
        switch(mInicial.getOpc()){
            case 1:
                hello();
                break;
            case 2:
                register();
                break;
            case 3:
                login();
                break;
            case 4:
                logout();
                break;
            case 5:
                System.out.println ("QUIT - Ainda não implementado");
                break;
            case 6:
                System.out.println ("QUIT - Ainda não implementado");
                break;
            case 7:
                listChallenges();
                break;
            case 8:
                makeChallenge();
                break;
            case 9:
                acceptChallenge();
                break;
            case 0:
                break;
            case 12:
                play();
                break;
            case 11:
                listRanking();
            default: 
                System.out.println ("Essa opção não existe!");
                break;
            }
        } while (mInicial.getOpc()!=0);
    }
    
    
    /**
     * HELLO (Tipo 01). 
     * Este é o primeiro pedido a ser enviado para o servidor. 
     */
    public static void hello() {
        byte[] data;
        byte[] resp;
        PDU p = new PDU((short)0,(byte)1,(byte)0,(short)0);
        System.out.println (host);
        try{
            
            //InetAddress host = InetAddress.getLocalHost();
           
            socket = new DatagramSocket();
            
            //Envio do pedido
            data = p.PDUtoByteArray();
            DatagramPacket packet = new DatagramPacket (data, data.length, host,port);
            socket.send(packet);
            
            //resposta
            socket.receive( packet ) ;
            resp = packet.getData();
            PDU r = new PDU (resp);
            System.out.println (r.toString());
          
            if (r.getLabel() == p.getLabel() && r.getTipo()==0) conectado = true;
            
        }
        catch (IOException e) {System.out.println (e.getMessage());}
    }
    
    
    /**
     * REGISTER (Tipo 02).
     * Pedido enviado para o servidor de modo a que o cliente fique registado.
     * Para efectuar o registo é necessário nome, alcunha e password. 
     * Caso o processo corra sem problemas, será dito na interface que o registo foi efectuado.
     */
    public static void register () {
        String nome, alc, pwd;
        byte[] data,resp;
        ArrayList<Campo> campos= new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            //Dados para registo
            System.out.print ("Nome: ");
            nome = in.readLine();
            System.out.print ("Alcunha: ");
            alc = in.readLine();
            System.out.print ("Password: ");
            pwd = in.readLine();
            
            //Criação do PDU
            Campo cnome = new CampoString ((byte)1,(byte)nome.length(),nome);
            Campo calc = new CampoString ((byte)2, (byte)alc.length(), alc);
            Campo cpwd = new CampoString ((byte)3, (byte)pwd.length(), pwd);
            campos.add(cnome);campos.add(calc);campos.add(cpwd);
            short tamanho = (short)(6+nome.length()+alc.length()+pwd.length());
            PDU p = new PDU ((byte)0,(byte)0,(short)0,(byte)2,(byte)3,tamanho,cnome,calc,cpwd);
            
            //InetAddress host = InetAddress.getLocalHost();
            socket = new DatagramSocket();
            
            //Envio do PDU
            data = p.PDUtoByteArray();
            DatagramPacket packet = new DatagramPacket (data, data.length, host,port);
            socket.send(packet);
            
            //Resposta do servidor
            socket.receive( packet ) ;
            resp = packet.getData();
            PDU r = new PDU (resp);
            
            if (r.getTipo() ==0) System.out.println ("-----------\nEstá registado!\n-----------");
          }
        catch (Exception e) {System.out.println(e.getMessage());}
    }
    
    
    /** LOGIN (Tipo 03).
     * Pedido que é efectuado quando se pretende fazer o login.
     * É necessário alcunha e password. Caso seja realizado com efectuado, é recebido um PDU com o respectivo nome e pontos.
     * Caso contrário, é recebido um PDU com a mensagem de erro.
     */
    public static void login(){
        String alc, pwd;
        byte[] data,resp;
        resp = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            //Dados para login
            System.out.print ("Alcunha: ");
            alc = in.readLine();
            System.out.print ("Password: ");
            pwd = in.readLine();
            
            //Construção do PDU
            Campo calc = new CampoString ((byte)2, (byte)alc.length(), alc);
            Campo cpwd = new CampoString ((byte)3, (byte)pwd.length(), pwd);
            short tamanho = (short)(4+alc.length()+pwd.length());
            PDU p = new PDU ((byte)0,(byte)0,(short)0,(byte)3,(byte)2,tamanho,calc,cpwd);
            
            //InetAddress host = InetAddress.getLocalHost();
            socket = new DatagramSocket();
            
            //Envio do PDU
            data = p.PDUtoByteArray();
            DatagramPacket packet = new DatagramPacket (data, data.length, host,port);
            socket.send(packet);
            
            //Resposta do servidor
            DatagramPacket packet2 = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
            socket.receive( packet2 ) ;
            resp = packet2.getData();
        }
        
        catch (Exception e) {System.out.println(e.getMessage());}
            
        PDU r = new PDU (resp);
        System.out.println ("----------");
        ArrayList<Campo> c = r.getCampos();
            
        //Se o tipo do PDU for 0 (reply) e o número de campos for 2, o login foi efectuado com sucesso.
        if (r.getTipo() == 0 && r.getNumeroCampos()==2) {
            CampoString cs = (CampoString)c.get(0);
            CampoShort csh = (CampoShort)c.get(1);
            System.out.println("\nSessão Iniciada.");
            System.out.println ("Nome: "+cs.getValor());
            System.out.println ("Score: "+csh.getValor());
            System.out.println ("----------");
        }
        //Caso contrário, erro.
        else if (r.getTipo()==0 && r.getNumeroCampos()==1){
            CampoString cs = (CampoString) c.get(0);
            System.out.println ("ERRO: "+cs.getValor());
        }
    }
    
    
    /** LOGOUT (Tipo 04).
     * Pedido realizado para efectuar logout*/
    public static void logout () {
        byte[] data;
        byte[] resp;
        PDU p = new PDU((short)0,(byte)4,(byte)0,(short)0);
        
        try{
            
            //InetAddress host = InetAddress.getLocalHost();
            socket = new DatagramSocket();
            
            //Envio do PDU
            data = p.PDUtoByteArray();
            DatagramPacket packet = new DatagramPacket (data, data.length, host,port);
            socket.send(packet);
            
            //Resposta do servidor
            socket.receive( packet ) ;
            resp = packet.getData();
            PDU r = new PDU (resp);
          
            //Confirmação de que a resposta é valida
            if (r.getLabel() == p.getLabel() && r.getTipo()==0) 
                System.out.println ("Foi efectuado o logout.");
        }
        catch (IOException e) {System.out.println (e.getMessage());}
    
    }
    
    /** LIST_CHALLENGES (Tipo 07).
     * Com este pedido, obtém-se uma lista com todos os desafios disponíveis.
     */
    public static void listChallenges () {
        byte[] data;
        byte[] resp;
        PDU p = new PDU((short)0,(byte)7,(byte)0,(short)0);
        
        try{
            //InetAddress host = InetAddress.getLocalHost();
            socket = new DatagramSocket();
            
            //Envio do PDU
            data = p.PDUtoByteArray();
            DatagramPacket packet = new DatagramPacket (data, data.length, host,port);
            socket.send(packet);
            
            //Resposta do servidor
            DatagramPacket packet2 = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
            socket.receive( packet2 ) ;
            resp = packet2.getData();
            PDU r = new PDU (resp);
            
            //Disponibiliza os desafios existentes
            if (r.getNumeroCampos()==0) System.out.println ("Não existem desafios...");
            else {
                ArrayList<Campo> campos = r.getCampos();
                for (int i=0;i<campos.size();i+=3){
                    CampoString nome = (CampoString)campos.get(i);
                    CampoString date =(CampoString)campos.get(i+1);
                    CampoString hora = (CampoString) campos.get(i+2);
                    System.out.println (nome.getValor()+": "+date.rawDataToFormatted()+" às "+hora.rawHourToFormatted());
                }
            }
        }
        catch (IOException e) {System.out.println (e.getMessage());}
    }
    
    
    
    /** MAKE_CHALLENGE (Tipo 08).
     * É utilizado este pedido para criar desafios. Pode-se especificar a data e hora ou, por defeito, fica marcado para
     * 5 minutos depois da hora actual.
     */
    public static void makeChallenge() {
        String jogo, hora,dataJ;
        CampoString dataJogo, horaJogo, nomeJogo;
        byte[] data,resp;
        resp = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PDU p;
        
        try{
            
            //Dados do desafio
            System.out.print ("Nome do jogo: ");
            jogo = in.readLine();
            nomeJogo = new CampoString ((byte)7,(byte)jogo.length(),jogo);
            System.out.print ("Quer escolher a data e hora do jogo? (s/n): ");
            hora = in.readLine();
            //Caso não se queira especificar a hora, é construído o PDU só com o nome do jogo.
            if (hora.equals("n")){
                p = new PDU ((byte)0,(byte)0,(short)0,(byte)8,(byte)1,(short)((short)2+jogo.length()),nomeJogo);
            }
            //Se se optar por especificar a data e hora, é construído o PDU com os três campos.
            else {
                //Data do desafio
                System.out.print ("Quer realizar o jogo em que data (yyMMdd): ");
                dataJ = in.readLine();
                if (dataJ.equals("teste"))
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                    Date d = new Date();
                    String s = dateFormat.format(d);
                    dataJogo = new CampoString ((byte)4,(byte)6,s);
                }
                else dataJogo = new CampoString ((byte)4,(byte)6,dataJ);
                    
                //Hora do desafio
                System.out.print ("A que horas (HHmmss): ");
                hora = in.readLine();
                if (hora.equals("teste"))
                {
                    DateFormat hourFormat = new SimpleDateFormat("HHmmss");
                    Date h = new Date(System.currentTimeMillis()+10*1000);
                    String ho = hourFormat.format(h);
                    horaJogo = new CampoString ((byte)5,(byte)6,ho); 
                }
                else  horaJogo = new CampoString ((byte)5,(byte)6,hora);
                p = new PDU ((byte)0,(byte)0,(short)0,(byte)8,(byte)3, (short) ((short)18+jogo.length())
                        ,nomeJogo,dataJogo,horaJogo);
            }
            
            //InetAddress host = InetAddress.getLocalHost();
            socket = new DatagramSocket();
            
            //Envio do PDU
            data = p.PDUtoByteArray();
            DatagramPacket packet = new DatagramPacket (data, data.length, host,port);
            socket.send(packet);
            
            //Resposta do servidor
            DatagramPacket packet2 = new DatagramPacket (new byte[PACKETSIZE], PACKETSIZE ) ;
            socket.receive( packet2 ) ;
            resp = packet2.getData();
            PDU r = new PDU (resp);
            
            System.out.println ("----------\n"+r.toString()+"\n--------------");
        }
        catch (IOException e) {System.out.println(e.getMessage());}
    }
    
    
    /** ACCEPT_CHALLENGE (Tipo 09).
     *  É através deste pedido que se aceita um desafio. 
     *  Se for registado no desafio com sucesso, é recebio um PDU sem campos.
     *  Caso contrário, é recebido um PDU com a mensagem de erro.
     */
    public static void acceptChallenge () {
        byte[] data, resp;
        String jogo = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            System.out.print("Em que jogo queres participar?: ");
            jogo = in.readLine();
        }
        catch (IOException e) {System.out.println (e.getMessage());}
        
        //Construção do PDU
        CampoString cs = new CampoString ((byte)7,(byte)jogo.length(),jogo);
        PDU p = new PDU ((byte)0, (byte)0, (short)0, (byte)9, (byte)1, (short)(2+jogo.length()),cs);
            
        try {    
            
            //InetAddress host = InetAddress.getLocalHost();
            socket = new DatagramSocket();
            
            //Envio do PDU
            data = p.PDUtoByteArray();
            DatagramPacket packet = new DatagramPacket (data, data.length, host,port);
            socket.send(packet);
            
            //Resposta
            DatagramPacket packet2 = new DatagramPacket (new byte[PACKETSIZE], PACKETSIZE ) ;
            socket.receive( packet2 ) ;
            resp = packet2.getData();
            PDU r = new PDU (resp);
            
            //Registo com sucesso
            if (r.getNumeroCampos()==0)
                System.out.println ("Foi registado com sucesso!");
            //Erro
            if (r.getNumeroCampos()==1)
            {
                ArrayList<Campo> c = r.getCampos();
                CampoString erro = (CampoString)c.get(0);
                System.out.println ("ERRO: "+erro.getValor());
            }
        } catch (IOException e) {System.out.println (e.getMessage());}
    }
    
    /** ANSWER (Tipo 11)
     * Pedido utilizado para responder a questões, dado o seu número e escolha da resposta.
     * @param numQ Número da questão
     * @param esc Escolha do utilizador (1, 2 ou 3)
     */
    public static void answer (int numQ, int esc) {
        PDU p;
        int tamanho = 0, r, score;
        CampoString desafio;
        CampoByte numeroQuestao, escolha, solucao, pontos;
        ArrayList<Campo> campos = new ArrayList<>();
        DatagramPacket packet;
        byte[] data;
        
        //Construção do PDU
        desafio = new CampoString ((byte)7,(byte)nomeDesafio.length(),nomeDesafio);
        numeroQuestao = new CampoByte ((byte)10, (byte)1, (byte)numQ);
        escolha = new CampoByte ((byte)6,(byte)1, (byte)esc);
        campos.add(desafio);campos.add(numeroQuestao);campos.add(escolha);
        tamanho = 8+nomeDesafio.length();
        p = new PDU ((short)0, (byte)11,(byte)3,(short)tamanho,campos);
        data = p.PDUtoByteArray();
        
        try {
            //InetAddress host = InetAddress.getLocalHost();
            data = p.PDUtoByteArray();
            
            //Envio do PDU
            packet = new DatagramPacket (data, data.length, host,port);
            socket.send(packet);
            
            //Resposta do servidor
            packet = new DatagramPacket (new byte[PACKETSIZE], PACKETSIZE);
            socket.receive(packet);
            data = packet.getData();
            p = new PDU(data);
            
            campos = p.getCampos();
            solucao = (CampoByte) campos.get(2);
            pontos = (CampoByte) campos.get(3);
            r = (solucao.getValor() & 0xff);
            score = (int)(pontos.getValor());
            
            //Resposta errada
            if (r==0) System.out.println ("Resposta errada! A tua pontuação no desafio é: "+score);
            //Resposta correcta
            else if (r==1) System.out.println ("Resposta correcta! A tua pontuação no desafio é: "+score);
        }
        catch (IOException e) {System.out.println (e.getMessage());}
    }
    
    public static void listRanking() {
        PDU p, resp;
        byte[] data;
        DatagramPacket packet;
        CampoString cs;
        
        try {
            //Criação do PDU
            p = new PDU((short)0,(byte)13, (byte)0, (short)0);
            data = p.PDUtoByteArray();
            
            //Envio do PDU
            packet = new DatagramPacket (data, data.length, host, port);
            socket.send(packet);
            
            //Resposta do servidor
            packet = new DatagramPacket (new byte[PACKETSIZE], PACKETSIZE);
            socket.receive(packet);
            resp = new PDU (packet.getData());
            ArrayList<Campo> ca = resp.getCampos();
            int i=1;
            for (Campo c: ca) {
                if (c.getTipoCampo() == 2){
                    cs = (CampoString) c;
                    System.out.print (i+" - Alcunha: "+cs.getValor());
                }
                if (c.getTipoCampo() == 20) {
                    CampoShort csh = (CampoShort)c;
                    System.out.println (" | Pontuação : "+csh.getValor());
                }
                i++;
            }
                
        }
        catch (IOException e ) {System.out.println(e.getMessage());}
    }
    
    
    
    
    
    /**
     *  Método auxiliar utilizado para receber as perguntas, imagens e músicas dum desafio.
     */
    public static void play () {
        int escolha,i;
        //Assumindo que haverá sempre 10 perguntas.
        for (i=1;i<=10;i++){
            escolha = pergunta(); // Método que exibe a janela com a imagem, pergunta e resposta e toca o trecho da música.
            answer(i,escolha);
        }
    }
    
    
    
    
    
    /**Método auxiliar que recebe as perguntas e resposta, seguida da imagem e trecho de música.
     * No fim, disponibilza estes elementos numa janela e toca o trecho de música. 
     * O utilizador responde à pergunta na janela e é guardado esse valor.
     * @return Resposta escolhida pelo utilizador
     */
    public static int pergunta () {
        byte[] buffer,imagem, musica;
        String pergunta="", r1="",r2="",r3="";   
        BufferedImage img;
        CampoString aux;
        PDU p;
        String[] resposta = new String[1];
        int i, tipo, n=0, limiteAux;
        HashMap<Integer,PDU> blocos = new HashMap<>();
        DatagramPacket  packet, packetImg, packetMus;
        Inteiro limite;
        
            try {
                packet = new DatagramPacket (new byte[PACKETSIZE], PACKETSIZE);
                packetImg = new DatagramPacket (new byte[63000], 63000);
                
                //PDU com o nome do desafio, pergunta e respostas
                socket.receive(packet);
                buffer = packet.getData();
                p = new PDU(buffer);
                ArrayList<Campo> campos = p.getCampos();
                
                //Desafio
                CampoString de = (CampoString) campos.get(0);
                nomeDesafio = de.getValor();
                //pergunta
                aux = (CampoString) campos.get(2);
                pergunta = aux.getValor();
                //respostas
                aux = (CampoString) campos.get(4);
                r1 = aux.getValor();
                aux = (CampoString) campos.get(6);
                r2 = aux.getValor();
                aux = (CampoString) campos.get(8);
                r3 = aux.getValor();

                //imagem
                socket.receive(packetImg);
                System.out.println ("Recebi imagem!");
                buffer = packetImg.getData();
                p = new PDU(buffer);
                imagem = gUtils.mountImage(p);
                InputStream in = new ByteArrayInputStream(imagem);
                img = ImageIO.read(in);

                //música
                try{
                    System.out.println ("A receber música..");
                    limite = new Inteiro(50);
                    socket.setSoTimeout(500);
                    limiteAux = limite.getInteiro();
                    for (n=0,i=1;n<limiteAux;i++,n++)
                    {
                        //É recebido um bloco de música
                        packetMus = new DatagramPacket (new byte[63000], 63000);
                        socket.receive(packetMus);
                        //É invocado uma thread para agrupar os blocos todos.
                        Thread t = new Thread (new SongBuilder(blocos,packetMus,i));
                        t.start();
                        limiteAux = limite.getInteiro();
                        System.out.println ("i - "+i+" | Limite: "+limite.toString()+" | Número de PDUs: "+blocos.size()
                                + " | LimiteAux :"+limiteAux);
                    }
                }
                // Sabe-se que não vêm mais blocos quando for recebido timeout
                catch  (SocketTimeoutException g) {
                try {
                    System.out.println ("Recebi a música!");
                    socket.setSoTimeout(0);
                    musica = gUtils.mountSong (blocos);
                    File dir = new File (System.getProperty("user.dir"));
                    File temp = File.createTempFile("cache","mp3", dir);
                    temp.deleteOnExit();

                    //Tocar música
                    gUtils.writeBytesToFile (temp,musica);
                    MP3Player player = new MP3Player(temp);
                    Thread t = new Thread (player);
                    t.start();

                    //Mostrar janela
                    Janela j = new Janela();
                    j.display (pergunta,r1,r2,r3,img,resposta,lock);
                    System.out.println(pergunta);
                    System.out.println(r1);
                    System.out.println(r2);
                    System.out.println(r3);
                    while (j.isVisible())
                        {
                            synchronized(lock) {
                                try {lock.wait();}
                                catch (InterruptedException e) {System.out.println (e.getMessage());}
                            }
                        }
                        player.stopPlaying();
                        return (Integer.parseInt(resposta[0]));
                        
                } catch (IOException e) {System.out.println (e.getMessage());}
            }
            }catch (IOException e) {System.out.println (e.getMessage());}
            return -1;
    }
    
    
    
    
   
    
    public static void  carregaMenus () {
        String menuInicial[] = {
            "1 - HELLO",
            "2 - REGISTER",
            "3 - LOGIN",
            "4 - LOGOUT",
            "5 - QUIT",
            "6 - END",
            "7 - LIST CHALLENGES",
            "8 - MAKE CHALLENGE",
            "9 - ACCEPT CHALLENGE",
            "10 - REMOVE CHALLENGE",
            "11 - LIST RANKING",
            "12 - PLAY"
                
                
                
               
        };
        
        mInicial = new Menu(menuInicial);
    }
    
    private static void printAddressInfo(String name, InetAddress... hosts) {
        try {
		System.out.println("===== Printing Info for: '" + name + "' =====");
		for(InetAddress host : hosts) {			
			System.out.println("Host Name: " + host.getHostName());
			System.out.println("Canonical Host Name: " + host.getCanonicalHostName());
			System.out.println("Host Address: " + host.getHostAddress());
			System.out.print("Is Any Local: " + host.isAnyLocalAddress());
			System.out.print(" - Is Link Local: " + host.isLinkLocalAddress());
			System.out.print(" - Is Loopback: " + host.isLoopbackAddress());
			System.out.print(" - Is Multicast: " + host.isMulticastAddress());
			System.out.println(" - Is Site Local: " + host.isSiteLocalAddress());
			System.out.println("Is Reachable in 2 seconds: " + host.isReachable(2000));
		}
        }
        catch (IOException e ) {System.out.println(e.getMessage());}
	}
    

}
            
       