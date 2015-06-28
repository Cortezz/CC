/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Work
 */
public class Menu {
    private List<String> opcoes;
    private int opc;
   
    public Menu(String[] opcoes) {
        this.opcoes = new ArrayList<>();
        for (String opt : opcoes) 
         this.opcoes.add(opt);
    
    }
    
    public int getOpc () { return this.opc;}
    
    public void mostraMenu () {
        
        for (String s: opcoes)
            System.out.println (s);
           
        System.out.println ("0 - Sair");
        System.out.println ("----------");
    }
    

    public void executa () {
        do {
            mostraMenu();
            this.opc = lerOpcao();
        } while (this.opc == -1);
    }
    
    public int lerOpcao () {
        String line=null; /*Alterei aqui*/
        int opt=-1; 
        Scanner is = new Scanner(System.in);
        
        while(opt==-1){
            System.out.print("Opção: ");
            try{
                line = is.nextLine();               
                opt=Integer.parseInt(line); // Se não for um número a excepção trata do erro
                if (opt < 0 || opt > this.opcoes.size()) {
                    System.out.println("Opção Inválida!!!");
                    opt = -1;
                }
            } catch(NumberFormatException e){
                System.out.println("Opção Inválida!!!");
                opt=-1;
            }
        }
        return opt;
    }
   

}
