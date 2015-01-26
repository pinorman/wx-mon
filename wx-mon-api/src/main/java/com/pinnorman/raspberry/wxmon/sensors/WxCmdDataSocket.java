package com.pinnorman.raspberry.wxmon.sensors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxCmdDataSocket<Q extends Serializable> {

    private Socket socket;
    private ServerSocket serverSocket;


    /*
    * Constructor creates the socket as a listener
    * Create the Server socket side
    *
     */
    public WxCmdDataSocket(int p) {
        try {
            serverSocket = new ServerSocket(p);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
/*
 * Constructor used to initiate a socket connection
 * Input is the ip address and port
 * makes the connection
 * port is open for read/write
 */
    public WxCmdDataSocket(String ip, int p) {

        try {
            socket = new Socket(ip, p);
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
    public void waitOnAccept() {
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCmd( ServerCommand cmd ) throws IOException {
        ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
        try {
            outStream.writeObject(cmd);

        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

    }

    public ServerCommand readCmd() {
        ServerCommand cmd = null;
        try {
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            cmd = (ServerCommand) inStream.readObject();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return (cmd);
    }

    public void writeData(Q D) {
        try {

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(D);

        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public Q readData() {
        Q D = null;
        try {
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            D = (Q) inStream.readObject();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    return( D );
    }
}

