import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnlineList extends JFrame {
    private JList list;
    
    private JFrame f;
    private JList OffList;
    private JList BlkList;
    
    
    public static String[] selected ;
    public static ArrayList<String> selectedList;

    public static String[] NotInGroup;
    public static String grpname;
    public static ArrayList<String> Notgrp;
    private Boolean groupFlag;
    //public static ArrayList<String> OnOnlyList;
   // public static ArrayList<String> OffOnlyList;
   
    //private JTextArea display;
    private JScrollPane s;
    private JScrollPane o;
    private JScrollPane b;
    //private JLabel trial;
    private JLabel OnlineLabel;
    private JLabel OfflineLabel;
    private JButton Chat;
    private JTextArea GroupName;
    private JLabel GroupNameLabel;
    private JButton PublicGroup;
    private JButton refresh;
    private JLabel BlackedLabel;
    public static ServerConnection Sconnect;
    public void UpdateList()
    {
        list.setListData(Main.OnOnly);
        OffList.setListData(Main.OffOnly);
        BlkList.setListData(Main.Blockedppl);

    }
    private void formWindowClosed(java.awt.event.WindowEvent evt) throws IOException {                                  
        // TODO add your handling code here:
        Sconnect.CloseMySocket();
    }    
    public OnlineList(ServerConnection c)
    {
       
        super("CSE Chat");
        setLayout(null);
         Sconnect=c;

        /*Initializations*/
        list = new JList(Main.OnOnly);
        list.setVisibleRowCount(4);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        OffList = new JList(Main.OffOnly);
        OffList.setVisibleRowCount(4);
        OffList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        BlkList = new JList(Main.Blockedppl);
        BlkList.setVisibleRowCount(4);
        BlkList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        Chat = new JButton("Chat");
        PublicGroup = new JButton("Join Public Group");
        refresh = new JButton();
        //display=  new JTextArea();
        //trial = new JLabel("new label");
        OnlineLabel = new JLabel("online");
        OfflineLabel = new JLabel("Offline");
        GroupName = new JTextArea();
        GroupNameLabel = new JLabel("Group Name");
        BlackedLabel = new JLabel("Blocked");

        //NotInGroup = new String[OnOnly.length - selected.length];
        Notgrp = new ArrayList<String>();
        
        
        /* 2 scrollable panels*/
        s = new JScrollPane(list);
        o = new JScrollPane(OffList);
        b = new JScrollPane(BlkList);

        /*Dimensions layout on the screen*/
        Dimension ScrollSize = s.getPreferredSize();
        Dimension ScrillSize = o.getPreferredSize();
        Dimension ScrolSize = b.getPreferredSize();
        //Dimension TrialSize = trial.getPreferredSize();
        Dimension OnlineLabelSize = OnlineLabel.getPreferredSize();
        Dimension OfflineLabelSize = OfflineLabel.getPreferredSize();
        Dimension ChatBttnSize = Chat.getPreferredSize();
        Dimension PublicGroupSize = PublicGroup.getPreferredSize();
       // Dimension RefreshSize = refresh.getPreferredSize();
        //Dimension displaySize = display.getPreferredSize();
        Dimension GroupNameSize = GroupName.getPreferredSize();
        Dimension GroupNameLabelSize = GroupNameLabel.getPreferredSize();
        Dimension BlockedSize = BlackedLabel.getPreferredSize();
        
        /* X&Y axis*/
        OnlineLabel.setBounds(30,35,OnlineLabelSize.width,OnlineLabelSize.height);
        OfflineLabel.setBounds(30,155,OfflineLabelSize.width,OfflineLabelSize.height);
        s.setBounds(30,50,100,100);
        o.setBounds(30,170,100,100);
        b.setBounds(145,80,100,100);
        //trial.setBounds(150,100,TrialSize.width,TrialSize.height);
        Chat.setBounds(50,275,ChatBttnSize.width,ChatBttnSize.height);
        PublicGroup.setBounds(20,310,PublicGroupSize.width,PublicGroupSize.height);
        refresh.setBounds(150,10,32,32);
        refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/refresh.png")));
        //display.setBounds(150,130,100,100);
        GroupName.setBounds(30,18,100,GroupNameSize.height);
        GroupNameLabel.setBounds(30,2,GroupNameLabelSize.width,GroupNameLabelSize.height);
        BlackedLabel.setBounds(110,65,BlockedSize.width,BlockedSize.height);
        
        //refresh button
        refresh.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent ev) {
            
           java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Refresh   ");
               UpdateList(); // sending data to the server
            }
           });
            }
        });
        //public group
        PublicGroup.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent ev) {
            
           java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println("PUBLIC GROUP   " + grpname);
               Sconnect.AddToPublicGroup(); // sending data to the server
            }
           });
            }
        });
        
        /*action when click on chat button*/
        Chat.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent ev) {
        
      /*  for(int i = 0; i < OnOnly.length ; i++)
        {
            int flag=0;
            for(int j=i ;j<selected.length;j++)
            {
               if(flag==0)
               {
                if(!OnOnly[i].contains(selected[j]))
                {
                    Notgrp.add(OnOnly[i]);
                    flag=1;
                }
               }
            }
        }
        */
       if(list.getSelectedIndices().length !=0)
       {
        Notgrp = Main.OnOnlyList;
        Iterator<String> it = Notgrp.iterator();
        while(it.hasNext())
        {
            if(selectedList.contains(it.next()))
                it.remove();
        }
        
        NotInGroup = new String[Notgrp.size()];
        NotInGroup = Notgrp.toArray(new String[Notgrp.size()]);

           if(selected.length > 1)
           {if(GroupName.getText().equals(""))
                JOptionPane.showMessageDialog(null,"Please Enter Name Of the Group!");
             else{
               groupFlag = true;
               grpname = new String(GroupName.getText());
    
// Group chating
               
            java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println("GROUP NAME IN ONLINE LIST   " + grpname);
               Sconnect.CreateGroup(grpname, selected); // sending data to the server
            }
        });
            
             
            
            }
           }
           else
           {
             //display.setText("");
             groupFlag = false;
            
                //
//            try {
//                
////                    GroupMessage startchat=new GroupMessage("127.0.0.1",1234, "fady");
////                        startchat.start();
//
//            } catch (IOException ex) {
//                Logger.getLogger(OnlineList.class.getName()).log(Level.SEVERE, null, ex);
//            }
             java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println("CHAT WITH " + selected[0]);
             Sconnect.requestUser(selected[0]); // sending data to the server
            }
        });
            
            
           }
          }
       else
          JOptionPane.showMessageDialog(null,"Please choose someone to chat with!");
         }
        });
        
        /*ADD things on the screen*/
        add(s);
        add(o);
       // add(b);
       // add(BlackedLabel);
        //add(trial);
        add(OnlineLabel);
        add(Chat);
        add(refresh);
        //add(display);
        add(OfflineLabel);
        add(GroupName);
        add(GroupNameLabel);
        add(PublicGroup);
 
        
        /*event on click on item in the list*/
        list.addListSelectionListener(
                new ListSelectionListener(){
                    public void valueChanged(ListSelectionEvent event)
                    {
                        int[] selectedIx = list.getSelectedIndices();
                        selected = new String[selectedIx.length];
                        for (int i = 0; i < selectedIx.length; i++) {
                        String sel = list.getModel().getElementAt(selectedIx[i]).toString();
                        selected[i]=sel;
                        //String size = Integer.toString(list.getSelectedIndex());
                        //trial.setText(size);
                        //add(new JLabel((String) list.getModel().getElementAt(selectedIx[i])));
                        }
                        String size = Integer.toString(selectedIx.length);
                        //trial.setText(size);
                        selectedList = new ArrayList<String>();
                         for(int i=0 ; i <selected.length ;i++)
                            selectedList.add(selected[i]);
                    };
                }
                
        );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(215,420);
        setVisible(true);
    }
    
    
}
