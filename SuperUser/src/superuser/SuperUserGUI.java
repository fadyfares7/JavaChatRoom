package superuser;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class SuperUserGUI extends JFrame{
    
    private static JList unblocked;
    private static JList blocked;
    
//    private static String rcv;//receive from server
//    private static String[] rcvArray = new String[0];//split rcv in this array (all people)
    
    public static String[] notbannedPeople = new String[0] ;
    public static ArrayList<String> notbannedList;

    public static String[] bannedPeople = new String[0];
    public static ArrayList<String> bannedList;

    public static String[] selected = new String[0];
    public static ArrayList<String> selectedList;
    
    public static String[] notselected = new String[0];
    public static ArrayList<String> notselectedList;
    //private static int ErrorFlag;

    private JScrollPane all;
    private JScrollPane blockedpane;
    
    private JLabel allPeople;
    private JLabel blkdPeople;
    
    private JButton Block;
    private JButton UnBlock;
    private JButton Refresh;

    public SuperUserGUI() {
        
        super("Super User");
        setLayout(null);
        
        unblocked = new JList(notbannedPeople);
        blocked = new JList(bannedPeople);
        
        all = new JScrollPane(unblocked);
        blockedpane = new JScrollPane(blocked);
        
        Block = new JButton("Block");
        UnBlock = new JButton("Unblock");
        Refresh = new JButton("Refresh");
        
        allPeople =new JLabel("All People");
        blkdPeople =new JLabel("Blocked People");
        
        unblocked.setVisibleRowCount(4);
        unblocked.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        blocked.setVisibleRowCount(4);
        blocked.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        
        all.setBounds(20,30,100,100);
        blockedpane.setBounds(20,160,100,100);
        
        Dimension BlkPane = blockedpane.getPreferredSize();
        Dimension AllPeople = allPeople.getPreferredSize();
        Dimension BlkPeople = blkdPeople.getPreferredSize();
        Dimension BlockButton = Block.getPreferredSize();
        Dimension unblockButton = UnBlock.getPreferredSize();
        Dimension refreshButton = Refresh.getPreferredSize();
        Dimension AllPane = all.getPreferredSize();

        
        allPeople.setBounds(20,10,AllPeople.width,AllPeople.height);
        blkdPeople.setBounds(20,140, BlkPeople.width,BlkPeople.height);
        Block.setBounds(130,30,unblockButton.width,BlockButton.height);
        UnBlock.setBounds(130,60,unblockButton.width,unblockButton.height);
        Refresh.setBounds(130,90,refreshButton.width,refreshButton.height);
        
        Block.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent ev) {
                if (unblocked.getSelectedIndices().length != 0)
                {
                    int[] indices = unblocked.getSelectedIndices();
                    String msg = "ban";
                    for(int i=0;i<indices.length;i++)
                    {
                        String user = unblocked.getModel().getElementAt(indices[i]).toString();
                        msg += "&" + user; 
                        //remove from unblocked and add to blocked 
                        notbannedList.remove(user);
                        bannedList.add(user);
                    }
                    SuperUser.msgq.add(msg);
                    UpdateList();
                }
            }});
       UnBlock.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent ev) {
                if (blocked.getSelectedIndices().length != 0)
                {
                    int[] indices = blocked.getSelectedIndices();
                    String msg = "unban";
                    for(int i=0;i<indices.length;i++)
                    {
                        String user = blocked.getModel().getElementAt(indices[i]).toString();
                        msg += "&" + user; 
                        //remove from unblocked and add to blocked 
                        bannedList.remove(user);
                        notbannedList.add(user);
                    }  
                    SuperUser.msgq.add(msg);
                    UpdateList();
                }
            
            }});
       Refresh.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent ev) {
                SuperUser.msgq.add("list");
            }});
       
       add(all);
       add(blockedpane);
       add(allPeople);
       add(blkdPeople);
       add(Block);
       add(UnBlock);
       add(Refresh);
       //for lists
       unblocked.addListSelectionListener(
                new ListSelectionListener(){
                    public void valueChanged(ListSelectionEvent event)
                    {
                        
                    }});
       
       blocked.addListSelectionListener(
                new ListSelectionListener(){
                    public void valueChanged(ListSelectionEvent event)
                    {
                        
                    }});
        
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setSize(300,400);
       setVisible(true);
       
    }
    
    public static void UpdateList()
    {
        notbannedPeople = new String[notbannedList.size()];
        notbannedPeople = notbannedList.toArray(notbannedPeople);
        bannedPeople = new String[bannedList.size()];
        bannedPeople = bannedList.toArray(bannedPeople);
        
        unblocked.setListData(notbannedPeople);
        blocked.setListData(bannedPeople);
    }
    
    private void formWindowClosed(java.awt.event.WindowEvent evt) throws IOException {                                  
        SuperUser.closeSocket();
    }  
}
