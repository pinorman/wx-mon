package sensors;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Paul on 4/21/2014.
 */
public class TempSensorImplTest {
    @Test
    public void testMax() {
        TempSensorImpl que = new TempSensorImpl();

        TempReading temp1 = new TempReading(60);
        TempReading temp2 = new TempReading(45);
        TempReading temp3 = new TempReading(75);
        TempReading temp4 = new TempReading(76);
        TempReading temp5 = new TempReading(65);

        que.add(temp1);
        que.add(temp2);
        que.add(temp3);
        que.add(temp4);
        que.add(temp5);

        Assert.assertEquals("Max temperature is incorrect", que.getMaxTemp(), temp4.getTemp(), 0.0);
    }

    @Test
    public void testMin() {
        TempSensorImpl que = new TempSensorImpl();

        TempReading temp1 = new TempReading(60);
        TempReading temp2 = new TempReading(-45);
        TempReading temp3 = new TempReading(-75);
        TempReading temp4 = new TempReading(76);
        TempReading temp5 = new TempReading(65);

        que.add(temp1);
        que.add(temp2);
        que.add(temp3);
        que.add(temp4);
        que.add(temp5);

        Assert.assertEquals("Min temperature is incorrect", que.getMinTemp(), temp3.getTemp(), 0.0);

    }

}
