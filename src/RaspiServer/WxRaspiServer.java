package RaspiServer;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import sensors.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxRaspiServer {

    public static void main(String[] args) throws InterruptedException {
        final GpioController gpio = GpioFactory.getInstance();
        RainSensor wxStation = new RainSensorImpl(gpio);

        String tProbeId = "28-00000514891a";
        TempSensorQue tempQue = new TempSensorQue(1000);
        TempSensor tProbe = new TempSensorImpl(tProbeId);
        DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

        TimerTask TempScheduler = new TimerTask() {
            @Override
            public void run() {
                tempQue.add(new Temperature(tProbe.readTemp(), LocalDateTime.now()));
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(TempScheduler, 0, 1000 * 60 * 10);    // 10 min
        System.out.println("Raspi hardware has been started");
        int count = 0;
        //hard code to use port 8080
        DataSocket tSocket = new DataSocket<TempSensorQue> (tempQue, 8080);
        DataSocket rSocket = new DataSocket<RainSensorImpl>((RainSensorImpl) wxStation, 8081);
        tSocket.start();
        rSocket.start();
        for (; ; )
            Thread.sleep(1000*60*60); //should be an hour
    }

}
