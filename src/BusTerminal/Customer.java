package BusTerminal;
/**
 *
 * @author Steven-
 */
class Customer implements Runnable{
    Terminal terminal;
    String name;
    int ticket;
    
    public Customer(Terminal terminal){
        this.terminal = terminal;
    }
    
    public int getTicket(){
        return ticket;
    }
    
    public void setTicket(int ticket){
        this.ticket = ticket;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName (String name){
        this.name = name;
    }

    public void run() {
        goToTerminal();
    }
    
    private void goToTerminal(){
        terminal.setEntrance(this);
    }
}
