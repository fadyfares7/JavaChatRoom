import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupMessage extends Thread {

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    
    private Socket socket;
    private String IP = "";
    private Queue<String> msgQ;

    public ChatWindow getCh() {
        return ch;
    }

    public void setCh(ChatWindow ch) {
        this.ch = ch;
    }
    private ChatWindow ch;
    private String ChatId = "";
    private DataOutputStream dos;
    private DataInputStream dis;
    private int groupFlag;
    private ArrayList<String> SSelected;
    private String CreatorName;

    public String getCreatorName() {
        return CreatorName;
    }

    public String getChatId() {
        return ChatId;
    }

    public void setChatId(String ChatId) {
        this.ChatId = ChatId;
    }

    public ArrayList<String> getSSelected() {
        return SSelected;
    }

    public void setSSelected(ArrayList<String> SSelected) {
        this.SSelected = SSelected;
        CreatorName = SSelected.get(0);
    }
    public GroupMessage(Socket socket,int Groupflag) {
        this.socket = socket;
         msgQ = new LinkedList<String>();
         groupFlag=Groupflag;
    }
    
    public GroupMessage(String ip,int portN,String grpN,int Groupflag) throws IOException
    {
        IP=ip;
        msgQ = new LinkedList<String>();
        socket = new Socket(IP,portN);
        ChatId=grpN;
        groupFlag=Groupflag;

    }
    public GroupMessage(String ip,int portN,String grpN,int Groupflag , ArrayList<String> array) throws IOException
    {
        IP=ip;
        msgQ = new LinkedList<String>();
        socket = new Socket(IP,portN);
        ChatId=grpN;
        groupFlag=Groupflag;
        SSelected = array;
        CreatorName = array.get(0);
    }
    
   synchronized public void SendData(String data)
    {
        
        msgQ.add(data);
    }

    public void run() {
         try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        
        if(groupFlag==4)//reciver p2p
        {
            ChatId=dis.readUTF();
        }
        else if(groupFlag==0||groupFlag==1||groupFlag==2||groupFlag==3)//sender p2p
        {
            
         dos.writeUTF(Main.username);

        }
        
        System.out.println("Step2-dos.writeUTF(Main.username)(groupmessage)  " + groupFlag + "  " + ChatId);

        } catch (IOException ex) {
            Logger.getLogger(GroupMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            new Thread(
            new Runnable() {
                public void run() {
                        ch=new ChatWindow(ChatId,GroupMessage.this,groupFlag);
                        ch.setVisible(true);
                        System.out.println("Step3-chatwindow(groupmessage)");

                }
            }
        ).start();
     
         
       
        
        
        while(!socket.isClosed())
             {
                 try{
                 if(dis.available() != 0)
                 {
                     
                     String recString = dis.readUTF();
                     System.out.println("DIS AVAILABLE IN GROUP MESSAGE  " + recString);
                     
                     String[] rec = recString.split("&");
                     if (groupFlag == 1 || groupFlag == 2 || groupFlag == 3)
                     {
                         
                         switch(rec[0])
                         {
                             case"msg":
                                 ch.displayText(recString);
                                 break;
                             
                             case"notify":
                                 try{
                                     if(ch==null) break;
                                    ch.justDisplayOnScreen(rec[1]);
                                 }
                                 catch(Exception ex){
                                     Logger.getLogger(GroupMessage.class.getName()).log(Level.SEVERE, null,ex);
                                 }
                                 break;
                                 
                             case"HB":break;
                         }
                     }
                     else
                     {
                      ch.displayText(recString);
                     }
                }
                 if(!msgQ.isEmpty())
                 {
                     String msg = msgQ.poll();
                     dos.writeUTF(msg);
                     if(msg.equals("bye")) socket.close();
                 }
             
          } catch (IOException ex) {
            Logger.getLogger(GroupMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        }

    
            
    
    }
    
    

