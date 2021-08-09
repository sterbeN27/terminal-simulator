package BusTerminal;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Steven-
 */
public class TerminalStaff extends Thread {
    Terminal terminal;
    private boolean terminalClosed = false;
    private boolean machineBroken = false;
    List<Customer> shiftCustomer;
    
    public TerminalStaff(Terminal terminal){
        this.terminal = terminal;
        shiftCustomer = new LinkedList<>();
    }
    
    @Override
    public void run(){
        while(!terminalClosed){
            // Will wait if no new customer, and notified when there is new customer
            if (terminal.listCustomer.size()==0){
                System.out.println(terminal.getTime()+": TerminalStaff: Waiting for a new customer.");
                synchronized(this){
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (!terminalClosed)
                        System.out.println(terminal.getTime()+": TerminalStaff: A new customer found.");
                }    
            }
            ticketing();
            if (machineBroken){
                shiftCustomer();
            }
        }
        if(terminalClosed){
            while(terminal.listCustomer.size()>0){
                System.out.println(terminal.getTime()+": TerminalStaff: "+terminal.listCustomer.size()+" customer(s) left.");
                ticketing();
                if (machineBroken){
                    shiftCustomer();
                }
            }
            System.out.println(terminal.getTime()+": TerminalStaff: Works done, time to go home!");
        }
        // notify all the ticketing operations to stop operating
        terminal.B1.setDone();
        terminal.B2.setDone();
        terminal.M1.setDone();
        return;
    }
    
    private void ticketing(){
        boolean added = false;
        Customer customer;
        synchronized (terminal.listCustomer){
            if (terminal.listCustomer.size() != 0)
                customer = (Customer)((LinkedList<?>)terminal.listCustomer).poll();
            else
                return;
        }
        // Assigning customer to one of the ticketing queues
        int randomInt = (int)Math.floor(Math.random()*3);
        while (!added){
            if (randomInt == 0){
                // pass to next queue if this booth is full or staff go to toilet
                if (terminal.B1.listBoothQueue.size() == 5 || terminal.B1.toilet)
                    randomInt = 1;
                // serving the customer
                else if (!terminal.B1.toilet){
                    System.out.println(terminal.getTime()+": TerminalStaff: Adding "+customer.getName() + " to TicketBooth-B1 queue.");
                    terminal.B1.add(customer);
                    added = true;
                }
            } else if (randomInt == 1){
                if (terminal.B2.listBoothQueue.size() == 5 || terminal.B2.toilet)
                    randomInt = 2;
                else{
                    System.out.println(terminal.getTime()+": TerminalStaff: Adding "+customer.getName() + " to TicketBooth-B2 queue.");
                    terminal.B2.add(customer);
                    added = true;
                }
            }else if (randomInt == 2){
                if (terminal.M1.listMachineQueue.size() == 5 || terminal.M1.broken)
                    randomInt = 0;
                else if (!terminal.M1.broken){
                    System.out.println(terminal.getTime()+": TerminalStaff: Adding "+customer.getName() + " to TicketMachine-M1 queue.");
                    terminal.M1.add(customer);
                    added = true;
                }
            }     
        }
    }
    // move customers to one of the ticket booths if ticket machine is out of order
    private void shiftCustomer(){
        Customer customer;
        while(shiftCustomer.size()>0){
            synchronized(shiftCustomer){
                customer = (Customer)((LinkedList<?>)shiftCustomer).poll();
            }
            if (terminal.B1.listBoothQueue.size()<5){
                synchronized(terminal.B1.listBoothQueue){
                    ((LinkedList<Customer>)terminal.B1.listBoothQueue).offer(customer);
                }
                System.out.println(terminal.getTime()+": TerminalStaff: "+customer.getName()+" moved to TicketBooth-B1.");
            } else if (terminal.B2.listBoothQueue.size()<5){
                synchronized(terminal.B2.listBoothQueue){
                    ((LinkedList<Customer>)terminal.B2.listBoothQueue).offer(customer);
                }
                System.out.println(terminal.getTime()+": TerminalStaff: "+customer.getName()+" moved to TicketBooth-B2.");
            } else{
                synchronized(terminal.listCustomer){
                    ((LinkedList<Customer>)terminal.listCustomer).offer(customer);
                }
                System.out.println(terminal.getTime()+": TerminalStaff: "+customer.getName()+" moved to Terminal Foyer.");
            }
        }
    }
    
    public void setCustomerAvailability(){
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
    
    public void setMachineStatus(){
        machineBroken = true;
    }
 
}
