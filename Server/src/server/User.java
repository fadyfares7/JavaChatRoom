package server;

public class User {
    private String username;
     private String ip;
     enum state{offline,online,blocked};
     private state s;

    public state getS() {
        return s;
    }

    public User(String username, String ip) {
        this.username = username;
        this.ip = ip;
        s=state.online;
    }
     
     

    public String getUsername() {
        return username;
    }

  

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
     public void makeOnline()
     {
         s=state.online;
     }
     public void makeOffline()
     {
         s=state.offline;
     }
     public void block(){
         s=state.blocked;
     }
     
     public void unblock() {
         s = state.offline;
     }
     
     @Override
     public String toString(){
         return username+','+s;
     }
    
}
