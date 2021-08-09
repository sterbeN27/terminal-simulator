package BusTerminal;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Steven-
 */
class Turnstile extends Thread {
    String name;
    Terminal terminal;
    private boolean terminalClosed = false;
    private boolean allow = true;
    private int maxCapacity = 50;
    List<Customer> listQueue;
    
    public Turnstile(String name, Terminal terminal){
        this.name = name;
        this.terminal = terminal;
        listQueue = new LinkedList<>();
    }
    
    @Override
    public void run(){
        while (!terminalClosed){
            operate();
        }
        if (terminalClosed){
            System.out.println(terminal.getTime()+": Turnstile-" + name + ": Stop receiving new customer.");
            terminal.checkDone();
            return;
        } 
    }

    private void operate(){
        Customer customer;
        int count = terminal.getTerminalCount();
        if (count == maxCapacity && !terminalClosed)
            allow = false;

        // available
        if (listQueue.size() > 0 && allow &&!terminalClosed){
            customer = (Customer)((LinkedList<?>)listQueue).poll();
            System.out.println(terminal.getTime()+": Turnstile-"+name+": Allowing "
                    +customer.getName()+" to come into the terminal.");
            terminal.addCustomer(customer);
            count = terminal.getTerminalCount();
        }
        // unavailable
        if (!allow && !terminalClosed){
            synchronized(this){
                System.out.println(terminal.getTime()+": Turnstile-" + name + 
                        ": *** Terminal full, prevent customer to come in. ***");
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (allow)
                    System.out.println(terminal.getTime()+": Turnstile-" + name + 
                        ": *** Terminal available, let customer to come in. ***");    
            }    
        }     
    }
    
    public void add(Customer customer){
        synchronized (listQueue){
            // insert a customer (last position)
            ((LinkedList<Customer>)listQueue).offer(customer);
            System.out.println(terminal.getTime()+": Turnstile-" + name + ": "+customer.getName()+" added to the queue.");
        }
    }
    
    public void setTerminalClosed(){
        terminalClosed = true;
        synchronized (this){
            notify();  
        }
    }
    
    public void setTerminalAvailability(){
        allow = true;
        synchronized (this){
            notify();  
        }
    }
}
