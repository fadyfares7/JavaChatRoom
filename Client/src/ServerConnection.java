import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ServerConnection extends Thread{
    private Socket client;
    private Queue<String> msgs = new LinkedList<String>();
     private DataOutputStream dos = null;
     private DataInputStream dis = null;
     private String Username;
     private OnlineList MyOnlineList;
     
    private GroupMessage someoneAddedMeToGroup;
    private ArrayList<GroupMessage> MyGroupsQList;

    private boolean flagFirstTime = true;
     
    public void CloseMySocket() throws IOException
    {
        dis.close();
        dos.close();
        client.close();
    }
    
    public ServerConnection(String ip,int portN) throws IOException {        
         client = new Socket(ip,portN);
         dos = new DataOutputStream(client.getOutputStream());
          dis = new DataInputStream(client.getInputStream());
        Username=Main.username;
        MyGroupsQList = new ArrayList<GroupMessage>();
    }
    public ServerConnection(Socket s) throws IOException {        
         client = s;
         dos = new DataOutputStream(s.getOutputStream());
          dis = new DataInputStream(s.getInputStream());
        Username=Main.username;
        MyGroupsQList = new ArrayList<GroupMessage>();
    }
    
    public synchronized void addMsg(String s) //send to server
    {
        msgs.add(s);
    }
//    public void sendUserData(String name) throws IOException
//    {
//        
//         
//                 //create online thread
//                java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new OnlineList();
//            }
//        });
//            }
//                   
//    }
    
    
    public void run() {
        try{
            String myName = Username;
            dos.writeUTF(myName);
            String recieve;
            recieve = dis.readUTF();
            System.out.println(recieve);
            String[] pop = recieve.split("&"); //u can replace it with OnPeople
            if(pop[0].matches("failed")) {//he is blocked or already logged in
                JOptionPane.showMessageDialog(null,"Failed!! you are Blocked OR Already Logged in");
                System.exit(0);
            }
            //ab3t esm el user w ast2bl "success& wl list" aw "failed"
            else if(pop[0].matches("success"))
            {
                Main.OnOnlyList = new ArrayList<String>();
                Main.OffOnlyList = new ArrayList<String>();
                Main.BlockedpplList = new ArrayList<String>();

                 //for first time no one joined the server except me
                if(pop.length == 1)
                {
                    Main.OnPeople = new String[0];
                    Main.OnOnly = new String[0];
                    Main.OffOnly = new String[0];
                    Main.Blockedppl = new String[0];
                } 
                
                else/*separate online from offline*/
                {
                    for(int i=1;i<pop.length;i++)
                    {
                    int index = pop[i].indexOf(",");
                    ++index;
                    if(pop[i].substring(index,pop[i].length()).matches("online"))
                    {
                        --index;
                        Main.OnOnlyList.add(pop[i].substring(0,index));
                    }
                    else if(pop[i].substring(index,pop[i].length()).matches("offline"))
                    {
                        --index;
                        Main.OffOnlyList.add(pop[i].substring(0,index));
                    }
                    else
                    {
                        --index;
                        Main.BlockedpplList.add(pop[i].substring(0,index));
                    }
                  }
                    Main.OnOnly=Main.OnOnlyList.toArray(new String[Main.OnOnlyList.size()]);
                    Main.OffOnly=Main.OffOnlyList.toArray(new String[Main.OffOnlyList.size()]);
                   // MyOnlineList.UpdateList();
                }
                new Thread(
            new Runnable() {
                public void run() {
                    
                   MyOnlineList=new OnlineList(ServerConnection.this);
                   MyOnlineList.setVisible(true);
                }
            }
        ).start();
//                 java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                
//            }
//        });
            }
        }
        catch(Exception e){
        }
             while(!client.isClosed())
             
             {
                  try{
                // 2 condition if
                //check queue feeh 7aga -> hb3at request w ast2bl raddo
                //lw 3ndy 7aga 22raha h2raha
                 //     System.out.println("THE SOCKET BETWEEN THE CLIENT AND THE SERVER IS STILL OPENNED");
                if(dis.available()!=0) // server 3ayz yklmny mn3'er ma aklmo
                {
                    
                   /*
                    switch case
                    h3od ashof fi 7aga h2raha walla l2
                    5-hast2bl handel heart beat("HB")

                    6- hast2bl en ana etdaft 3la group("notifyGroup&esm el group&por ely ha connect 3leh")

                    7-receive list of user 3nd ay change;kol ma wa7d d5al aw 5rag aw m3 el bdaya aw blocked
                        di internally w7da m3 el bdaya wl tanya 
                        el awl("success&list of users")
                      8-  ay 7aga b3d kda ("list&list of users")

                    9- receive blocked("blocked") -> h2fl el brnamg
                    */

                    String got = dis.readUTF();
                    //Main.OnPeople = got.split("&"); //or craete new array
                    String[] msgFromServer = got.split("&");
                    switch(msgFromServer[0])
                    {
                        case"HB": break;
                        
                        case"group":
                        {
                            System.out.println("WHAT I RECEIVED FROM THE SERVER  GROUP  (case group)" + got);
                            ArrayList<String> emptyList = new ArrayList<String>();
                            emptyList.add("empty");
                            someoneAddedMeToGroup = new GroupMessage(Main.MainServerIp, Integer.parseInt(msgFromServer[2]), msgFromServer[1], 2, emptyList);
                            someoneAddedMeToGroup.start();
                            MyGroupsQList.add(someoneAddedMeToGroup);
                            flagFirstTime = true;
                        }
                        break;
                        
                        case"notifyGroup":
                        {
                            
                            System.out.println("WHAT I RECEIVED FROM THE SERVER  GROUP  (case notifyGroup)" + got);
                    
                            ArrayList<String> temp = new ArrayList<String>();
                            //OnlineList.grpname = msgFromServer[1];
                            
                            for(int i=2 ; i<msgFromServer.length ;i++)
                            {
                                temp.add(msgFromServer[i]);
                                //OnlineList.selectedList.add(msgFromServer[i]);
                            }
                            System.out.println("GROUP NAME I SEARCHED FOR IN NOTIFY GROUP = " + getGroupMessage(msgFromServer[1]));
                            if (flagFirstTime)
                            {
                                getGroupMessage(msgFromServer[1]).setSSelected(temp);
                                flagFirstTime = false;

                            }
                            else
                                getGroupMessage(msgFromServer[1]).getCh().setGroupChatSelectedList(temp);
                          
                        }
                            break;
                        
                        case"list":
                        {
                            Main.OnOnlyList = new ArrayList<String>();
                            Main.OffOnlyList = new ArrayList<String>();
                            Main.BlockedpplList = new ArrayList<String>();
                            System.out.println(got);
                            /*separate online from offline*/
                            for(int i=1;i<msgFromServer.length;i++)
                           {
                            int index = msgFromServer[i].indexOf(",");
                            ++index;
                            if(msgFromServer[i].substring(index,msgFromServer[i].length()).matches("online"))
                            {
                                --index;
                                if(!msgFromServer[i].substring(0,index).matches(Main.username))
                                {
                                    Main.OnOnlyList.add(msgFromServer[i].substring(0,index));
                                }
                            }
                            else if(msgFromServer[i].substring(index,msgFromServer[i].length()).matches("offline"))
                            {
                                --index;
                                Main.OffOnlyList.add(msgFromServer[i].substring(0,index));
                            }
                            else
                            {
                                --index;
                                Main.BlockedpplList.add(msgFromServer[i].substring(0,index));
                            }
                            //update gui online list
                            Main.OnOnly=Main.OnOnlyList.toArray(new String[Main.OnOnlyList.size()]);
                            Main.OffOnly=Main.OffOnlyList.toArray(new String[Main.OffOnlyList.size()]);
                            MyOnlineList.UpdateList();
                           }
                        }
                            break;
                        
                        case"blocked":
                            System.exit(1);
                            break;
                            
                        case"exitGroup":
                            getGroupMessage(msgFromServer[1]).getCh().dispose();
                            for(int i = 0 ; i < MyGroupsQList.size(); i++)
                            {
                                if(getGroupMessage(msgFromServer[1]) == MyGroupsQList.get(i))
                                {
                                    MyGroupsQList.remove(i);
                                    break;
                                }
                            }
                            JOptionPane.showMessageDialog(null,"You are removed from Group " + msgFromServer[1]);
                            break;
                    }
                    
                }
                if(!msgs.isEmpty())
                {
                    /*
                    string x= queque.pop
                    #dos.write ely fl queue.get (pop)
                    
                    switch case ana b3t eh 3lshan gwa kol case hkon 3arf ana hast2bl eh
                    
                    gwa kol swtich case do read while("HB") di klma w7da msh lazm split 
                    b3dha el valid info ely ana 3ayzha h3ml split w ashof lw success aw failed
                    
                    1- request IP ("ip&esm el user")
                    has2bl 7aga ("success&ipNumber") -> aft7 client socket s = new socket(port number)
                    create new thread of class chat (string ip,int portNumber ,String name)

                    2- create group ("group&esm el group&members")
                    hast2bl 7aga ("success&portNumber") -> aft7 client socket s = new socket(port number)
                    create new thread of class chat (string ip,int portNumber ,String name)

                    3- add user to group ("addUser&esm el group&asma2 el users")
                    hast2bl 7aga ("success")

                    4- remove user ("removeUser&esm el group&asma2 el users")
                    hast2bl 7aga ("success")
                    */
                    String read = msgs.poll();
                    dos.writeUTF(read);
                    String[] splitted = read.split("&"); //array of what i sent to the server
                    String rcv;
                    rcv=dis.readUTF();
                   // String[] reply = rcv.split("&"); //array of what i got from server
                    System.out.println("FROM THE SERVER"+rcv + "    SPLITTED    " + read);
                    switch(splitted[0])
                    {
                        case"ip":
                        {
                            /*do{
                                if(dis.available()!=0)
                               rcv=dis.readUTF();
                               
                               //reply = rcv.split("&");
                            }while(rcv.matches("HB"));
                            */
                            String[] reply = rcv.split("&"); //array of what i got from server
                            /*
                             1- request IP ("ip&esm el user")
                            has2bl 7aga ("success&ipNumber") -> aft7 client socket s = new socket(port number)
                            create new thread of class chat (string ip,int portNumber ,String name)
                            */
                            if(reply[0].matches("success"))
                            {
                                String ip=reply[2].substring(1,reply[2].length());
                               String id=reply[1]; //problem here ! da esm ely e5trtw wna msh 3wzha hena ana 3wzo fl Main !
                                 System.out.println(ip);
                               // Socket s = new Socket(ip,1234);//ip i got & my port number
                                //create new thread of class chat gui pass(strong ip,int port number, string name)
                          GroupMessage g=new GroupMessage(ip,Main.MyServerPort,id,0);//ana mfrod
                          //bab3t esmy ana lely haklmo f hna kda sa7.
                          g.start();
                            
                            }
                            else
                             JOptionPane.showMessageDialog(null,"Failed to contact!!");
                        }break;
                            
                        case"group":
                        {
                            /*do{
                               rcv=dis.readUTF();
                               //reply = rcv.split("&");
                            }while(rcv.matches("HB"));
                            */
                            ArrayList<String> SelectedGrpNames = new ArrayList<>();
                            for(int i=2;i<splitted.length;i++)
                            {
                                SelectedGrpNames.add(splitted[i]);
                            }
                            
                            
                            System.out.println("rcv     " + rcv);
                            String[] reply = rcv.split("&"); //array of what i got from server
                            /*
                            2- create group ("group&esm el group&members")
                            hast2bl 7aga ("success&portNumber") -> aft7 client socket s = new socket(port number)
                            create new thread of class chat (string ip,int portNumber ,String name)
                            */
                            if(reply[0].matches("success"))
                            {
                                String portNumber=reply[1];
                                int prtNum = Integer.parseInt(portNumber);
                                //Socket s = new Socket(WelcomeScreen.MainServerIp,prtNum);//ip of server & port number i got
                                //create new thread of class chat gui pass(strong ip,int port number, string name)
                                System.out.println("Success Group created socket at server with ip " + Main.MainServerIp +" with the server on port " + prtNum + " with Group name" + splitted[1]);
                                GroupMessage gm = new GroupMessage(Main.MainServerIp,prtNum,splitted[1],1,SelectedGrpNames);
                                gm.start();
                                MyGroupsQList.add(gm);
                                
                            }
                            else
                             JOptionPane.showMessageDialog(null,"Failed to create a group!!");
                        }break;
                            
                        case"addUser":
                        {
                            /*do{
                               rcv=dis.readUTF();
                               //reply = rcv.split("&");
                            }while(rcv.matches("HB"));
                            */
                            String[] reply = rcv.split("&"); //array of what i got from server
                            /*
                            3- add user to group ("addUser&esm el group&asma2 el users")
                            hast2bl 7aga ("success") -> add him to the selected list
                            */
                            ArrayList<String> addedUsers = new ArrayList<>();
                            for(int i=2;i<splitted.length;i++)
                            {
                                addedUsers.add(splitted[i]);
                            }
                            if(reply[0].matches("success"))
                            {
                                ArrayList<String> memberstemp = getGroupMessage(reply[1]).getSSelected();
                                
                                Iterator<String> it = addedUsers.iterator();
                                while(it.hasNext())
                                {
                                    if(memberstemp.contains(it.next()))
                                    { 
                                        memberstemp.add(it.next());
                                        it.remove();
                                    }
                                        
                                }
                            }
                            else
                             JOptionPane.showMessageDialog(null,"Failed to add user!!");
                        }break;
                            
                        case"removeUser":
                        {
                            /*do{
                               rcv=dis.readUTF();
                               //reply = rcv.split("&");
                            }while(rcv.matches("HB"));
                            */
                            String[] reply = rcv.split("&"); //array of what i got from server
                            /*
                             4- remove user ("removeUser&esm el group&asma2 el users")
                            hast2bl 7aga ("success")
                            */
                            ArrayList<String> removedUsers = new ArrayList<>();
                            for(int i=2;i<splitted.length;i++)
                            {
                                removedUsers.add(splitted[i]);
                            }
//                            if(reply[0].matches("success"))
//                            {
                                ArrayList<String> memberstemp = getGroupMessage(reply[1]).getSSelected();
                                
                                Iterator<String> it = removedUsers.iterator();
                                while(it.hasNext())
                                {
                                    if(memberstemp.contains(it.next()))
                                    { 
                                        memberstemp.remove(it.next());
                                        it.remove();
                                    }
                                        
                                }
                                getGroupMessage(reply[1]).setSSelected(memberstemp);
//                            }
//                            else
//                             JOptionPane.showMessageDialog(null,"Failed to remove user!!");
                        }break;
                            case "public":
                        {
                            /*
                            bab3at public& 
                            bast2bl success&grpName&grpNumber
                            */
                            System.out.println("rcv     " + rcv);
                            String[] reply = rcv.split("&"); //array of what i got from server
                            if(reply[0].matches("success"))
                            {
                                String GroupName=reply[1];
                                String portNumber=reply[2];
                                int prtNum = Integer.parseInt(portNumber);
                                //Socket s = new Socket(WelcomeScreen.MainServerIp,prtNum);//ip of server & port number i got
                                //create new thread of class chat gui pass(strong ip,int port number, string name)
                                System.out.println("Success in entering the public group "+" on port"+prtNum+"group name is "+GroupName);
                                
                                GroupMessage gm = new GroupMessage(Main.MainServerIp,prtNum,GroupName,3);
                                gm.start();
                                
                            }
                            else
                             JOptionPane.showMessageDialog(null,"Failed to enter the public group !!");
                           
                        }break;
                    }
                    
                }
                  }
                 catch(Exception e){
                     }
              }
            
        
        System.exit(1);
    }
    
    
 public void requestUser(String UserName)
    {
      
        String conc = "ip&"+UserName;
        msgs.add(conc);
    }
 
 public void CreateGroup(String grpName,String[] members)//selected msh el list
 {
  
     String conc = "";
     String conc_temp = "";
     for(int i=0;i<members.length;i++)
     {
         conc_temp += "&"+members[i];
     }
     
     conc = "group&"+grpName+conc_temp;
     msgs.add(conc);
     System.out.println("CREATEGROUP    "+conc);
 }
 
 public void AddUserToGroup(String[] UserName,String GroupName)
 {
     String conc = "";
     for(int i=0;i<UserName.length;i++)
     {
         conc += "&" + UserName[i];
     }
     conc = "addUser&" + GroupName + conc;
     msgs.add(conc);
 }
 public void removeUser(String[] UserName,String GroupName)
 {
     String conc ="";
     for(int i=0;i<UserName.length;i++)
     {
         conc += "&" + UserName[i];
     }
     conc = "removeUser&" + GroupName + conc;
     System.out.println("Users removed : "+ conc);
     msgs.add(conc);
 }
 
  public void AddToPublicGroup()
 {
     String conc ="public&";
     msgs.add(conc);
 }
 
 public GroupMessage getGroupMessage(String GroupName)
 {
     for(GroupMessage gms : MyGroupsQList){
        if(gms.getChatId().equals(GroupName))
        {
            return gms;
        }
     }
     return null;
 } 
 
}
/*
1- request IP ("ip&esm el user")
has2bl 7aga

2- create group ("group&esm el group&members")
hast2bl 7aga

3- add user to group ("addUser&esm el group&asma2 el users")
hast2bl 7aga

4- remove user ("removeUser&esm el group&asma2 el users")
hast2bl 7aga
------------------------------
4 public functions
public funtion request group -> hst2bl mn fady esm el group wl members -> ha5dhom a7thom fi string b tre2a #2
funtion 
*/

