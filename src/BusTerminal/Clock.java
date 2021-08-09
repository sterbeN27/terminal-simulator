package BusTerminal;

/**
 *
 * @author Steven-
 */
public class Clock extends Thread{
    private CustomerGenerator cg;
    private Terminal t;
    
    public Clock (CustomerGenerator cg, Terminal t){
        this.cg = cg;
        this.t = t;
    }
    
    public void run(){
        try{
            // Timer for operation hours
            Thread.sleep(25000);
            NotifyClosed();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
    
    public synchronized void NotifyClosed(){
        System.out.println(t.getTime()+": Clock: ================ TIME TO CLOSE! ================");
        cg.setTerminalClosed();
        t.setTerminalClosed();
    }
}
