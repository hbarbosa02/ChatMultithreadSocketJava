/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientMenssageJava;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


/**
 *
 * @author gondi
 */
public class client {
    
    private Socket socket;
    private OutputStream output ;
    private Writer outputW; 
    private BufferedWriter bufferW;
    String nome;
    
    public client(){}
    
    public void setNome(String nome){this.nome = nome;}
    
    public void conectar(String Ip, int Port){
        try {
            socket = new Socket(Ip, Port);
            output = socket.getOutputStream();
            outputW = new OutputStreamWriter(output);
            bufferW = new BufferedWriter(outputW);
            bufferW.write(this.nome+"\r\n");
            bufferW.flush();
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void enviarMensagem(String msg){
        try {
            if(msg.equals("Sair")){
                bufferW.write("Desconectado \r\n");
            } else {
                bufferW.write(msg+"\r\n");
            }
            bufferW.flush();
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
