/*
 * This project has been created as part of an assignment for COMP90015 assignment 1 at University of Melbourne
 * Author: Aayush Mehta
 * Student id: 1105081
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ClientThreadHandler extends Thread {
    final DataInputStream dit;
    final DataOutputStream dout;
    final Socket s;
    TreeMap<String, String> dict;
    DictionaryServer ds;
    String clientName;

    List<Socket> connList;

    public ClientThreadHandler(Socket s, TreeMap<String, String> dict, DataInputStream dit,
                               DataOutputStream dout, List<Socket> connList, DictionaryServer ds,
                               String clientName) {
        this.s = s;
        this.dit = dit;
        this.dout = dout;
        this.dict = dict;
        this.connList = connList;
        this.ds = ds;
        this.clientName=clientName;
    }


    @Override
    public void run() {
        String req, res;
        this.connList.add(s);
        this.ds.updateCounter();
        this.ds.updateServerLog("Serving "+this.clientName+" now");
        while (true) {
            try {
                req = this.dit.readUTF();
                if (req.equals("exit")) {
                    System.out.println("closing port : " + this.s);
                    break;
                }
                this.ds.updateServerLog(this.clientName+" requested: "+req);
                res = this.ds.performOperation(req);
                this.ds.updateServerLog(this.clientName+" replied with: "+res);
                this.dout.writeUTF(res);
//                String[] reqAr = req.split("!");
//                String action = reqAr[0];
//                String word = reqAr[1];
//                System.out.println(action + " " + word);
//                switch (action) {
//                    case "search":
//                        if (dict.get(word) != null)
//                            this.dout.writeUTF(dict.get(word));
//                        else this.dout.writeUTF("Word not found!");
//                        break;
//
//                    case "delete":
//                        if (dict.get(word) != null) {
//                            dict.remove(word);
//                            this.dout.writeUTF("Deleted Successfully!");
//                        } else this.dout.writeUTF("Word not found!");
//                        break;
//
//                    case "add":
//                        String[] wMeaning=word.split(":");
//                        if (dict.get(wMeaning[0]) != null) {
//                            this.dout.writeUTF("Word already exists!");
//                        } else{
//
//                            this.dict.put(wMeaning[0],wMeaning[1]);
//                            this.dout.writeUTF("Word added successfully!");
//                        }
//                        break;
//
//                }

//                if (!req.equals("exit")) {
//                    //System.out.println(dict.get("abc"));
//
//                    this.dout.writeUTF("hello");
//                    //this.dout.writeUTF("Hello to you too!");
//                } else if (req.equals("exit")) {
//                    System.out.println("closing port : " + this.s);
//                    break;
//                }

            } catch (Exception e) {
                //System.out.println(e);
                break;
            }
        }
        try {
            this.ds.updateServerLog("Disconnected "+this.clientName+" on " + s);
            this.connList.remove(s);
            this.ds.updateCounter();
            this.s.close();
            this.dit.close();
            this.dout.close();
        } catch (Exception e) {
            this.ds.updateServerLog(this.clientName+" "+e.getMessage());
            this.ds.updateServerLog("Disconnected on: " + s);
        }
    }
}
