package superuser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class SuperUser {

    public static Queue<String> msgq ;
    public static Socket socket;
    public static DataOutputStream dos;
    public static DataInputStream dis;
    
    public static void main(String[] args) {          
          new SuperUserGUI();        
        try {
            socket = new Socket("127.0.0.1",11222);           
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            msgq = new LinkedList<String>();
            msgq.add("list");
            String input;
            while(true)
            {
                if(!msgq.isEmpty())
                {
                    String msg = msgq.remove();
                    System.out.println(msg);
                    dos.writeUTF(msg);
                }
                
                if(dis.available()!=0)
                {
                    input = dis.readUTF();
                    String[] token = input.split("&");
                    switch(token[0])
                    {
                        case"HB":
                            break;
                        default:
                            String [] usersList = input.split("&");
                            SuperUserGUI.bannedList = new ArrayList<String>();
                            SuperUserGUI.notbannedList = new ArrayList<String>();
                            for(int i = 0; i < usersList.length; i++)
                            {
                                System.out.println(usersList[i]);
                                String [] user = usersList[i].split(",");
                                if(user.length < 2) break;
                                if(user[1].equals("blocked")) 
                                {
                                    SuperUserGUI.bannedList.add(user[0]);
                                }
                                else
                                {   
                                    SuperUserGUI.notbannedList.add(user[0]);
                                }
                            }
                            SuperUserGUI.UpdateList();
                            break;                    
                    }
                }
                         
            }
        } catch (IOException ex) {
            Logger.getLogger(SuperUser.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }      
    
    public static void closeSocket()
    {
        try {
        dos.close();
        dis.close();       
        socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SuperUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
