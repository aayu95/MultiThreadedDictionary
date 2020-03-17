/*
* This project has been created as part of an assignment for COMP90015 assignment 1 at University of Melbourne
* Author: Aayush Mehta
* Student id: 1105081
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Pattern;

public class DClient extends JFrame implements ActionListener {

    JButton search;
    JButton searchTab;
    JButton delete;
    JButton deleteTab;
    JButton add;
    JButton addTab;

    JTabbedPane tp;

    JTextField sField;
    JTextField dField;
    JTextField aField;
    JTextField mField;

    JLabel wordLabel;
    JLabel meaningLabel;
    JLabel formatLabel;

    JPanel cardPanel;
    JPanel searchPanel;
    JPanel deletePanel;
    JPanel addPanel;
    JPanel tabPanel;

    Container container;
    CardLayout cl;
    private static DataInputStream din;
    private static DataOutputStream dout;
    private static Socket s;

    DClient() {

//        container=getContentPane();
//        card = new CardLayout(20,20);
//        container.setLayout(card);
//
//        next= new JButton("Next");
//        next.setBounds(10,10,100,100);
//        search.addActionListener(this);
//
//
//        search = new JButton("Search");//creating instance of JButton
//        search.setBounds(130, 100, 100, 40);
//        search.addActionListener(this);
//
//        delete = new JButton("Delete");//creating instance of JButton
//        delete.setBounds(130, 100, 150, 40);
//        delete.addActionListener(this);
//
//        container.add("search",search);//adding button in JFrame
//        container.add("delete",delete);//adding button in JFrame
//
//        add(next);

        cardPanel = new JPanel();
        cl = new CardLayout();

        cardPanel.setLayout(cl);


        searchPanel = new JPanel();
        searchPanel.setBackground(Color.gray);
        searchPanel.setBounds(100, 100, 200, 200);
        deletePanel = new JPanel();
        deletePanel.setBackground(Color.gray);
        addPanel = new JPanel();
        addPanel.setBackground(Color.gray);

        tabPanel = new JPanel();

        search = new JButton("Search Word");
        searchTab = new JButton("Search");
        delete = new JButton("Delete Word");
        deleteTab = new JButton("Delete");
        add = new JButton("Add Word");
        addTab = new JButton("Add");

        sField = new JTextField(25);
        dField = new JTextField(25);
        aField = new JTextField(27);
        mField = new JTextField(27);

        wordLabel = new JLabel("Word :");
        meaningLabel = new JLabel("Meaning(s) :");
        formatLabel = new JLabel("The meanings should be separated by a semi-colon(;)");
        formatLabel.setForeground(Color.darkGray);

        searchPanel.add(sField);
        searchPanel.add(search);
        deletePanel.add(dField);
        deletePanel.add(delete);
        addPanel.add(wordLabel);
        addPanel.add(aField);
        addPanel.add(meaningLabel);
        addPanel.add(mField);
        addPanel.add(formatLabel);
        addPanel.add(add);

        cardPanel.add("search", searchPanel);
        cardPanel.add("delete", deletePanel);
        cardPanel.add("add", addPanel);

        tabPanel.add("searchTab", searchTab);
        tabPanel.add("deleteTab", deleteTab);
        tabPanel.add("addTab", addTab);

        searchTab.addActionListener(this);
        search.addActionListener(this);
        deleteTab.addActionListener(this);
        delete.addActionListener(this);
        addTab.addActionListener(this);
        add.addActionListener(this);

//        getContentPane().add(cardPanel,BorderLayout.CENTER);
//        getContentPane().add(tabPanel,BorderLayout.NORTH);

        tp = new JTabbedPane();
        tp.setBounds(50, 25, 100, 100);
        tp.add("SEARCH", searchPanel);
        tp.add("DELETE", deletePanel);
        tp.add("ADD", addPanel);

        add(tp);


        setSize(400, 300);//400 width and 500 height
        //setLayout(null);//using no layout managers
        setTitle("Connected to Server on: " + s);
        setLocationRelativeTo(null);
        setVisible(true);//making the frame visible
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == search) {
                String word = sField.getText().trim().toLowerCase();
                String searchString = "search!" + word;
                //System.out.println(searchString);
                if (word.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "!!!   Please enter a word first   !!!", "Failed entry validation", JOptionPane.PLAIN_MESSAGE);
                } else if (!word.chars().allMatch(Character::isLetter)) {
                    JOptionPane.showMessageDialog(this, "!!!   Only alphabets allowed (no spaces)  !!!", "Failed entry validation", JOptionPane.PLAIN_MESSAGE);
                } else {
                    dout.writeUTF(searchString);
                    String meaning = (din.readUTF());
                    String[] meanings = meaning.split(";");
                    String displayRes = "";
                    int counter = 1;
                    for (String mean : meanings) {
                        displayRes += counter + ". " + mean.trim() + "\n";
                        counter++;
                    }
                    if (!meaning.equals("Word not found!"))
                        JOptionPane.showMessageDialog(this, displayRes, "Meaning(s)", JOptionPane.PLAIN_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(this, "!!!   No match found in dictionary   !!!", "No match", JOptionPane.PLAIN_MESSAGE);
                }
            } else if (e.getSource() == delete) {
                String word = dField.getText().trim().toLowerCase();
                String delString = "delete!" + word;
                //System.out.println(delString);
                if (word.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "!!!   Please enter a word first   !!!", "Failed entry validation", JOptionPane.PLAIN_MESSAGE);
                } else if (!word.chars().allMatch(Character::isLetter)) {
                    JOptionPane.showMessageDialog(this, "!!!   Only alphabets allowed ((no spaces)  !!!", "Failed entry validation", JOptionPane.PLAIN_MESSAGE);
                } else {
                    dout.writeUTF(delString);
                    String meaning = (din.readUTF());
                    if (!meaning.equals("Word not found!"))
                        JOptionPane.showMessageDialog(this, "Word deleted Successfully !!!", "Deleted", JOptionPane.PLAIN_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(this, "!!!   No match found in dictionary   !!!", "No match", JOptionPane.PLAIN_MESSAGE);
                }
            } else if (e.getSource() == add) {
                String word = aField.getText().trim().toLowerCase();
                String meaning = mField.getText().trim().toLowerCase();
                String addString = "add!" + word + ":" + meaning;
                //System.out.println(addString);
                if (word.isEmpty() || meaning.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "!!!   Please make all entries   !!!", "Failed entry validation", JOptionPane.PLAIN_MESSAGE);
                } else if (!word.chars().allMatch(Character::isLetter) || !Pattern.matches("[a-zA-Z]+", word)) {
                    JOptionPane.showMessageDialog(this, "!!!   Word entry not valid (no spaces/numbers)  !!!", "Failed entry validation", JOptionPane.PLAIN_MESSAGE);
                } else if (!Pattern.matches("((\\s*[a-zA-z]+\\s*)+\\;?)+[^\\;]", meaning)) {
                    JOptionPane.showMessageDialog(this, "!!!   Meaning not in specified format !!!", "Failed entry validation", JOptionPane.PLAIN_MESSAGE);
                } else {
                    dout.writeUTF(addString);
                    String res = (din.readUTF());
                    if (!res.equals("Word already exists!"))
                        JOptionPane.showMessageDialog(this, "Word added Successfully !!!", "Deleted", JOptionPane.PLAIN_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(this, "!!!   Word already exists in dictionary   !!!", "Already exists", JOptionPane.PLAIN_MESSAGE);
                }
            } else if (e.getSource() == searchTab) {
                cl.show(cardPanel, "search");
            } else if (e.getSource() == deleteTab) {
                cl.show(cardPanel, "delete");
            } else if (e.getSource() == addTab) {
                cl.show(cardPanel, "add");
            }
        } catch (Exception ex) {
            //System.out.println("Exception:" + ex);
            JOptionPane.showMessageDialog(this, "!!!   Server is down. Restart your app   !!!", "Server Error!", JOptionPane.PLAIN_MESSAGE);

        }

    }

    public static void main(String[] args) throws IOException {


        try {

            String hpn = JOptionPane.showInputDialog("Enter hostname:port:your_name");
            String[] hpnArr = hpn.split(":");
            if (hpnArr.length == 3) {
                InetAddress ip = InetAddress.getByName(hpnArr[0]);

                s = new Socket(ip, Integer.parseInt(hpnArr[1]));

                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());

                String serverConnectionResponse = din.readUTF();
                //System.out.println(serverConnectionResponse);
                if (!(serverConnectionResponse.equals("very busy"))) {
                    dout.writeUTF(hpnArr[2]);
                    new DClient();
                } else {
                    JOptionPane.showMessageDialog(null, "!!!   Server very busy right now. Try again later   !!!", "Server Busy", JOptionPane.PLAIN_MESSAGE);

                }

            } else {
                JOptionPane.showMessageDialog(null, "!!!   Make the entries in valid format   !!!", "Entry validation error", JOptionPane.PLAIN_MESSAGE);

            }
        } catch (Exception e) {
            //System.out.println(e);
            JOptionPane.showMessageDialog(null, "!!!   The specified hostname/port is wrong or the server is down.   !!!", "Server Error!", JOptionPane.PLAIN_MESSAGE);


        }

    }
}
