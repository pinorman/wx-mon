package com.pinorman.wxmon.client;


import com.pinorman.wxmon.sensors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
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
    DateTimeFormatter dateTimeForm = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:ss");


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
        TempHistory tempInside;
        TempHistory tempOutside;
        RainHistory rain;

        ServerCommand cmd = new ServerCommand();
        cmd.setCommand(ServerCommand.CmdType.TEMPERATURE);
        cmd.SetSensorId("inside");                           //inside tempInside probe
        cmd.setQuickDateEnum(ServerCommand.DateRange.LASTDAY);
        WxCmdDataSocket<Serializable> wxSocket = new WxCmdDataSocket<>(serverIP, WX_PORT);
        log.info("port is open, now send cmd");
        wxSocket.writeData(cmd);
        tempInside = (TempHistory) wxSocket.readData();
        //* get the outside temp too
        cmd.SetSensorId("outside");
        wxSocket.writeData(cmd);
        tempOutside = (TempHistory) wxSocket.readData();
        //* get any rain data
        cmd.setCommand(ServerCommand.CmdType.RAIN);
        log.info("Write Rain cmd, then get rain  data");
        wxSocket.writeData(cmd);
        log.info("waiting on read for rain");
        rain = (RainHistory) wxSocket.readData();

        wxSocket.close();
        LocalDateTime timeNow = LocalDateTime.now();
        TempReading tMax = tempOutside.getMaxTemp(timeNow.minusDays(7), timeNow);
        TempReading tMin = tempOutside.getMinTemp(timeNow.minusDays(7), timeNow);
        log.info("The Low temp for the past week: {} on day {}",
                tMin.getTemp(), dateTimeForm.format(tMin.getTempTime()));
        log.info("The high temp for the past week: {} on day {}",
                tMax.getTemp(), dateTimeForm.format(tMax.getTempTime()));
//        TempReading tArray[] = tempInside.toArray();


        log.info("Overall amount of Rain is: {}", decForm.format(rain.getRainTotal()));
        log.info("Amount of rain since there's been an 8 hour gap (when it was actually raining) is{} ", decForm.format(rain.getAccumulatedRainLevel(ChronoUnit.HOURS, 8)));
        log.info("Rate/Hour (by min & Hr): {} {}", decForm.format(rain.getRainPerHour(ChronoUnit.MINUTES)), decForm.format(rain.getRainPerHour(ChronoUnit.HOURS)));
        log.info("Last time it Rained {}", dateForm.format(rain.getLastTimeSawRain()));
        log.info("When this rain started {}", dateForm.format(rain.getWhenStartedRaining()));

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
