package com.pinorman.wxmon.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Paul on 4/30/2014.
 */
public class WriteDataSocket<Q extends Serializable> extends Thread {

    private int port;
    private Q que;

    public WriteDataSocket(Q q, int p) {
        port = p;
        que = q;
    }

    @Override
    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    try {
                        OutputStream outStream = socket.getOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
                        objectOutputStream.writeObject(que);

                    } catch (IOException ex) {
                        System.out.println(ex.toString());
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            System.out.println(ex.toString());
                        }
                    }
                } catch (IOException ex) {
                    System.out.println(ex.toString());
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

}
