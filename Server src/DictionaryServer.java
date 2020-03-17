/*
 * This project has been created as part of an assignment for COMP90015 assignment 1 at University of Melbourne
 * Author: Aayush Mehta
 * Student id: 1105081
 */
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DictionaryServer extends JFrame implements ActionListener {

    public static int port;
    public static List<Socket> connList;
    public static ServerSocket ss = null;
    public static JSONObject jo;
    public static TreeMap<String, String> dict ;
    public static int maxThreadCount = 3;
    public static String fileName;

    public static JLabel cCounter;
    public JLabel maxAllowed;
    public JLabel setMaxLimit;

    public JTextField setMaxLimitTf;

    public static JTextArea serverLog;

    public JButton setMaxBtn;

    public JPanel sl;

    public JScrollPane scrollPane;


    public DictionaryServer() {

        cCounter = new JLabel("Current no. of connections: " + String.valueOf(0));
        maxAllowed = new JLabel("Maximum connections: " + (maxThreadCount));
        setMaxLimit = new JLabel("Set max connections to: ");

        setMaxLimitTf = new JTextField(5);

        serverLog = new JTextArea(530, 350);

        setMaxBtn = new JButton("Change Limit");

        cCounter.setBounds(375, 20, 200, 50);
        maxAllowed.setBounds(375, 40, 200, 50);
        setMaxLimit.setBounds(375, 60, 200, 50);
        setMaxLimitTf.setBounds(375, 100, 40, 20);
        setMaxBtn.setBounds(425, 100, 100, 20);
        serverLog.setLineWrap(true);
        serverLog.setAutoscrolls(true);
        serverLog.setEditable(false);

        scrollPane = new JScrollPane(serverLog);
        scrollPane.setBounds(20, 20, 350, 530);

//        sl= new JPanel();
//        sl.add(scrollPane);
//        sl.setBounds(20,20,200,50);

        setMaxBtn.addActionListener(this);

        add(cCounter);
        add(maxAllowed);
        add(setMaxLimit);
        add(setMaxLimitTf);
        add(setMaxBtn);
        add(scrollPane);
        //add(sl);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                terminate();
            }
        });
        setSize(600, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setVisible(true);
        setTitle("Dictionary server hosted on: " + ss);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == setMaxBtn) {
            int newLimit = Integer.parseInt(setMaxLimitTf.getText());
            if (newLimit >= connList.size()) {
                maxThreadCount = newLimit;
                maxAllowed.setText("Maximum connections: " + (maxThreadCount));
                updateServerLog("Client limit changed to : " + maxThreadCount);
            } else {
                JOptionPane.showMessageDialog(this, "!!!   Value should be greater than or equal to active connections   !!!", "Limit Error!", JOptionPane.PLAIN_MESSAGE);

            }
        }
    }

    public static void terminate() {
        try {
            FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/logs/logFile_" + new Date().toString()+".txt");
            file.write(serverLog.getText());
            file.flush();
            file.close();
            updateServerLog("Log file saved successfully");
        } catch (Exception e) {
            updateServerLog("Cannot save log  file");
        }
        System.exit(0);
    }

    synchronized public static void updateServerLog(String msg) {
        serverLog.setText(serverLog.getText() + "\n" + new Date().toString() + " : " + msg);
    }

    synchronized public static void updateCounter() {
        cCounter.setText("Current no. of connections: " + String.valueOf(connList.size()));
    }

    synchronized public static String performOperation(String req) {

        String res = null;
        String[] reqAr = req.split("!");
        String action = reqAr[0];
        String word = reqAr[1];

        switch (action) {
            case "search":
                if (dict.get(word) != null)
                    res = (dict.get(word));
                else res = "Word not found!";
                break;

            case "delete":
                if (dict.get(word) != null) {
                    dict.remove(word);
                    jo.remove(word);
                    res = "Deleted Successfully!";
                } else res = "Word not found!";
                break;

            case "add":
                String[] wMeaning = word.split(":");
                if (dict.get(wMeaning[0]) != null) {
                    res = "Word already exists!";
                } else {
                    dict.put(wMeaning[0], wMeaning[1]);
                    jo.put(wMeaning[0], wMeaning[1]);
                    res = "Word added successfully!";
                }
                break;

        }
        if ((action.equals("add") || action.equals("delete")) && (! ((res.equals("Word already exists!")) || res.equals("Word not found!")) ) ) {
            try {
                FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/"+fileName);
                file.write(jo.toJSONString());
                file.flush();
                file.close();
                updateServerLog("File updated successfully");
            } catch (Exception e) {
                updateServerLog("Cannot write to the file");
            }
        }
//        try {
//            Thread.sleep(2000);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }

        return res;
    }

    public static void main(String[] args) {
        dict= new TreeMap<>();

        try {
            String pf = JOptionPane.showInputDialog("Enter port number:dictionary_file_name ");
            String pfar[]=pf.split(":");
            if(!(pfar.length==2)){
                JOptionPane.showMessageDialog(null, "!!!   Make entries in proper format   !!!", "Input error!", JOptionPane.PLAIN_MESSAGE);
                System.exit(0);
            }
            fileName = pfar[1];
            port=Integer.parseInt(pfar[0]);
            Object obj = new JSONParser().parse(new FileReader(new File("").getAbsolutePath() + "/"+fileName));
            jo = (JSONObject) obj;
            for (Object k : jo.keySet()) {
                dict.put((String) k, (String) jo.get(k));
                //System.out.println(k+" : "+ jo.get(k));
            }


        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "!!!   Cannot load empty/invalid file   !!!", "File load error!", JOptionPane.PLAIN_MESSAGE);

            //System.out.println("Can't read empty file!!! " + e);
            System.exit(0);
        }
        try {
            connList = new ArrayList<>();
            ss = new ServerSocket(port);
            DictionaryServer ds = new DictionaryServer();
            serverLog.setText(new Date().toString() + " : Server started successfully!");
            while (true) {
                Socket s = null;
                try {

                    s = ss.accept();
                    updateServerLog("Client Requested!! Counter: " + (connList.size() + 1));
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                    if ((connList.size()) >= maxThreadCount) {
                        dout.writeUTF("very busy");
                        updateServerLog("Server very busy to accept any more connections");
                    } else {
                        dout.writeUTF("Connection succesfull!");
                        String clientName = dis.readUTF();
                        cCounter.setText("Current no. of connections: " + String.valueOf(connList.size()));
                        updateServerLog("Connection established with " + clientName + " on: " + s);
                        Thread clientThread = new ClientThreadHandler(s, dict, dis, dout, connList, ds, clientName);
                        clientThread.start();
                    }


                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (Exception e) {
            //System.out.println(e + "" + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Port error!", JOptionPane.PLAIN_MESSAGE);

        }


    }


}
