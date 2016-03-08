package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    /**
     * @param args the command line arguments
     */
    static List<User> users = new LinkedList<>();
    static List<Group> groups=new LinkedList<>();
    static public int nextServerSocket=11224;
    public static Map<String,clientThread> map=new HashMap<String,clientThread>();
    
    public static int userFind(String name)
    {
        for(int i=0;i<users.size();i++)
        {
            if(users.get(i).getUsername().equals(name)){
                return i;
            }
        }
        return -1;
    }
    
    
    public static void main(String[] args) {
        try {
            adminThread Admin = new adminThread();
            Admin.start();
            Group g=new Group("All",null);
            g.setSocket(Integer.toString(nextServerSocket));
            groupThread gthread = new groupThread(nextServerSocket,"All");
            gthread.start();
            g.setGthread(gthread);
            groups.add(g);
            nextServerSocket++;

            ServerSocket server = new ServerSocket(11223);
            while(true)
            {
                try
                {
                    Socket client = new Socket();
                    client=server.accept();
                    String ip=client.getRemoteSocketAddress().toString().replaceAll(":[0-9]+", "");
                    DataInputStream dis = new DataInputStream(client.getInputStream());
                    DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                    String name=dis.readUTF();
                    //String ip=dis.readUTF();
                   int index=userFind(name);
                    if(index!=-1)
                    {
                        if(users.get(index).getS()!=User.state.offline)
                        {
                            dos.writeUTF("failed");
                            client.close();
                        }
                        else{
                            users.get(index).makeOnline();
                        }
                    }
                    else{
                    User user = new User(name,ip);
                    users.add(user);
                    //TODO inform users
                    }
                    if(client.isConnected()){
                        dos.writeUTF("success&"+getListOfUsers(name));
                        clientThread thread = new clientThread(client,name);
                        thread.start();
                        map.put(name, thread);
                    }
                }
                catch(Exception ex)
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
           
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static synchronized void removeThread(String user) {
       map.remove(user);
    }
    
    public static String getListOfUsers(String userName){
        String text = "";
        for(User user : users){
            if (user.getUsername().equals(userName)) continue;
            text+=user.toString()+'&';
        }
        if (text.isEmpty()) return text;
        else return text.substring(0, text.length()-1);
    }
    
    public static String getListOfUsers(){
        String text = "";
        for(User user : users){
            text += user.toString()+'&';
        }
        if (text.isEmpty()) return text;
        else return text.substring(0, text.length()-1);
    }
    
}
