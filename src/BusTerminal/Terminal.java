package BusTerminal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Steven-
 */
class Terminal {
    int maxCapacity = 50;
    int terminalCount = 0;
    int threadFinished = 0;
    List<Customer> listCustomer;
    public boolean terminalClosed = false;
    Turnstile west = new Turnstile("West", this);
    Turnstile east = new Turnstile("East", this);
    TicketBooth B1 = new TicketBooth("B1", this);
    TicketBooth B2 = new TicketBooth("B2", this);
    TicketMachine M1 = new TicketMachine ("M1", this);
    WaitingArea W1 = new WaitingArea("W1",this);
    WaitingArea W2 = new WaitingArea("W2",this);
    WaitingArea W3 = new WaitingArea("W3",this);
    TerminalStaff staff = new TerminalStaff(this);
    
    public Terminal(){
        listCustomer = new LinkedList<>();
        activateTerminalStaff();
        activateTurnstile();
        activateTicketing();
        activateWaitingArea();
    }
    
    public String getTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm:ss");  
        LocalDateTime now = LocalDateTime.now();  
        return dtf.format(now);  
    }
    // Increase number of customers inside terminal
    public synchronized void increment(){
        terminalCount++;
    }
     // Decrease number of customers inside terminal
    public synchronized void decrement(){
        terminalCount--;
    }
    
    public synchronized int getTerminalCount(){
        return terminalCount;
    }
    
    private void activateTerminalStaff(){
        staff.start();
    }
    
    private void activateTurnstile(){
        west.start();
        east.start();
    }
    
    private void activateTicketing(){
        B1.start();
        B2.start();
        M1.start();
    }

    private void activateWaitingArea(){
        W1.start();
        W2.start();
        W3.start();
    }
    // Assigning customer to one of the turnstiles
    public synchronized void setEntrance(Customer customer){
        int randomGate = (int)Math.floor(Math.random()*2);
        if (randomGate == 1){
            System.out.println(getTime()+": "+customer.getName() + ": Queueing at West Turnstile.");
            west.add(customer);
        } else if (randomGate == 0){
            System.out.println(getTime()+": "+customer.getName() + ": Queueing at East Turnstile.");
            east.add(customer);
        }
    }
    // Let customer to come into the terminal
    public synchronized void addCustomer(Customer customer){
        increment();
        synchronized (listCustomer){
            // Insert a customer into the terminal foyer (last position)
            ((LinkedList<Customer>)listCustomer).offer(customer);
            System.out.println(getTime()+": "+ customer.getName()+": Entering the terminal.");
        }
        if (listCustomer.size() == 1){
            M1.setCustomerAvailability();
            B1.setCustomerAvailability();
            B2.setCustomerAvailability();
            staff.setCustomerAvailability();
        }        
    }
    // Guide customer who has purchased ticket to the right waiting area
    public synchronized void addPurchasedCustomer(Customer customer, int destination){
        if(destination == 1){
            System.out.println(getTime()+": "+ customer.getName()+": Going to WaitingArea-W1");
            W1.add(customer);
        }else if(destination == 2){
            System.out.println(getTime()+": "+ customer.getName()+": Going to WaitingArea-W2");
            W2.add(customer);
        }else{
            System.out.println(getTime()+": "+ customer.getName()+": Going to WaitingArea-W3");
            W3.add(customer);
        }
    }
    // Check terminal capacity
    public synchronized void checkAvailability(){
        int check = getTerminalCount();
        if (check < 35){
            west.setTerminalAvailability();
            east.setTerminalAvailability();
        }
    }
    
    public synchronized void setTerminalClosed(){
        terminalClosed = true;
        System.out.println(getTime()+": Terminal: =================== TIME TO CLOSE ===================");
        west.setTerminalClosed();
        east.setTerminalClosed();
        B1.setTerminalClosed();
        B2.setTerminalClosed();
        M1.setTerminalClosed();
        W1.setTerminalClosed();
        W2.setTerminalClosed();
        W3.setTerminalClosed();
        staff.setTerminalClosed();
    }
    // Will increase once an operation has finished
    public synchronized void countThreadFinished(){
        threadFinished++;
    }
    // Last check on TICKETING operations for closing time
    public synchronized void checkDone(){
        int check = listCustomer.size()+B1.listBoothQueue.size()+B2.listBoothQueue.size()+
                M1.listMachineQueue.size()+staff.shiftCustomer.size();
        
        // check if all processes stop operating
        if (check == 0 && threadFinished == 3){
            W1.setDone();
            W2.setDone();
            W3.setDone();
            System.out.println(getTime()+": WaitingArea-W1: CLOSED.");
            System.out.println(getTime()+": WaitingArea-W2: CLOSED.");
            System.out.println(getTime()+": WaitingArea-W3: CLOSED.");
            System.out.println(getTime()+": Terminal: ========== We're TOTALLY CLOSED. Thank You. ==========");
        }
    }
}
