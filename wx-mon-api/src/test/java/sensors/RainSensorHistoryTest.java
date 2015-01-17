package sensors;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RainSensorHistoryTest {

    @Test
    public void testIncrementRain() throws Exception {
        RainSensorHistory r = new RainSensorHistory();
        r.incrementRain();
        r.incrementRain();
        Assert.assertEquals("IncrementRain: incorrect rain amount", 2 * .01, r.getRainLevel(), .01); // .01
    }

    @Test
    public void testIncrementRainWithTime() throws Exception {
        RainSensorHistory r = new RainSensorHistory();
        r.incrementRain();
        r.incrementRain( LocalDateTime.now());
        long interval = r.getWhenStartedRaining().until(r.getLastTimeSawRain(), ChronoUnit.NANOS);
        Assert.assertEquals("IncrementRain(t): incorrect rain at t",1.5E9 , interval, 0.5E9 ); //
    }

    @Test
    public void testGetRainPerHour() throws Exception {
        RainSensorHistory r = new RainSensorHistory();
        // check for no rain
        Assert.assertEquals("No Rain --> Rain per hour incorrect ", 0.0, r.getRainPerHour(ChronoUnit.MINUTES), 0.01);
        r.incrementRain();
        Thread.sleep(1000);
        r.incrementRain();
        Assert.assertEquals("Rain per hour incorrect; too little time for calc ", 0.0, r.getRainPerHour(ChronoUnit.MINUTES), 0.01);
        for (int i = 0; i < 61; i++) {
            Thread.sleep(1000);
            r.incrementRain();
            // r.incrementRain();
        }
        // - we put in more than a minutes worth, but it should still be 60 seconds apart for the cal
        //  so 63  increments (inclusive) over 62 seconds (approx)
        Assert.assertEquals("Rain per hour incorrect ",  ((63.0 * .01) / 62.0) * 3600,
                r.getRainPerHour(ChronoUnit.MINUTES), 0.1);

    }

    @Test
    public void testGetRainLevel() throws Exception {

    }

    @Test
    public void getLastTimeSawRained() throws Exception {
        RainSensorHistory r = new RainSensorHistory();
        LocalDateTime t = r.getLastTimeSawRain();        // result should be MIN time.
        boolean testPass = t.getHour() == 23 && t.getMinute() == 59 && t.getSecond() == 59;
        Assert.assertEquals("getLastTimeRained failed with no rain", true, testPass);
        r.incrementRain();
        r.incrementRain();
        t = LocalDateTime.now();
        r.incrementRain();
        int rainSec = r.getLastTimeSawRain().getSecond();
        int testSec = t.getSecond();
        Assert.assertEquals("getLastTimeRained is incorrect ", testSec, rainSec, 1);
        Thread.sleep(2000);
        r.incrementRain();
        rainSec = r.getLastTimeSawRain().getSecond();
        Assert.assertNotEquals("getLastTimeRained should not match: ", testSec, rainSec);

    }

    @Test
    public void getWhenStartedRaining() throws Exception {
        RainSensorHistory r = new RainSensorHistory();
        LocalDateTime t = r.getWhenStartedRaining();        // result should be MIN time.
        boolean testPass = t.getHour() == 0 && t.getMinute() == 0 && t.getSecond() == 0;
        Assert.assertEquals("getWhenStartedRaining failed with no rain", true, testPass);
        t = LocalDateTime.now();
        r.incrementRain();
        Thread.sleep(2000);
        r.incrementRain();
        r.incrementRain();
        r.incrementRain();
        Assert.assertEquals("getWhenStartedRaining date is incorrect ", t.getSecond(), r.getWhenStartedRaining().getSecond(), 1);
    }
}