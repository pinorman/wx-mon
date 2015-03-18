package com.pinorman.raspberry.wxmon.sensors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxCmdDataSocket<Q extends Serializable> {

    private static final Logger log = LoggerFactory.getLogger(WxCmdDataSocket.class);
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
            log.warn("", ex);
        }
    }

    public void close()  {
        try {
            socket.close();
        } catch (IOException e) {
            log.warn("", e);
        }
    }

    public void accept() {
        try {
            socket = serverSocket.accept();

        } catch (IOException e) {
            log.warn("", e);
        }
    }

    public boolean stillConnected() {
        return (serverSocket.isBound());
    }


    public void writeData(Q D) {
        try {

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(D);

        } catch (IOException ex) {
            log.warn("", ex);
        }
    }

    public Q readData() {
        Q data = null;
        try {
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            data = (Q) inStream.readObject();
        } catch (IOException ex) {
            log.warn("", ex);
//            return (null);
        } catch (ClassNotFoundException e) {
            log.warn("", e);
        }
        return (data);
    }
}

