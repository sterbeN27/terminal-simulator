package BusTerminal;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Steven-
 */
public class TicketMachine extends Thread{
    Terminal terminal;
    String name;
    List<Customer> listMachineQueue;
    boolean broken = false;
    private boolean done = false;
    private boolean terminalClosed = false;
    
    public TicketMachine (String name, Terminal terminal){
        this.terminal = terminal;
        this.name = name;
        listMachineQueue = new LinkedList<Customer>();
    }
    
    @Override
    public void run() {
        System.out.println(terminal.getTime()+": TicketMachine-"+name+": Started.");
        while(!terminalClosed){
            // Machine broken
            if (broken){
                System.out.println(terminal.getTime()+": TicketMachine-"+name+": ### Sorry! Machine is out of order! ###");
                moveCustomer();
                // Report to terminal, ticket machine has stopped operating
                terminal.countThreadFinished();
                terminal.checkDone();
                return;
            }
            // Empty queue
            if (listMachineQueue.size() == 0 && !terminalClosed && !broken){
                System.out.println(terminal.getTime()+": TicketMachine-"+name+": Waiting for a customer...");
                synchronized (this){
                    try{
                        this.wait();
                    } catch(InterruptedException iex){
                        iex.printStackTrace();
                    }    
                } 
            }
            // Processing
            while(!terminalClosed && !broken && listMachineQueue.size()>0){
                serving();
                // Generate random probability of the machine will broke
                int randomInt = (int)Math.floor(Math.random()*10);
                if (randomInt == 5)
                    broken = true;
            }
        }
        
        if (terminalClosed){
            // Serving all the remaining customers
            while(!done || listMachineQueue.size()!=0 && !broken){
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    // To do auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println(terminal.getTime()+": TicketMachine-"+name+": "+ listMachineQueue.size()+" customer(s) left. Serving next customer..."); 
                serving();        
            }
            System.out.println(terminal.getTime()+": TicketMachine-"+name+": Job done, shut down by staff.");  
            terminal.countThreadFinished();
            terminal.checkDone();
            return;
        }   
    }
    
    private void moveCustomer(){
        Customer customer;
        while(listMachineQueue.size()>0){
            customer = (Customer)((LinkedList<?>)listMachineQueue).poll();
            synchronized (terminal.staff.shiftCustomer){
                ((LinkedList<Customer>)terminal.staff.shiftCustomer).offer(customer);
            }
        }
        // notify the terminal staff, machine is out of order
        terminal.staff.setMachineStatus();
    }
    
    private void serving(){
        synchronized(listMachineQueue){
            Customer customer;
            if(listMachineQueue.size()!=0){
                customer = (Customer)((LinkedList<?>)listMachineQueue).poll();
                System.out.println(terminal.getTime()+": TicketMachine-"+name+": Serving "+customer.getName());
                // processing ticket
                try {
                    // DURATION FOR SERVING A CUSTOMER
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                int randomTicket = (int)Math.floor(1+(Math.random()*3));
                if (randomTicket == 0)
                    customer.setTicket(1);
                else if (randomTicket == 1)
                    customer.setTicket(2);   
                else
                    customer.setTicket(3);
                System.out.println(terminal.getTime()+": "+customer.getName()+": Purchased ticket to destination "+randomTicket);
                terminal.addPurchasedCustomer(customer, randomTicket);       
            }
            
        }
    }
    
    public void add(Customer customer){
        synchronized (listMachineQueue){
            // insert a customer (last position)
            ((LinkedList<Customer>)listMachineQueue).offer(customer);
            System.out.println(terminal.getTime()+": TicketMachine-" + name + ": "+customer.getName()+" Queueing");
        }
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
    
    public void setCustomerAvailability(){
        synchronized (this){
            notify(); 
        }
    }
    
    public synchronized void setDone(){
        done = true;
    }
}
