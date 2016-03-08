package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class clientThread extends Thread{
    private Socket socket;
    private String username;
    private Queue<String> msgq ;

    public clientThread(Socket client,String name) {
        this.socket=client;
        this.username=name;
        msgq= new LinkedList<String>();
    }

    @Override
    public void run() {
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            super.run();
            notifyState();
            dis = new DataInputStream(socket.getInputStream());
            dos= new DataOutputStream(socket.getOutputStream());
            int i=0;
            while(!socket.isClosed())
            {
                if(dis.available()!=0){
                    String input = dis.readUTF();
                    String[] tokens=input.split("&");
                    switch(tokens[0])
                    {
                        case "ip": 
                            String ip=sendIP(tokens[1]);
                            dos.writeUTF(ip);
                            break;
                        case "group":
                            if(!checkGroup(tokens[1])){
                                
                                Group g=new Group(tokens[1],username);
                                g.addMember(username);
                                for(int j=2;j<tokens.length;j++)
                                {
                                    g.addMember(tokens[j]);
                                }
                                groupThread gthread = new groupThread(Server.nextServerSocket,tokens[1]);
                                gthread.start();
                                g.setGthread(gthread);
                                Server.groups.add(g);
                                dos.writeUTF("success&"+Server.nextServerSocket);
                                g.setSocket(Integer.toString(Server.nextServerSocket));
                                Server.nextServerSocket++;
                                notifyGroup(g);
                                chatRoom.newListNotify(tokens[1]);
                            }
                            else
                            {
                                dos.writeUTF("failed");
                            }
                            break;
                        case "addUser":
                            Group g = findGroup(tokens[1]);
                            
                            for(int j=2;j<tokens.length;j++)
                                {
                                    g.addMember(tokens[j]);
                                    notifyGroupmember(g, tokens[j]);
                                }     
                            chatRoom.newListNotify(tokens[1]);
                            break;                            
                        case "removeUser":
                            for(int j=2;j<tokens.length;j++)
                            {
                        	removeUser(tokens[1], tokens[j]);
                            }
                            chatRoom.newListNotify(tokens[1]);
                            break;
                        case "public":
                            Group group = Server.groups.get(0);
                            dos.writeUTF("success&"+group.getName()+'&'+group.getSocket());
                            break;
                    }
                }
               if(!msgq.isEmpty()) 
               {
                   dos.writeUTF(msgq.remove());
               }
               i++;
               if(i%1000000==0){
                   i=0;
                   dos.writeUTF("HB");
               }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(clientThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dis.close();
            } catch (IOException ex) {
                Logger.getLogger(clientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Server.removeThread(this.username);
        User u = Server.users.get(Server.userFind(username));
        if(u.getS().equals(User.state.online))u.makeOffline();
        notifyState();
        
        
        
        
    }
    
    public synchronized void addMsg(String msg)
    {
        msgq.add(msg);
    }
    
    public void notifyState(){ 
            String msg = "list&"+Server.getListOfUsers();
       for(Map.Entry<String,clientThread> entry:Server.map.entrySet()){
            if(entry.getValue()==this)continue;
            //String msg = "notify&"+status+"&"+username;
            try {
                entry.getValue().addMsg(msg);
            } catch (Exception e) {
            }
       }
    }
    
    public String sendIP(String name)
    {
        for(User user : Server.users){
            if(user.getUsername().equals(name))
                return "success&"+name+'&'+user.getIp();
        }
        return "failed";
    }
    
    public boolean checkGroup(String name){
        for(Group group:Server.groups){
            if(group.getName().equals(name))
                return true;
        }
        return false;
    }
    
    public void notifyGroup(Group g){
        for(String user:g.members){
            if (user.equals(g.creatorName)) continue;
            Server.map.get(user).addMsg("group&"+g.getName()+"&"+g.getSocket());
        }
    }

    public void notifyGroupmember(Group g, String userName){
        
            Server.map.get(userName).addMsg("group&" + g.getName() + "&" + g.getSocket());       
    }

    
    private Group findGroup(String gname) {
        for(Group g: Server.groups)
        {
            if(g.getName().equals(gname))
            {
                return g;
            }
        }        
        return null;
    }
    
    public static int findGroupIndex(String gname) {
        for(int i=0; i<Server.groups.size(); i++)
        {
            if(Server.groups.get(i).getName().equals(gname))
               return i;               
        }
        return -1;
    }

    private void removeUser(String gname, String uname) {
        int gindex = findGroupIndex(gname);
        if(gindex<0) return;        
        Group group = Server.groups.get(gindex);
        int memIndex = group.members.indexOf(uname);
        if(memIndex<0) return;
        group.members.remove(memIndex);
        groupThread gthread= group.getGthread();
        
        for(Map.Entry<Socket, String> entry: gthread.map.entrySet())
        {
            if(entry.getValue().equals(uname))
            {
                gthread.map.remove(entry.getKey());
                break;
            }        
        }   
        Server.map.get(uname).addMsg("exitGroup&" + gname);
    }
    public static void removeUser(String gname, String uname, Socket socket) {
        int gindex = findGroupIndex(gname);
        if(gindex<0) return;   
        Group group = Server.groups.get(gindex);
        group.members.remove(group.members.indexOf(uname));
        groupThread gthread= group.getGthread();
        gthread.map.remove(socket);
    }
}
