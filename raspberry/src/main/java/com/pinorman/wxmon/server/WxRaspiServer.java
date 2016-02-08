package com.pinorman.wxmon.server;


import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pinorman.wxmon.sensors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxRaspiServer {

    private static final Logger log = LoggerFactory.getLogger(WxRaspiServer.class);
    private final static String TEMP_SENSOR_OUTSIDE_ID = "28-00000514891a";
    private final static String TEMP_SENSOR_INSIDE_ID = "28-031554406eff";
    private final static int TEMP_SCAN_INTERVAL = 1000 * 60 * 15;       // 15 minutes
    private final static int WX_PORT = 8080;
    private final GpioController gpio;

    private TempSensorHW tempOutsideProbe;
    private TempSensorHW tempInsideProbe;
    private RainHistory rSensor;
    private static String tOutsideFile = "temperatureOutsideDB.txt";
    private static String tInsideFile = "temperatureInsideDB.txt";
    private TempHistory tOutsideSensor = new TempHistoryImpl(tOutsideFile);
    private TempHistory tInsideSensor = new TempHistoryImpl(tInsideFile);

    public WxRaspiServer() {
        /*
        Process the Temperature file(s) and add to history(s)
         */
        // Hardware temperature probes defined
        tempInsideProbe = new TempSensorHW(TEMP_SENSOR_INSIDE_ID);
        tempOutsideProbe = new TempSensorHW(TEMP_SENSOR_OUTSIDE_ID);
        // temperature history created

        rSensor = new RainHistoryImpl();
// at some point we will read in raindb as well

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
                    wxSocket.writeData(tOutsideSensor);
                }
                if (cmd.getCommand() == ServerCommand.CmdType.RAIN) {
                    log.info("Send Rain Data back");
                    wxSocket.writeData(rSensor);
                }
            }


        }
    }

    private void initSensors() {
        TimerTask TempScheduler = new TimerTask() {
            @Override
            public void run() {

                tOutsideSensor.add(tempOutsideProbe.read());
                tInsideSensor.add(tempInsideProbe.read());
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(TempScheduler, 0, TEMP_SCAN_INTERVAL);
        log.info("Raspi hardware has been started");
    }


}
