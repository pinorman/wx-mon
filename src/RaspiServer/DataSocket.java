package RaspiServer;

import sensors.TempSensorQue;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
* Created by Paul on 4/30/2014.
*/
public class DataSocket<Que> extends Thread {

    private int port;
    private Que que;

    public DataSocket(Que q, int p) {
        port = p;
        que = q;
    }

    @Override
    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting here on: " + serverSocket.getLocalPort());
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    try {
                        OutputStream outStream = socket.getOutputStream();
                        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream)) {
                            objectOutputStream.writeObject(que);
                        }
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

    private class TempThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;
        private TempSensorQue tQue;

        TempThread(Socket socket, TempSensorQue t, int c) {
            hostThreadSocket = socket;
            tQue = t;
            cnt = c;
        }

        @Override
        public void run() {

            OutputStream outputStream;
            try {
                outputStream = hostThreadSocket.getOutputStream();
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                    objectOutputStream.writeObject(tQue);
                }
            } catch (IOException ex) {
                System.out.println(" On Temp data" + ex.toString());
            } finally {
                try {
                    hostThreadSocket.close();
                } catch (IOException ex) {
                    System.out.println(ex.toString());
                }
            }
        }

    }
}
