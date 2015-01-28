package com.pinorman.raspberry.wxmon.client;


import com.pinorman.raspberry.wxmon.sensors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxClient {

    private static final Logger log = LoggerFactory.getLogger(WxClient.class);
    private final static int WX_PORT = 8080;
    private final static int RAIN_PORT = 8081;

    DecimalFormat decForm = new DecimalFormat("##0.00");
    DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:ss.nnnnnnnnn");


    private TempHistory temp;
    private RainHistory rain;


    public WxClient() {
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println("Usage: WxClient <Local IP address>");
            System.exit(1);
        }
        WxClient client = new WxClient();
        client.getAndDisplayData(args[0]);
    }

    public void getAndDisplayData(String serverIP) {
        ServerCommand cmd = new ServerCommand();
        cmd.setCommand(ServerCommand.CmdType.TEMPERATURE);
        cmd.setQuickDateEnum(ServerCommand.DateRange.LASTDAY);
        WxCmdDataSocket<Serializable> wxSocket = new WxCmdDataSocket<>(serverIP, WX_PORT);
        log.info("port is open, now send cmd");
        wxSocket.writeData(cmd);
        temp = (TempHistory) wxSocket.readData();
        cmd.setCommand(ServerCommand.CmdType.RAIN);
        log.info("Write Rain cmd, then get rain  data");
        wxSocket.writeData(cmd);
        log.info("waiting on read for rain");
        rain = (RainHistory) wxSocket.readData();

        wxSocket.close();

        log.info("Print last 24 hours worth of from history");
        TempReading tArray[] = temp.toArray();

        int len = temp.queSize();
        int begin = 0;
        if (len >= 15 * 4 * 24) begin = len - 15 * 4 * 24;      // 24 hours worth
        for (int i = begin; i < len; i++) {
            log.info("Temp is {} Time was {}", decForm.format(tArray[i].getTemp()), dateForm.format(tArray[i].getTempTime()));
        }
        log.info("Overall amount of Rain is: {}", decForm.format(rain.getRainTotal()));
        log.info("Amount of rain since there's been an 8 hour gap (when it was actually raining) is{} ", decForm.format(rain.getAccumulatedRainLevel(ChronoUnit.HOURS, 8)));
        log.info("Rate/Hour (by min & Hr): {} {}", decForm.format(rain.getRainPerHour(ChronoUnit.MINUTES)), decForm.format(rain.getRainPerHour(ChronoUnit.HOURS)));
        log.info("Last time it Rained {}", dateForm.format(rain.getLastTimeSawRain()));
        log.info("When this rain started {}", dateForm.format(rain.getWhenStartedRaining()));
//            log.info("Latest Temp {} Max {} Min {}", decForm.format(temp.getCurrentTemp()), decForm.format(temp.getMaxTemp()), decForm.format(temp.getMinTemp()));



}

    private Object readData(String ip, int port) {
        ObjectInputStream inputStream;
        Socket socket;
        Object obj = null;

        try {
            socket = new Socket(ip, port);
            inputStream = new ObjectInputStream(socket.getInputStream());
            try {
                obj = inputStream.readObject();
            } catch (ClassNotFoundException ex) {
                log.warn("Read object failed on port {}", port, ex);
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            log.warn("Socket failed on port {}", port, e);
        }
        return obj;
    }

}