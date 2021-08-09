package BusTerminal;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Steven-
 */
class CustomerGenerator extends Thread {
    Terminal terminal;
    public boolean terminalClosed = false;
    int custId = 1;
    
    public CustomerGenerator (Terminal terminal){
        this.terminal = terminal;
    }

    @Override
    public void run() {
        // ======== Run using number of customers ========
        // SET CUSTOMERS LIMIT
        while (custId <= 150){
            Customer customer = new Customer(terminal);
            Thread thCustomer = new Thread(customer);
            customer.setName("Customer-"+custId);
            thCustomer.start();
            try {
                thCustomer.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            custId++;
            
            // set delay time customer coming
            try{
                TimeUnit.SECONDS.sleep((long)(1+(Math.random()*4)));
            }catch (InterruptedException iex){
                iex.printStackTrace();
            }
        }
        setTerminalClosed();
        terminal.setTerminalClosed();
        if (terminalClosed){
            try{
                TimeUnit.SECONDS.sleep(2);
            } catch(InterruptedException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        // ===============================================
        
        // Uncomment code below to run the program in a specific duration
        // ========= Run in a specific duration ==========
//        while (!terminalClosed){
//            Customer customer = new Customer(terminal);
//            Thread thCustomer = new Thread(customer);
//            customer.setName("Customer-"+custId);
//            thCustomer.start();
//            try {
//                thCustomer.join();
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//            custId++;
//            
//            // set delay time customer coming
//            try{
//                TimeUnit.SECONDS.sleep((long)(1+(Math.random()*4)));
//            }catch (InterruptedException iex){
//                iex.printStackTrace();
//            }
//        }
//        if (terminalClosed){
//            try{
//                TimeUnit.SECONDS.sleep(2);
//            } catch(InterruptedException e){
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return;
//        }
        // ===============================================
    }
    
    public synchronized void setTerminalClosed(){
        terminalClosed = true;
        System.out.println(terminal.getTime()+": CustomerGenerator: Stop generating new customer.");
    }
    
}
