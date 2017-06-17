/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServidorMenssageJAVA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gondi
 */
public class servidor extends Thread
{
    private static ArrayList<BufferedWriter>clientes;           
    private static ServerSocket server; 
    private String nome;
    private Socket port;
    private InputStream input;  
    private InputStreamReader inputR;  
    private BufferedReader bufferR; 
    
    /*
        Construtor da classe  
    */
    public servidor (){}
    public servidor (Socket port) 
    {
        this.port = port;
        try 
        {
            input = port.getInputStream();
            inputR = new InputStreamReader(input);
            bufferR = new BufferedReader(inputR);
        } catch (IOException ex) 
        {
            Logger.getLogger(servidor.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public void conectar(int port){
        try {
            server = new ServerSocket(port);
            clientes = new ArrayList<BufferedWriter>();
            System.out.println("Servidor on....\r\n in ->" + InetAddress.getLocalHost().getHostName() + "\r\n");
            while(true){
                System.out.println("Aguardando conexão... \r\n");
                Socket socket = server.accept();
                System.out.println(InetAddress.getLocalHost().getHostName() +" -> "+ "Cliente conectado... \r\n");
                Thread t = new servidor(socket);
                t.run();
            }
        } catch (IOException ex) {
            Logger.getLogger(servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void desconectar(){
        try {
            clientes.clear();
            server.close();
            port.close();
            input.close();
            inputR.close();
            bufferR.close();
        } catch (IOException ex) {
            Logger.getLogger(servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /*
        Metodo de execução  
    */
    @Override
    public void run()
    {   
        try 
        {
            String msg;
            OutputStream out;
            out = this.port.getOutputStream();
            Writer outWrite = new OutputStreamWriter(out);
            BufferedWriter bufferW = new BufferedWriter(outWrite);
            clientes.add(bufferW);
            nome = msg = bufferR.readLine();
            
            while(!"Sair".equalsIgnoreCase(msg) && msg != null)
            {
                msg = bufferR.readLine();
                sendToAll(bufferW,msg);
                System.out.println(msg);
            }   
        } catch (IOException ex) 
        {
            Logger.getLogger(servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
        Metodo para enviar as menssagens para todos conectados.  
    */
    public void sendToAll(BufferedWriter bSaida, String msg) throws IOException
    {
        BufferedWriter bufferWS;
        
        for(BufferedWriter bufferW : clientes)
        {
            bufferWS = (BufferedWriter)bufferW;
            if(!(bSaida == bufferWS))
            {
                bufferW.write(nome + " -> " + msg + "\r\n");
                System.out.println(nome + " -> " + msg + "\r\n");
                bufferW.flush();
            }
        }
    }
}
