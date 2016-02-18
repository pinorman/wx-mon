package com.pinorman.wxmon.client;


import com.pinorman.wxmon.sensors.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxClient extends Application {
    private static final Logger log = LoggerFactory.getLogger(WxClient.class);
    private final static int WX_PORT = 8080;
    private final static int RAIN_PORT = 8081;

    private static DecimalFormat decForm = new DecimalFormat("##0.00");
    private static DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:ss.nnnnnnnnn");
    private static DateTimeFormatter dateTimeForm = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:ss");

    private static TempHistory tempInside;
    private static TempHistory tempOutside;
    private static RainHistory rain;


    private double temp = 10.00;
    String temperatureLine;

    public static void main(String[] args) {
        if (args.length == 0) {
            log.info("Usage: WxClient <Local IP address>");
            System.exit(1);
        }
        getAndDisplayData(args[0]);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LocalDateTime timeNow = LocalDateTime.now();
        TempReading tMax = tempOutside.getMaxTemp(timeNow.minusDays(3), timeNow);
        TempReading tMin = tempOutside.getMinTemp(timeNow.minusDays(3), timeNow);

        Text text = new Text("Outside Low temperature is: ".concat(decForm.format(tMin.getTemp()))
                .concat("; Outside High is: ").concat(decForm.format(tMax.getTemp())));
        VBox root = new VBox();
        root.getChildren().add(text);
        Text nextLine = new Text("Current Outside temperature is: ".concat(decForm.format(tempOutside.getCurrentTemp()))
                .concat("; Inside is: ").concat(decForm.format(tempInside.getCurrentTemp())));
        root.getChildren().add(nextLine);
        root.setMinSize(350, 250);
        Scene scene = new Scene(root);
        primaryStage.setX(100);
        primaryStage.setY(200);
        primaryStage.setMinHeight(300);
        primaryStage.setWidth(400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Your first JavaFX Example");
        primaryStage.show();
    }


    /*
        public class WxClient {


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

    */
    public static void getAndDisplayData(String serverIP) {
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
        TempReading tMax = tempOutside.getMaxTemp(timeNow.minusDays(3), timeNow);
        TempReading tMin = tempOutside.getMinTemp(timeNow.minusDays(3), timeNow);
        log.info("The Low temp - 3 days: {} on {}",
                decForm.format(tMin.getTemp()), dateTimeForm.format(tMin.getTempTime()));
        log.info("The high temp - 3 days: {} on {}",
                decForm.format(tMax.getTemp()), dateTimeForm.format(tMax.getTempTime()));
        log.info("Temp now outside: {} and inside {}",
                decForm.format(tempOutside.getCurrentTemp()),
                decForm.format(tempInside.getCurrentTemp()));
//        TempReading tArray[] = tempInside.toArray();

/*
        log.info("Overall amount of Rain is: {}", decForm.format(rain.getRainTotal()));
        log.info("Amount of rain since there's been an 8 hour gap (when it was actually raining) is{} ", decForm.format(rain.getAccumulatedRainLevel(ChronoUnit.HOURS, 8)));
        log.info("Rate/Hour (by min & Hr): {} {}", decForm.format(rain.getRainPerHour(ChronoUnit.MINUTES)), decForm.format(rain.getRainPerHour(ChronoUnit.HOURS)));
        log.info("Last time it Rained {}", dateForm.format(rain.getLastTimeSawRain()));
        log.info("When this rain started {}", dateForm.format(rain.getWhenStartedRaining()));
*/

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