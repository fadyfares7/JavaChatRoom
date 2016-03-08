import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class EditGroup extends JFrame {

    private ChatWindow MyChatWindow;
    private ArrayList<String> selectedListRight;
    private ArrayList<String> NotInGroupListLeft;
    private ArrayList<String> addedMemebersList;
    
    public EditGroup(ChatWindow c) {
        
        MyChatWindow =c;
        
        initComponents();
        
        addedOnline.setListData(c.getGroupChatSelectedList().toArray(new String[c.getGroupChatSelectedList().size()]));
      selectedListRight = MyChatWindow.getGroupChatSelectedList();
        NotInGroupListLeft=Main.OnOnlyList;
    Iterator<String> it = NotInGroupListLeft.iterator();
                while(it.hasNext())
                {
                    if(selectedListRight.contains(it.next()))
                        it.remove();
                }
                otherOnline.setListData(NotInGroupListLeft.toArray(new String[NotInGroupListLeft.size()]));
       // otherOnline.setListData(c.getGroupChatNotSelectedList().toArray(new String[c.getGroupChatNotSelectedList().size()]));
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        otherOnline = new javax.swing.JList<String>();
        addButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        addedOnline = new javax.swing.JList<String>();

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jScrollPane1.setViewportView(otherOnline);

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(addedOnline);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 8, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // TODO add your handling code here:
        
       selectedListRight = MyChatWindow.getGroupChatSelectedList();
       NotInGroupListLeft = Main.OnOnlyList;
       
       int temp[]= otherOnline.getSelectedIndices();
       if(temp.length != 0)
       {
           addedMemebersList = new ArrayList<String>();
           
       for(int i=0;i<temp.length;i++)
       {
           selectedListRight.add(NotInGroupListLeft.get(temp[i]));
           addedMemebersList.add(NotInGroupListLeft.get(temp[i]));
       }
       
       Iterator<String> it = NotInGroupListLeft.iterator();
                while(it.hasNext())
                {
                    if(selectedListRight.contains(it.next()))
                        it.remove();
                }
       
       MyChatWindow.setGroupChatSelectedList(selectedListRight);
       MyChatWindow.setGroupChatNotSelectedList(NotInGroupListLeft);
       addedOnline.setListData(selectedListRight.toArray(new String[selectedListRight.size()]));
       /*
        if(MyChatWindow.getGroupChatNotSelectedList().size() == 0)
        {
            OnlineList.NotInGroup = OnlineList.Notgrp.toArray(new String[0]);
        }
        else
            OnlineList.NotInGroup = OnlineList.Notgrp.toArray(new String[OnlineList.NotInGroup.length]);
        */
         otherOnline.setListData(NotInGroupListLeft.toArray(new String[NotInGroupListLeft.size()]));
         
         OnlineList.Sconnect.AddUserToGroup(addedMemebersList.toArray(new String[addedMemebersList.size()]), MyChatWindow.getId());
         
       }
       else
       {
           JOptionPane.showMessageDialog(null,"please choose a member to add!");
       }
         
    }//GEN-LAST:event_addButtonActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // TODO add your handling code here:
        updateOnlineList();
    }//GEN-LAST:event_formFocusGained

public void updateOnlineList()
{
    selectedListRight = MyChatWindow.getGroupChatSelectedList();
    NotInGroupListLeft=Main.OnOnlyList;
    Iterator<String> it = NotInGroupListLeft.iterator();
                while(it.hasNext())
                {
                    if(selectedListRight.contains(it.next()))
                        it.remove();
                }
                otherOnline.setListData(NotInGroupListLeft.toArray(new String[NotInGroupListLeft.size()]));
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JList<String> addedOnline;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> otherOnline;
    // End of variables declaration//GEN-END:variables
}
