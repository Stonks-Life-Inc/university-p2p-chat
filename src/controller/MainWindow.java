package src.controller;

import src.app.P2PChatSystem;
import src.app._GLOBAL;
import src.network.HelloThread;
import src.network.RecieverThread;
import src.network.SenderThread;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JButton conBtn;
    private JPanel friendListPan;
    private JList friendList;
    private JTextField msgInputBox;
    private JButton sendBtn;
    private JPanel msgTAPanel;

    private JTextArea msgTextArea;

    public MainWindow() {
        super("Chat");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setSize(750,800);
        setMinimumSize(getSize());

        // Connection button listener
        conBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Do a test to see if user is connected to the network or not
                // If not, we want to display a new instance of the connection form window
                // Else we want to display a new instance of the disconnection form window
                if (!_GLOBAL.isConnected()) {
                    new Connect();
                } else {
                    new Disconnect();
                }

            }
        });
        //Listen for enter key press on the input box
        //if enter key is pressed, send the message
        msgInputBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    super.keyPressed(e);
                    sendBtn.doClick(); //call the send button action listener to save lines of code
                }

            }
        });

        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Calling the send message method from ServerThread class

                try{
                    SenderThread sender = new SenderThread(InetAddress.getByName(_GLOBAL.getCurrRemoteUsrIP()), _GLOBAL.getMsgPort());
                    sender.start();
                    sender.setMsg(msgInputBox.getText());
                    sender.interrupt();
                    System.out.println("Sending packet to " + _GLOBAL.getCurrRemoteUsrIP() + ":" + _GLOBAL.getMsgPort() + " Message : " + msgInputBox.getText());
                    msgTextArea.append("\n You: " + msgInputBox.getText());

                } catch (SocketException ex) {
                    ex.printStackTrace();
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
                msgInputBox.setText("");

            }
        });
        friendList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // We must do a +2 because our local user is getting registered twice when connectong.
                // Why do we need to do a +2?
                // Why is the user being registered twice?
                // I don't know. I don't care. I don't have time to find the source of the problem and fix this!
                _GLOBAL.setCurrRemoteUsrIP(HelloThread.getUsrs().get(friendList.getSelectedIndex()+2).getIpAdress());

            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            /**
             * On closing Window, we send a Bye Request to all the users
             * @exception IOException
             */
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if(_GLOBAL.isConnected()) {
                    try {
                        // add your code here
                        _GLOBAL.setConnected(false);

                        //We disconnect
                        _GLOBAL.getLocalUser().bye();

                        dispose();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public JTextField getMsgInputBox() {
        return msgInputBox;
    }

    public void setMsgInputBox(JTextField msgInputBoxArg) {
        msgInputBox = msgInputBoxArg;
    }

    public JButton getSendBtn() {
        return sendBtn;
    }

    public void setSendBtn(JButton sendBtnArg) {
        sendBtn = sendBtnArg;
    }

    public JTextArea getMsgTextArea() {
        return msgTextArea;
    }

    public void setMsgTextArea(JTextArea msgTextArea) {
        this.msgTextArea = msgTextArea;
    }

    public JButton getConBtn() {
        return conBtn;
    }

    public void setConBtn(JButton conBtn) {
        this.conBtn = conBtn;
    }

    public JList getFriendList() {
        return friendList;
    }

    public void setFriendList(JList friendList) {
        this.friendList = friendList;
    }

    // Method to update the friend list
    public void updateConUserList(ArrayList<src.entity.Contact.User> userList) {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (src.entity.Contact.User user : userList) {
            listModel.addElement(user.getName());
        }
        friendList.setModel(listModel);
    }


}
