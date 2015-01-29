package com.pinorman.raspberry.wxmon.server;


import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pinorman.raspberry.wxmon.sensors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxRaspiServer {

    private static final Logger log = LoggerFactory.getLogger(WxRaspiServer.class);
    private final static String TEMP_PROBE_ID = "28-00000514891a";
    private final static int TEMP_SCAN_INTERVAL = 1000 * 60 * 15;       // 15 minutes
    private final static int WX_PORT = 8080;
    private final GpioController gpio;
    private TempSensorHW tempProbe;
    private TempHistory tSensor;
    private RainHistory rSensor;


    private String tFile = "temperatureDB.txt";

    public WxRaspiServer() {
        tSensor = new TempHistoryImpl();
        tempProbe = new TempSensorHW(TEMP_PROBE_ID);
        rSensor = new RainHistoryImpl();

        /*
        Process the Temperature file and add to history
         */
        readTempDataBase();
        initSensors();


        gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) rSensor.incrementRain();
                // display pin state on console
                //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            }

        });
    }

    public static void main(String[] args) throws InterruptedException {
        WxRaspiServer server = new WxRaspiServer();
        server.startServer();
    }


    public void startServer() {


        ServerCommand cmd = null;
        WxCmdDataSocket<Serializable> wxSocket = new WxCmdDataSocket<>(WX_PORT);
        for (; ; ) {
            wxSocket.accept();        // wait on caller -

            log.info("waiting for command from socket");
            while ( (cmd = (ServerCommand)wxSocket.readData()) != null ) {      // Process data while we are still connected

                log.info("cmd is {}", cmd.getCommand());
                if (cmd.getCommand() == ServerCommand.CmdType.TEMPERATURE) {
                    log.info("Send temp data back");
                    wxSocket.writeData(tSensor);
                }
                if (cmd.getCommand() == ServerCommand.CmdType.RAIN) {
                    log.info("Send Rain Data back");
                    wxSocket.writeData(rSensor);
                }
            }


        }
    }

    private void initSensors() {
        DecimalFormat decForm = new DecimalFormat("##0.00");
        DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:ss.nnnnnnnnn");

        TimerTask TempScheduler = new TimerTask() {
            @Override
            public void run() {
                //       double lastTemp = tempProbe.getCurrentTemp();
                double currentTemp = tempProbe.readTemp();
                int retryCount = 0;

                LocalDateTime t = LocalDateTime.now();
                tSensor.add(new TempReading(currentTemp, t));
                StringBuilder sb = new StringBuilder();
                // build string with temp and date and a <LF>
                sb.append(decForm.format(currentTemp)).append(" ").append(dateForm.format(t)).append("\n");
                try (BufferedWriter tOut = new BufferedWriter(new FileWriter(tFile, true))) {
                    tOut.write(sb.toString());
                } catch (IOException e) {
                    log.warn("Error on Temperature file", e);
                }
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(TempScheduler, 0, TEMP_SCAN_INTERVAL);
        log.info("Raspi hardware has been started");
    }

    private void readTempDataBase() {
        double temp;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyy-MM-d H:m:ss.nnnnnnnnn");       // notice the " " in front of yyyy
        log.info("Read in any existing temperature data");
        try (Scanner s = new Scanner(new File(tFile))) {
            while (s.hasNext()) {
                temp = s.nextDouble();          // read in temp
                tSensor.add(new TempReading(temp, LocalDateTime.parse(s.nextLine(), formatter))); // read in date and add in the records we parse from the file
            }
        } catch (IOException e) {
            log.warn("Error reading Temperature file", e);
        }
    }

}
