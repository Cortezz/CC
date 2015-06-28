/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Work
 */
public class Inteiro {
    
    private int inteiro;
    private Lock lock = new ReentrantLock();
    
    public Inteiro (int inteiro) {this.inteiro = inteiro;}
    
    public int getInteiro () { return this.inteiro;}
    
    public void setInteiro (int inteiro) { 
        lock.lock();
        try {this.inteiro = inteiro;}
        finally {lock.unlock();}
    }
    
    public String toString () {
        return String.valueOf(inteiro);
    }
    
}
