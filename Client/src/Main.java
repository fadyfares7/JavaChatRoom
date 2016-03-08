import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte0.runnable;

public class Main {
    
           public static final String MainServerIp="192.168.173.1";
           public static final int MainServerPort=11223;  
 
    
    
//    public static ArrayList<StartChating> Requests=new ArrayList<StartChating>();
//    public static ArrayList<clientHandlerP2P> chatingWithme=new ArrayList<clientHandlerP2P>();
      public static String username;
      
        public static String AllPeople;
        public static String[] OnPeople=new String[0];;
        public static String[] OnOnly=new String[0];;
        public static String[] OffOnly=new String[0];;
        public static String[] Blockedppl=new String[0];;
        public static ArrayList<String> BlockedpplList;
        public static ArrayList<String> OnOnlyList;
        public static ArrayList<String> OffOnlyList;
        public static String groupNum;
          public static final int MyServerPort=1234; 
    public static void main(String[] arg)
    {
        
         
         new Thread(
            new Runnable() {
                public void run() {
                    new WelcomeScreen().setVisible(true);
                }
            }
        ).start();
//            java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                
//                new WelcomeScreen().setVisible(true);
//            }
//        });
            
                try {
            //1.Create Server Socket
            ServerSocket sv = new ServerSocket(MyServerPort);
           while (true) {
                //2.Listen for Clients
                Socket c;
                c = sv.accept();
                System.out.println("New Chat request");
                
                GroupMessage g=new GroupMessage(c,4);// problem here ! I should get 
               // user id from the one who has requested chatting withme
                
                g.start();
            }
                }
         catch (Exception e) {
            System.out.println(e.getMessage());
        }
            
             
    }
}
