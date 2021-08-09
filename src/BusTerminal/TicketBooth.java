package BusTerminal;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Steven-
 */
public class TicketBooth extends Thread {
    Terminal terminal;
    String name;
    List<Customer> listBoothQueue;
    boolean toilet = false;
    private boolean done = false;
    private boolean terminalClosed = false;
    
    public TicketBooth(String name, Terminal terminal){
        this.terminal = terminal;
        this.name = name;
        listBoothQueue = new LinkedList<Customer>();
    }
    
    @Override
    public void run() {
        System.out.println(terminal.getTime()+": TicketBooth-"+name+": Started.");
        while(!terminalClosed){
            // Staff go to toilet
            if (toilet){
                goToilet();
            }
            // Empty queue
            if (listBoothQueue.size() == 0 && !terminalClosed){
                System.out.println(terminal.getTime()+": TicketBooth-"+name+": Waiting for a customer...");
                synchronized (this){
                    try{
                        this.wait();
                    } catch(InterruptedException iex){
                        iex.printStackTrace();
                    }    
                } 
            }
            // Processing
            while (listBoothQueue.size()>0 && !terminalClosed && !toilet){
                serving();
                // Generate random probability of staff go to toilet
                int randomInt = (int)Math.floor(Math.random()*10);
                if (randomInt == 5)
                    toilet = true;
            }    
        }
        
        if (terminalClosed){
            // Serving all the remaining customers if customers in terminal is not empty yet
            while (listBoothQueue.size()!=0 || !done){
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    // To do auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println(terminal.getTime()+": TicketBooth-"+name+": "+ listBoothQueue.size()+" customer(s) in the queue. Waiting for customers...");
                serving();
            }
            System.out.println(terminal.getTime()+": TicketBooth-"+name+": no more customers, booth CLOSED!");
            // Report to terminal the TicketBooth operations has finished
            terminal.countThreadFinished();
            terminal.checkDone();
            return;
        }   
    }
    
    private void goToilet(){
        System.out.println(terminal.getTime()+": TicketBooth-"+name+": ### Staff want to go to toilet! ###");
        System.out.println(terminal.getTime()+": TicketBooth-"+name+": ### Stop getting new customers. ###");
        // if there are customers left
        if (listBoothQueue.size()>0){
            System.out.println(terminal.getTime()+": TicketBooth-"+name+": Staff will serve "+listBoothQueue.size()+" customer(s) left.");
            while (listBoothQueue.size()>0)
                serving();
            }
        System.out.println(terminal.getTime()+": TicketBooth-"+name+": Staff went to toilet, close booth for a while...");
        try {
            TimeUnit.SECONDS.sleep((long)(Math.random()*4));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println(terminal.getTime()+": TicketBooth-"+name+": ### Staff came back, continuing the service... ###");
        toilet = false;
    }
    
    private void serving(){
        synchronized (listBoothQueue){
            Customer customer;
            if(listBoothQueue.size()!=0){
                customer = (Customer)((LinkedList<?>)listBoothQueue).poll();
                System.out.println(terminal.getTime()+": TicketBooth-"+name+": Serving "+customer.getName());    
                // processing ticket
                try {
                    // DURATION FOR SERVING A CUSTOMER
                    Thread.sleep(8000);
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                }
                // randomly set ticket destination
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
        synchronized (listBoothQueue){
            // insert a customer (last position)
            ((LinkedList<Customer>)listBoothQueue).offer(customer);
            System.out.println(terminal.getTime()+": TicketBooth-" + name + ": "+customer.getName()+" queueing");    
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
