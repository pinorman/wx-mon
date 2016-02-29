package com.pinorman.wxmon.client;


import com.pinorman.wxmon.sensors.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private  TempHistory tempInside;
    private  TempHistory tempOutside;
    private  RainHistory rain;
    private Text outsideLow;
    private Text outsideHigh;
    private Text currentReading;
    static private String serverIP;
    static private int dayInterval;


    public static void main(String[] args) {
        if (args.length == 0) {
            log.info("Usage: WxClient <Local IP address>");
            System.exit(1);
        }
        serverIP = args[0];
        dayInterval = 7;
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {

        VBox root = new VBox();
        // set up button
        Button releaseButton = new Button();
        root.getChildren().add(releaseButton);
        releaseButton.setText("Refresh");
        releaseButton.setLayoutX(50);
        releaseButton.setLayoutY(10);
        releaseButton.setOnAction(event -> displayTemps());
        // setup the 3 text boxes for outside low, outside high, and current temperatures
        outsideLow = new Text("");
        root.getChildren().add(outsideLow);
        outsideHigh = new Text("");
        root.getChildren().add(outsideHigh);
        currentReading = new Text("");
        root.getChildren().add(currentReading);
        root.setMinSize(350, 250);
        Scene scene = new Scene(root);
        primaryStage.setX(100);
        primaryStage.setY(200);
        primaryStage.setMinHeight(300);
        primaryStage.setWidth(600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Temperature Monitoring System");
        primaryStage.show();
    }

    public void displayTemps() {
        LocalDateTime timeNow = LocalDateTime.now();

        getTempData(serverIP, dayInterval);
        TempReading tMax = tempOutside.getMaxTemp(timeNow.minusDays(dayInterval), timeNow);
        TempReading tMin = tempOutside.getMinTemp(timeNow.minusDays(dayInterval), timeNow);
        outsideLow.setText("Outside Low temperature over the pass week is: "
                + decForm.format(tMin.getTemp())
                + " - On " + dateTimeForm.format(tMin.getTempTime()));

        outsideHigh.setText("Outside High temperature over the pass week is: "
                + (decForm.format(tMax.getTemp()))
                + " - On " + dateTimeForm.format(tMax.getTempTime()));
        currentReading.setText("Current Outside temperature is: "
                + decForm.format(tempOutside.getCurrentTemp())
                + " -  Inside is: " + decForm.format(tempInside.getCurrentTemp()));

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
            client.getTempData(args[0]);
        }

    */
    public void getTempData(String serverIP, int days) {
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
        TempReading tMax = tempOutside.getMaxTemp(timeNow.minusDays(days), timeNow);
        TempReading tMin = tempOutside.getMinTemp(timeNow.minusDays(days), timeNow);
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