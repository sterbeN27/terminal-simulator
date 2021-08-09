package BusTerminal;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Steven-
 */
public class WaitingArea extends Thread{
    Terminal terminal;
    String name;
    List<Customer> listWaitingArea;
    private boolean terminalClosed = false;
    private boolean done = false;
    
    public WaitingArea(String name, Terminal terminal){
        this.terminal = terminal;
        this.name = name;
        listWaitingArea = new LinkedList<Customer>();
    }
    
    @Override
    public void run(){
        System.out.println(terminal.getTime()+": WaitingArea-"+name+": Ready.");
        while(!terminalClosed){
            synchronized(this){
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            terminal.checkAvailability();
        }
        
        if (terminalClosed){
            while(!done){
                synchronized(this){
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } 
                }
            }
            try{
                // Catch concurrent process, avoid missing process
                Thread.sleep(2000);
            } catch (InterruptedException e){
                // To do auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
    }
    
    public void add(Customer customer){
        synchronized (listWaitingArea){
            ((LinkedList<Customer>)listWaitingArea).offer(customer);
            System.out.println(terminal.getTime()+": WaitingArea-" + name + ": "+customer.getName()+" entered.");
        }
        terminal.decrement();
        System.out.println(terminal.getTime()+": WaitingArea-" + name + ": "+customer.getName()+" get on the bus and leave the terminal.");
        synchronized (this){
            notify();  
        }
    }
    
    public synchronized void setTerminalClosed(){
        terminalClosed = true;
        synchronized (this){
            notify(); 
        }
    }
    
    public synchronized void setDone(){
        done = true;
        // to make sure all processes are completely done by shutting down all 
        // waiting processes
        for (int i=0; i<3; i++){
            synchronized (this){
                notifyAll();  
            }
        }
    }
}
