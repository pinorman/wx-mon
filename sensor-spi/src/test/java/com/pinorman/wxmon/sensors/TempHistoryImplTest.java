package com.pinorman.wxmon.sensors;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 4/21/2014.
 */
public class TempHistoryImplTest {

    @Test
    public void testAdd() throws Exception {
        TempHistoryImpl que = new TempHistoryImpl();


    }

    @Test
    public void testQueSize() throws Exception {
        TempHistoryImpl que = new TempHistoryImpl();


    }

    @Test
    public void testToArray() throws Exception {
        TempHistoryImpl que = new TempHistoryImpl();


    }

    @Test
    public void testGetCurrentTemp() throws Exception {
        TempHistoryImpl que = new TempHistoryImpl();

        Assert.assertEquals("testGetCurrentTemp failed with empty que", que.getCurrentTemp(), -40, 0.0);
        TempReading t1 = new TempReading(20, LocalDateTime.now());
        TempReading t2 = new TempReading(40 );
        que.add(t1);
        que.add(t2);
        Assert.assertEquals("testGetCurrentTemp failed to return current value", que.getCurrentTemp(), 40, 0.0);


    }

    @Test
    public void testGetMaxTemp() throws Exception {
        TempHistoryImpl que = new TempHistoryImpl();

        Assert.assertEquals("testGetMaxTemp: Max temperature is incorrect with que empty", -40,
                que.getMaxTemp(LocalDateTime.of(2000, 1, 1, 0, 0, 0), LocalDateTime.of(2000, 1, 2, 0, 1, 0)).getTemp(), 0.0);

        LocalDateTime time = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        List<LocalDateTime> times = new ArrayList<>();

        for (int i = 0; i < 10; i++) {                  // build an array of times to use for testing
            times.add(time.plusMinutes(i));             // each a min apart.
        }
        /*
        Take the list of times and build a set of temp readings to test against
         */

        List<TempReading> temps = new ArrayList<>();

        temps.add(new TempReading(170, times.get(0)));
        temps.add(new TempReading(80, times.get(1)));
        temps.add(new TempReading(70, times.get(2)));
        temps.add(new TempReading(160, times.get(3)));
        temps.add(new TempReading(85, times.get(4)));
        temps.add(new TempReading(110, times.get(5)));
        temps.add(new TempReading(45, times.get(6)));
        temps.add(new TempReading(-10, times.get(7)));
        temps.add(new TempReading(190, times.get(8)));
        temps.add(new TempReading(200, times.get(9)));

        for (TempReading t : temps) {
            que.add(t);
        }

        /*
        Now set up for testing
        test against each edge, the middle etc.
         */
        Assert.assertEquals("testGetMaxTemp: Max temperature is incorrect for whole que", 200,
                que.getMaxTemp(times.get(0), times.get(9)).getTemp(), 0.0);
        Assert.assertEquals("testGetMaxTemp: Max temperature is incorrect for start boundary", 170,
                que.getMaxTemp(times.get(0), times.get(4)).getTemp(), 0.0);
        Assert.assertEquals("testGetMaxTemp: Max temperature is incorrect for end boundary", 200,
                que.getMaxTemp(times.get(6), times.get(9)).getTemp(), 0.0);
        Assert.assertEquals("testGetMaxTemp: Max temperature is incorrect for middle of que", 160,
                que.getMaxTemp(times.get(1), times.get(7)).getTemp(), 0.0);
        Assert.assertEquals("testGetMaxTemp: Max temperature is incorrect for middle of que", 190,
                que.getMaxTemp(times.get(1), times.get(8)).getTemp(), 0.0);
        Assert.assertEquals("testGetMaxTemp: Max temperature is incorrect for times equal", 190,
                que.getMaxTemp(times.get(8), times.get(8)).getTemp(), 0.0);

    }

    @Test
    public void testGetMinTemp() throws Exception {
        TempHistoryImpl que = new TempHistoryImpl();

        Assert.assertEquals("testGetMinTesmp: Max temperature is incorrect with que empty", 120,
                que.getMinTemp(LocalDateTime.of(2000, 1, 1, 0, 0, 0), LocalDateTime.of(2000, 1, 2, 0, 1, 0)).getTemp(), 0.0);

        LocalDateTime time = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        List<LocalDateTime> times = new ArrayList<>();

        for (int i = 0; i < 10; i++) {                  // build an array of times to use for testing
            times.add(time.plusMinutes(i));             // each a min apart.
        }
        /*
        Take the list of times and build a set of temp readings to test against
         */

        List<TempReading> temps = new ArrayList<>();

        temps.add(new TempReading(-10, times.get(0)));
        temps.add(new TempReading(80, times.get(1)));
        temps.add(new TempReading(70, times.get(2)));
        temps.add(new TempReading(-15, times.get(3)));
        temps.add(new TempReading(20, times.get(4)));
        temps.add(new TempReading(-14, times.get(5)));
        temps.add(new TempReading(0, times.get(6)));
        temps.add(new TempReading(-10, times.get(7)));
        temps.add(new TempReading(-25, times.get(8)));
        temps.add(new TempReading(-30, times.get(9)));

        for (TempReading t : temps) {
            que.add(t);
        }

        /*
        Now we are set up for testing
        test against each edge, the middle etc.
         */
        Assert.assertEquals("testGetMinTemp: Max temperature is incorrect for whole que", -30,
                que.getMinTemp(times.get(0), times.get(9)).getTemp(), 0.0);
        Assert.assertEquals("testGetMinTemp: Max temperature is incorrect for start boundary", -15,
                que.getMinTemp(times.get(0), times.get(4)).getTemp(), 0.0);
        Assert.assertEquals("testGetMinTemp: Max temperature is incorrect for end boundary", -30,
                que.getMinTemp(times.get(6), times.get(9)).getTemp(), 0.0);
        Assert.assertEquals("testGetMinTemp: Max temperature is incorrect for middle of que", -15,
                que.getMinTemp(times.get(1), times.get(7)).getTemp(), 0.0);
        Assert.assertEquals("testGetMinTemp: Max temperature is incorrect for middle of que", -25,
                que.getMinTemp(times.get(1), times.get(8)).getTemp(), 0.0);
        Assert.assertEquals("testGetMinTemp: Max temperature is incorrect for times equal", -25,
                que.getMinTemp(times.get(8), times.get(8)).getTemp(), 0.0);
    }
}
