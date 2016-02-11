package com.pinorman.wxmon.sensors;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RainHistoryImplTest {

    @Test
    public void testIncrementRain() throws Exception {
        String fileName = "testRain.txt";
        File file = new File(fileName);
        if (file.isFile()) file.delete();        // in case is was around from before
        RainHistoryImpl r = new RainHistoryImpl(fileName);
        r.incrementRain();
        r.incrementRain();
        Assert.assertEquals("IncrementRain: incorrect rain amount", 2 * .01, r.getRainTotal(), .001); // .01
        /* now instantiate another rain
         We should see the same amount from the file created above
          */
        RainHistoryImpl newR = new RainHistoryImpl(fileName);
        if (file.isFile()) file.delete();        // get rid of it before the assert
        Assert.assertEquals("IncrementRain: incorrect rain amount from file", 2 * .01, newR.getRainTotal(), .001); // .01

    }

    @Test
    public void testIncrementRainWithTime() throws Exception {
        RainHistoryImpl r = new RainHistoryImpl();
        LocalDateTime baseTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        r.incrementRain(baseTime);
        r.incrementRain(baseTime.plusSeconds(1));
        Assert.assertEquals("IncrementRainWithTime failed on First increment", 0, r.getWhenStartedRaining().getSecond());
        Assert.assertEquals("IncrementRainWithTime Failed on following increment", 1, r.getLastTimeSawRain().getSecond());
    }

    @Test
    public void testGetRainPerHour() throws Exception {
        RainHistoryImpl r = new RainHistoryImpl();
        List<LocalDateTime> times = new ArrayList<>();
        LocalDateTime time;      // 0 seconds is the important part
        int accumRain = 0;
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 60; i++) {
                times.add(LocalDateTime.of(2000, 1, 1, 1, j, i));
                accumRain++;
            }
        }
        for (LocalDateTime t : times) {
            r.incrementRain(t);
        }
        // accumRain over 10 min  = accumRain/10 min
        // 600/10 =
        Assert.assertEquals("Rain per hour incorrect ", (accumRain * .01) / (10 * 60) * 60,
                r.getRainPerHour(ChronoUnit.MINUTES), 0.01);
    }

    @Test
    public void testGetRainTotal() throws Exception {
        RainHistoryImpl r = new RainHistoryImpl();
        r.incrementRain();
        r.incrementRain();
        Assert.assertEquals("GetRainTotal amount", 2 * .01, r.getRainTotal(), .001); //
    }

    @Test
    public void hoursBeenRaining() throws Exception {
        RainHistoryImpl r = new RainHistoryImpl();
        LocalDateTime time = LocalDateTime.of(2000, 1, 1, 0, 0, 0); //base time
        r.incrementRain(time);
        r.incrementRain(time.plusHours(2));
        Assert.assertEquals("hoursBeenRaining amount", 2, r.hoursBeenRaining(), 0); //
        List<LocalDateTime> times = new ArrayList<>();
        for (int i = 5; i < 16; i++) {      // create a gap - add some rain
            times.add(time.plusHours(i));   // Add a reading every hour
        }
        for (LocalDateTime tt : times) {
            r.incrementRain(tt);
        }
        Assert.assertEquals("hoursBeenRaining with a gap wrong", 10, r.hoursBeenRaining(), 0);
    }

    @Test
    public void getLastTimeSawRained() throws Exception {
        RainHistoryImpl r = new RainHistoryImpl();
        LocalDateTime t = r.getLastTimeSawRain();        // result should be MIN time.
        boolean testPass = t.getHour() == 23 && t.getMinute() == 59 && t.getSecond() == 59;
        Assert.assertEquals("getLastTimeRained failed with no rain", true, testPass);
        List<LocalDateTime> times = new ArrayList<>();
        LocalDateTime time = LocalDateTime.of(2000, 1, 1, 0, 0, 5); //base time
        for (int i = 1; i < 10; i++) {
            times.add(time.plusHours(i));   // Add a reading every hour - seconds fixed at 5
        }
        for (LocalDateTime tt : times) {
            r.incrementRain(tt);
        }
        Assert.assertEquals("getLastTimeRained should match: ", 9, r.getLastTimeSawRain().getHour());
    }

    @Test
    public void getWhenStartedRaining() throws Exception {
        RainHistoryImpl r = new RainHistoryImpl();
        LocalDateTime t = r.getWhenStartedRaining();        // result should be MIN time.
        boolean testPass = t.getHour() == 0 && t.getMinute() == 0 && t.getSecond() == 0;
        Assert.assertEquals("getWhenStartedRaining failed with no rain", true, testPass);
        List<LocalDateTime> times = new ArrayList<>();
        LocalDateTime time = LocalDateTime.of(2000, 1, 1, 0, 0, 5); //base time
        for (int i = 0; i < 10; i++) {
            times.add(time.plusHours(i));   // Add a reading every hour - seconds fixed at 5
        }
        for (LocalDateTime tt : times) {
            r.incrementRain(tt);
        }
        time = LocalDateTime.of(2000, 1, 2, 0, 0, 20);  //Add another - gap is a day from previous
        for (int i = 0; i < 10; i++) {
            times.add(time.plusSeconds(i));   // Add a reading every second starting at 20
        }
        for (LocalDateTime tt : times) {
            r.incrementRain(tt);
        }
        Assert.assertEquals("getWhenStartedRaining date is incorrect ", 20, r.getWhenStartedRaining().getSecond(), 0);
    }
}
