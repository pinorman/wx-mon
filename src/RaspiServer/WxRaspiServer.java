package RaspiServer;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import sensors.*;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxRaspiServer {

    private final static String TEMP_PROBE_ID = "28-00000514891a";
     private final static int TEMP_SCAN_INTERVAL = 1000 * 60 * 10;       // 10 minutes
    private final static int TEMP_PORT = 8080;
    private final static int RAIN_PORT = 8081;
    private final GpioController gpio;
    private TempSensor tempProbe;
    private RainSensor wxStation;

    public WxRaspiServer() {

        tempProbe = new TempSensorImpl(TEMP_PROBE_ID);
        gpio = GpioFactory.getInstance();
        wxStation = new RainSensorImpl(gpio);
    }

    public static void main(String[] args) throws InterruptedException {
        WxRaspiServer server = new WxRaspiServer();
        server.startServer();


    }

    public void startServer() {

        TimerTask TempScheduler = new TimerTask() {
            @Override
            public void run() {
                tempProbe.add(new TempReading(tempProbe.readTemp(), LocalDateTime.now()));
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(TempScheduler, 0, TEMP_SCAN_INTERVAL);
        System.out.println("Raspi hardware has been started");

        DataSocket<TempSensor> tSocket = new DataSocket<>(tempProbe, TEMP_PORT);
        DataSocket<RainSensor> rSocket = new DataSocket<>(wxStation, RAIN_PORT);
        tSocket.start();
        rSocket.start();
        for (; ; )
            try {
                Thread.sleep(1000 * 60 * 60); //should be an hour
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
    }

}
