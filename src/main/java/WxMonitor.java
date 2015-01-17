import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import sensors.*;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Paul on 4/17/2014.
 */
public class WxMonitor {
    public static void main(String[] args) throws InterruptedException {
        String tProbeId = "28-00000514891a";
        TempSensor tProbe = new TempSensorImpl(tProbeId);
        final TempSensorHistory tempQue = new TempSensorHistory(1000);
        tempQue.add(new TempReading(tProbe.readTemp()));
        TimerTask TempScheduler = new TimerTask() {
            @Override
            public void run() {
                tempQue.add(new TempReading(tProbe.readTemp(), LocalDateTime.now()));
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(TempScheduler, 0, 1000 * 60 * 10);    // 10 min


        final GpioController gpio = GpioFactory.getInstance();
        RainSensor wxStation = new RainSensorImpl(gpio);
        DecimalFormat decForm = new DecimalFormat("##0.00");
        DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");
        // keep program running until user aborts (CTRL-C)
        for (; ; ) {
            System.out.println("");
            Thread.sleep(1000 * 60 * 5);    // sleep for 5 min
            System.out.println("Rain level is: " + decForm.format(wxStation.getRainLevel()) +
                    " Rate/Hour (by min & Hr): " +
                    decForm.format(wxStation.getRainPerHour(ChronoUnit.MINUTES)) + " " +
                    decForm.format(wxStation.getRainPerHour(ChronoUnit.HOURS)));
            System.out.println("Last time it Rained " + dateForm.format(wxStation.getLastTimeSawRain()));
            System.out.println("When this rain started " + dateForm.format(wxStation.getWhenStartedRaining()));
            System.out.println("Latest Temp " + decForm.format(tempQue.getCurrentTemp()) +
                    " Max " + decForm.format(tempQue.getMaxTemp()) +
                    " Min " + decForm.format(tempQue.getMinTemp()));
            System.out.println("Now print out last 50");
            TempReading tArray[] = tempQue.toArray();
            int len = tempQue.queSize();
            int begin = 0;
            if (len >= 50) begin = len - 50;
            for (int i = begin; i < len; i++) {
                System.out.println("Index "+ i + " Temp is " + decForm.format(tArray[i].getTemp()) +
                        " Time was " + dateForm.format(tArray[i].getTempTime()));
            }
        }
    }
}
