/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServidorClass;

import com.sun.xml.internal.bind.v2.schemagen.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gondi
 */
public class ServidorH extends Thread{
    private static Map<String, PrintStream> client = new HashMap<String, PrintStream>();
    private static List<String> nameList = new ArrayList<String>(); 
    private Socket connection;
    Thread t;
    private String nameClient;
    private boolean flagConnection = true;
    
    public ServidorH(){}
    
    public ServidorH(Socket socket){
        this.connection = socket;
    }
    
    public boolean store(String newName){
        for(int i = 0; i < nameList.size(); ++i){
            if(nameList.get(i).equals(newName))
                return true;
        }
        nameList.add(newName);
        return false;
    }
    
    public void remove(String oldName){
        for(int i = 0; i < nameList.size(); ++i){
            if(nameList.get(i).equals(oldName))
                nameList.remove(oldName);
        }
    }
    
    public void connection(int port){
        try {
            ServerSocket server = new ServerSocket (port);
            System.out.println("Server running on the port " + port + "\r\n");
            while(this.flagConnection){
                Socket Connection = server.accept();
                t = new ServidorH(Connection);
                t.start();
            }
            this.flagConnection = true;
        } catch (IOException ex) {
            Logger.getLogger(ServidorH.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    @Override
    public void run(){
        try {
            BufferedReader input;
            input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            PrintStream output = new PrintStream(this.connection.getOutputStream());
            this.nameClient = input.readLine();
            
            if(store(this.nameClient)){
                output.println("This name already exists! Connect again with another Name.");
                this.connection.close();
                return;
            } else {
                System.out.println(this.nameClient + ": Connected to Server!\r\n");
                //envia para o novo cliente a lista com todos que estão conectados
                output.println("Connected: " + nameList.toString());
            }
            
            if(this.nameClient == null){
                return;
            }
            
            client.put(this.nameClient,output);
            String[] msg = input.readLine().split(":");
            while(!(msg[0].trim().equals("")) && msg != null){
                send(output, " send: ", msg);
                msg = input.readLine().split(":");
            } 
            System.out.println(this.nameClient + " Left chat!" + "\r\n");
            String[] out = {" Of the chat!"};
            send(output," leave", out);
            remove(this.nameClient);
            client.remove(this.nameClient);
            this.connection.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public void disconnect(){
        try {
            System.out.println("Server Disconnected\r\n");
            this.connection.close();
            this.flagConnection = false;
            client.clear();
            this.nameClient = "";
            nameList.clear();
        } catch (IOException ex) {
            Logger.getLogger(ServidorH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    
    public void send(PrintStream output, String action, String[] msg){
        out:
        for(Map.Entry<String, PrintStream> client: client.entrySet()){
            PrintStream chat = client.getValue();
            if(chat != output){
                if(msg.length == 1){
                    chat.println(this.nameClient + action + msg[0]);
                    //chat.println(msg[0]);
                } else {
                    if(msg[1].equalsIgnoreCase(client.getKey())){
                        chat.println(this.nameClient + action + msg[0]);
                        //chat.println(msg[0]);
                        break out;
                    }
                }
            }
        }
    }
}
