package BusTerminal;

/**
 *
 * @author Steven-
 */
public class Main {
    
    public static void main(String[] args) {
        Terminal t = new Terminal();
        CustomerGenerator cg = new CustomerGenerator (t);
        
        cg.start();
        
        // Uncomment code below to run the program in a specific duration &
        // change the CustomerGenerator.java
        // Clock clk = new Clock (cg,t);
        // clk.start();
    }
}
