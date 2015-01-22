package RaspiServer;


import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sensors.*;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxRaspiServer {

    private static final Logger log = LoggerFactory.getLogger(WxRaspiServer.class);
    private static int QUE_DEPTH = 1000;
    private final static String TEMP_PROBE_ID = "28-00000514891a";
    private final static int TEMP_SCAN_INTERVAL = 1000 * 60 * 15;       // 15 minutes
    private final static int TEMP_PORT = 8080;
    private final static int RAIN_PORT = 8081;
    private final GpioController gpio;
    private TempSensorHW tempProbe;
    private TempSensor tSensor;
    private RainSensor rSensor;


    public WxRaspiServer() {
        tSensor = new TempSensorImpl();
        tempProbe = new TempSensorHW(TEMP_PROBE_ID);
        rSensor = new RainSensorHistory();

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


        TimerTask TempScheduler = new TimerTask() {
            @Override
            public void run() {
                //       double lastTemp = tempProbe.getCurrentTemp();
                double currentTemp = tempProbe.readTemp();
                int retryCount = 0;
                /*
                * Sometimes the temp returned is not correct.
                * Look for a temp with a difference of more than 10 degrees (likely a problem)
                * retry until we get a good temp or fail by not adding temperature (this time)
                 */
                /*
                while ((Math.abs(lastTemp - currentTemp) >= 10.0) && retryCount < 10) {
                    currentTemp = tempProbe.readTemp(); // try again
                    retryCount++;
                }
                if (retryCount == 10) return; // Temp sensor didn't read correctly - get out until next time
                */
                tSensor.add(new TempReading(currentTemp, LocalDateTime.now()));
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(TempScheduler, 0, TEMP_SCAN_INTERVAL);
        log.info("Raspi hardware has been started");

        DataSocket<TempSensor> tSocket = new DataSocket<>(tSensor, TEMP_PORT);
        DataSocket<RainSensor> rSocket = new DataSocket<>(rSensor, RAIN_PORT);
        tSocket.start();
        rSocket.start();
        for (; ; ) {
            try {
                Thread.sleep(1000 * 60 * 60); //should be an hour
            } catch (Exception ex) {
                log.warn("", ex);
            }
        }
    }

}
