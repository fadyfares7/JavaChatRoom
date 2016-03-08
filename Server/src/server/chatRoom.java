package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

public class chatRoom extends Thread {
    
    groupThread parentThread;
    
    chatRoom(groupThread aThis) {
        parentThread=aThis;
    }

    @Override
    public void run() {
        super.run(); 
        while(parentThread.name.equals("All")||(!parentThread.map.isEmpty()||parentThread.map.isEmpty()&&!parentThread.init)){
            for(Map.Entry<Socket,String> entry: parentThread.map.entrySet()){
                try {
                    if(!entry.getKey().isClosed()){
                        DataInputStream dis = new DataInputStream(entry.getKey().getInputStream());
                        if(dis.available()!=0){
                            String message = dis.readUTF();
                            if(message.equals("Bye")){
                                String userName = entry.getValue();
                                clientThread.removeUser(parentThread.name, userName, entry.getKey());
                                message = "notify&"+userName+" has left the group.";
                            }
                            for(Map.Entry<Socket,String> entry2: parentThread.map.entrySet()){
                                if(entry2.getValue().equals(entry.getValue())) continue;
                                try
                                {
                                    DataOutputStream dos = new DataOutputStream(entry2.getKey().getOutputStream());
                                    //dos.writeUTF("msg&"+entry.getValue()+"&"+message);
                                    dos.writeUTF(message);
                                }
                                catch (IOException ex) {
                                    Logger.getLogger(chatRoom.class.getName()).log(Level.SEVERE, null, ex);
                                    String userName = entry2.getValue();
                                    clientThread.removeUser(parentThread.name, userName, entry2.getKey());
                                    for(Map.Entry<Socket,String> entry3: parentThread.map.entrySet()){
                                            DataOutputStream dos = new DataOutputStream(entry3.getKey().getOutputStream());
                                            dos.writeUTF("notify&"+userName+" has left the group.");
                                    }
                                }
                            }
                        }
                    }
                    else {
                        String userName = entry.getValue();
                        clientThread.removeUser(parentThread.name, userName, entry.getKey());
                        for(Map.Entry<Socket,String> entry2: parentThread.map.entrySet()){
                                DataOutputStream dos = new DataOutputStream(entry2.getKey().getOutputStream());
                                dos.writeUTF("notify&"+userName+" has left the group.");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(chatRoom.class.getName()).log(Level.SEVERE, null, ex);
                    String userName = entry.getValue();
                        clientThread.removeUser(parentThread.name, userName, entry.getKey());
                        for(Map.Entry<Socket,String> entry2: parentThread.map.entrySet()){
                            try {
                                DataOutputStream dos = new DataOutputStream(entry2.getKey().getOutputStream());
                                dos.writeUTF("notify&"+userName+" has left the group.");
                            } catch (IOException ex1) {
                                Logger.getLogger(chatRoom.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        }
                }
                
            }
        }
        try {
            parentThread.ssocket.close();
        } catch (IOException ex) {
            Logger.getLogger(chatRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void newListNotify(String gname)
    {
        List<String> gmembers = new LinkedList<>();
        int index = clientThread.findGroupIndex(gname);
        if(index <0) return;
        gmembers = Server.groups.get(index).members;
        
        String msg = "notifyGroup&" + gname;
        for(String mem: gmembers)
        {
            msg += "&" + mem;       
        }
        
        for(String mem: gmembers)
        {
            Server.map.get(mem).addMsg(msg);
        }
        
    }
    
 }

