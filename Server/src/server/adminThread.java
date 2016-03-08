package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class adminThread extends Thread{

    public adminThread() {
    }

    @Override
    public void run() {
        super.run(); //To change body of generated methods, choose Tools | Templates.
        try {
            ServerSocket sSocket = new ServerSocket(11222);
            while(true)
            {
                Socket socket = sSocket.accept();
                int j=0;
                try 
                {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    while(!socket.isClosed())
                    {
                        ++j;
                        if(j%1000000==0){
                            j=0;
                            dos.writeUTF("HB");
                        }
                        if(dis.available()!=0)
                        {
                            String msg = dis.readUTF();
                            String [] tokens = msg.split("&");
                            switch (tokens[0]) {
                                case  "ban":
                                    for (int i=1; i<tokens.length; i++)
                                    {
                                        //change state
                                        int userIndex = Server.userFind(tokens[i]);
                                        if(userIndex<0) continue;
                                        User user = Server.users.get(userIndex);
                                        if(user.getS()== User.state.online)
                                        {
                                            Server.map.get(tokens[i]).addMsg("blocked");
                                        }
                                        user.block();                                                                              //
                                    }
                                    dos.writeUTF(Server.getListOfUsers());
                                    break;
                                case "unban":
                                    for (int i=1; i<tokens.length; i++)
                                    {
                                        //change state
                                        int userIndex = Server.userFind(tokens[i]);
                                        if(userIndex<0) continue;
                                        Server.users.get(userIndex).unblock();                                        
                                    }
                                    dos.writeUTF(Server.getListOfUsers());
                                    break;
                                case "list":
                                    dos.writeUTF(Server.getListOfUsers());          
                                    break;                            
                            }
                        }
                    }
                }
                catch (Exception e){
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(adminThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
}
