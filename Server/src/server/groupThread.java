package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class groupThread extends Thread{
    public int port;
    public Map<Socket,String> map;
    public String name;
    public ServerSocket ssocket;
    boolean init;

    public groupThread(int ssocket,String name) {
        this.port = ssocket;
        this.name=name;
        map = new ConcurrentHashMap<>();
        init = false;
    }

    
    
    @Override
    public void run() {
        super.run();
        try {
            ssocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(groupThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        chatRoom chat = new chatRoom(this);
        chat.start();
        while(!ssocket.isClosed()){
            try{
                Socket csocket = new Socket();
                csocket=ssocket.accept();
                DataInputStream dis = new DataInputStream(csocket.getInputStream());
                String name = dis.readUTF();
                for(Map.Entry<Socket,String> entry2: map.entrySet()){
                                DataOutputStream dos = new DataOutputStream(entry2.getKey().getOutputStream());
                                dos.writeUTF("notify&"+name+" has joined the group.");
                        }
                map.put(csocket, name);
                init = true;
                
                //TODO inform users of new arrivals
                
            }
            catch(Exception e){
                
            }
        }
        Server.groups.remove(clientThread.findGroupIndex(name));
    }
    
    
}
