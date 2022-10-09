package sendfile.client;


import sendfile.client.LoginForm;
import sendfile.client.ClientThread;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainForm extends javax.swing.JFrame {
    String username;
    String host;
    int port;
    Socket socket;
    DataOutputStream dos;
    public boolean attachmentOpen = false;
    private boolean isConnected = false;
    private String mydownloadfolder = "D:\\";

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
        MyInit();
    }
        public void initFrame(String username, String host, int port){
        this.username = username;
        this.host = host;
        this.port = port;
        setTitle("You are currently logged in as: " + username);
        //Kết nối 
        connect();
    }
     void MyInit(){
         setLocationRelativeTo(null);
     }
    
    public void connect(){
        appendMessage(" Connecting...", "Status", Color.PINK, Color.PINK);
        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            // gửi username đang kết nối
            dos.writeUTF("CMD_JOIN "+ username);
            appendMessage(" Connected", "Status", Color.PINK, Color.PINK);
            appendMessage(" Send a message now!", "Status", Color.PINK, Color.PINK);
            
            // Khởi động Client Thread 
            new Thread(new ClientThread(socket, this)).start();
            btn_Send.setEnabled(true);
            // đã được kết nối
            isConnected = true;
            
        }
        catch(IOException e) {
            isConnected = false;
            JOptionPane.showMessageDialog(this, "Unable to connect to the server, please try again later!","Connection failed",JOptionPane.ERROR_MESSAGE);
            appendMessage("[IOException]: "+ e.getMessage(), "Error", Color.RED, Color.RED);
        }
    }
    
    /*
        Được kết nối
    */
    public boolean isConnected(){
        return this.isConnected;
    }
    
    /*
        Hiển thị Message
    */
    public void appendMessage(String msg, String header, Color headerColor, Color contentColor){
        txt_ChatBox.setEditable(true);
        getMsgHeader(header, headerColor);
        getMsgContent(msg, contentColor);
        txt_ChatBox.setEditable(false);
    }
    
    /*
        Tin nhắn chat
    */
    public void appendMyMessage(String msg, String header){
        txt_ChatBox.setEditable(true);
        getMsgHeader(header, Color.GREEN);
        getMsgContent(msg, Color.BLACK);
        txt_ChatBox.setEditable(false);
    }
    
    /*
        Tiêu đề tin nhắn
    */
    public void getMsgHeader(String header, Color color){
        int len = txt_ChatBox.getDocument().getLength();
        txt_ChatBox.setCaretPosition(len);
        txt_ChatBox.setCharacterAttributes(MessageStyle.styleMessageContent(color, "Impact", 13), false);
        txt_ChatBox.replaceSelection(header+":");
    }
    /*
        Nội dung tin nhắn
    */
    public void getMsgContent(String msg, Color color){
        int len = txt_ChatBox.getDocument().getLength();
        txt_ChatBox.setCaretPosition(len);
        txt_ChatBox.setCharacterAttributes(MessageStyle.styleMessageContent(color, "Arial", 12), false);
        txt_ChatBox.replaceSelection(msg +"\n\n");
    }
    
    public void appendOnlineList(Vector list){
        sampleOnlineList(list); 
    }
    
    /*
        Hiển thị danh sách đang online
    */
    public void showOnLineList(Vector list){
        try {
            txt_Online.setEditable(true);
            txt_Online.setContentType("text/html");
            StringBuilder sb = new StringBuilder();
            Iterator it = list.iterator();
            sb.append("<html><table>");
            while(it.hasNext()){
                Object e = it.next();
                URL url = getImageFile();
                Icon icon = new ImageIcon(this.getClass().getResource("/images/online.png"));
                sb.append("<tr><td><b>></b></td><td>").append(e).append("</td></tr>");
                System.out.println("Online: "+ e);
            }
            sb.append("</table></body></html>");
            txt_Online.removeAll();
            txt_Online.setText(sb.toString());
            txt_Online.setEditable(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    /*
      ************************************  Hiển thị danh sách online  *********************************************
    */
    private void sampleOnlineList(Vector list){
        txt_Online.setEditable(true);
        txt_Online.removeAll();
        txt_Online.setText("");
        Iterator i = list.iterator();
        while(i.hasNext()){
            Object e = i.next();
            /*  Hiển thị Username Online   */
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(Color.white);
            
            Icon icon = new ImageIcon(this.getClass().getResource("/images/online.png"));
            JLabel label = new JLabel(icon);
            label.setText(" "+ e);
            panel.add(label);
            int len = txt_Online.getDocument().getLength();
            txt_Online.setCaretPosition(len);
            txt_Online.insertComponent(panel);
            /*  Append Next Line   */
            sampleAppend();
        }
        txt_Online.setEditable(false);
    }
    private void sampleAppend(){
        int len = txt_Online.getDocument().getLength();
        txt_Online.setCaretPosition(len);
        txt_Online.replaceSelection("\n");
    }
    /*
      ************************************  Show Online Sample  *********************************************
    */
    
    
    
    
    /*
        Get image file path
    */
    public URL getImageFile(){
        URL url = this.getClass().getResource("/images/online.png");
        return url;
    }
    
    
    /*
        Set myTitle
    */
    public void setMyTitle(String s){
        setTitle(s);
    }
    
    /*
        Phương thức tải get download
    */
    public String getMyDownloadFolder(){
        return this.mydownloadfolder;
    }
    
    /*
        Phương thức get host
    */
    public String getMyHost(){
        return this.host;
    }
    
    /*
        Phương thức get Port
    */
    public int getMyPort(){
        return this.port;
    }
    
    /*
        Phương thức nhận My Username
    */
    public String getMyUsername(){
        return this.username;
    }
    
    /*
        Cập nhật Attachment 
    */
    public void updateAttachment(boolean b){
        this.attachmentOpen = b;
    }
    
    /*
        Hàm này sẽ mở 1 file chooser
    */
    public void openFolder(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int open = chooser.showDialog(this, "Open Folder");
        if(open == chooser.APPROVE_OPTION){
            mydownloadfolder = chooser.getSelectedFile().toString()+"\\";
        } else {
            mydownloadfolder = "D:\\";
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txt_ChatBox = new javax.swing.JTextPane();
        txt_InputChat = new javax.swing.JTextField();
        btn_Send = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txt_Online = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        LogoutMenu = new javax.swing.JMenuItem();
        menu_ShareFiles = new javax.swing.JMenu();
        sendFileMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));

        txt_ChatBox.setFont(new java.awt.Font("Yu Gothic UI Light", 0, 11)); // NOI18N
        jScrollPane1.setViewportView(txt_ChatBox);

        txt_InputChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_InputChatActionPerformed(evt);
            }
        });

        btn_Send.setBackground(new java.awt.Color(167, 255, 228));
        btn_Send.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_Send.setForeground(new java.awt.Color(255, 116, 177));
        btn_Send.setText("SEND");
        btn_Send.setEnabled(false);
        btn_Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SendActionPerformed(evt);
            }
        });

        txt_Online.setFont(new java.awt.Font("Tahoma", 1, 9)); // NOI18N
        txt_Online.setForeground(new java.awt.Color(120, 14, 3));
        txt_Online.setAutoscrolls(false);
        txt_Online.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setViewportView(txt_Online);

        jLabel1.setBackground(new java.awt.Color(255, 153, 153));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("Online:");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/facebook_messenger_80px.png"))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(147, 125, 194));
        jLabel4.setText("TEAM 5 CHATTING APPLICATION");

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.png"))); // NOI18N
        jMenu2.setText("Account");

        LogoutMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loggoff.png"))); // NOI18N
        LogoutMenu.setText("Log Out");
        LogoutMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutMenuActionPerformed(evt);
            }
        });
        jMenu2.add(LogoutMenu);

        jMenuBar1.add(jMenu2);

        menu_ShareFiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sharing.png"))); // NOI18N
        menu_ShareFiles.setText("Share Files");
        menu_ShareFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_ShareFilesActionPerformed(evt);
            }
        });

        sendFileMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sendfile.png"))); // NOI18N
        sendFileMenu.setText("Send File");
        sendFileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendFileMenuActionPerformed(evt);
            }
        });
        menu_ShareFiles.add(sendFileMenu);

        jMenuBar1.add(menu_ShareFiles);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(txt_InputChat, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_Send, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(30, 30, 30)
                .addComponent(jLabel3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_InputChat, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_Send, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)))
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendFileMenuActionPerformed
        // TODO add your handling code here:
        if(!attachmentOpen){
            SendFile s = new SendFile();
            if(s.prepare(username, host, port, this)){
                s.setLocationRelativeTo(null);
                s.setVisible(true);
                attachmentOpen = true;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to set up File Sharing at this time, please try again later!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_sendFileMenuActionPerformed

    private void menu_ShareFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_ShareFilesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menu_ShareFilesActionPerformed

    private void LogoutMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutMenuActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you signed out?");
        if(confirm == 0){
            try {
                socket.close();
                setVisible(false);
                /** Login Form **/
                new LoginForm().setVisible(true);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }//GEN-LAST:event_LogoutMenuActionPerformed

    private void txt_InputChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_InputChatActionPerformed
        // TODO add your handling code here:
        try {
            String content = username+" "+ evt.getActionCommand();
            dos.writeUTF("CMD_CHATALL "+ content);
            appendMyMessage(" "+evt.getActionCommand(), username);
            txt_InputChat.setText("");
        } catch (IOException e) {
            appendMessage(" Cannot send message now, cannot connect to Server at this time, please try again later or restart this application.!", "Error", Color.RED, Color.RED);
        }
    }//GEN-LAST:event_txt_InputChatActionPerformed

    private void btn_SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SendActionPerformed
        // TODO add your handling code here:
        try {
            String content = username+" "+ txt_InputChat.getText();
            dos.writeUTF("CMD_CHATALL "+ content);
            appendMyMessage(" "+txt_InputChat.getText(), username);
            txt_InputChat.setText("");
        } catch (IOException e) {
            appendMessage(" Cannot send message now, cannot connect to Server at this time, please try again later or restart this application!", "Error", Color.RED, Color.RED);
        }
    }//GEN-LAST:event_btn_SendActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem LogoutMenu;
    private javax.swing.JButton btn_Send;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JMenu menu_ShareFiles;
    private javax.swing.JMenuItem sendFileMenu;
    private javax.swing.JTextPane txt_ChatBox;
    private javax.swing.JTextField txt_InputChat;
    private javax.swing.JTextPane txt_Online;
    // End of variables declaration//GEN-END:variables
}
