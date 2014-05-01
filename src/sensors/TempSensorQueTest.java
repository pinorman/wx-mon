package sensors;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Brian on 4/21/2014.
 */
public class TempSensorQueTest {
    @Test
    public void testMax() {
        TempSensorQue que = new TempSensorQue();

        Temperature temp1 = new Temperature(60);
        Temperature temp2 = new Temperature(45);
        Temperature temp3 = new Temperature(75);
        Temperature temp4 = new Temperature(76);
        Temperature temp5 = new Temperature(65);

        que.add(temp1);
        que.add(temp2);
        que.add(temp3);
        que.add(temp4);
        que.add(temp5);

        Assert.assertEquals("Max temperature is incorrect", que.getMaxTemp(), temp4.getTemp(), 0.0);
    }

    @Test
    public void testMin() {
        TempSensorQue que = new TempSensorQue();

        Temperature temp1 = new Temperature(60);
        Temperature temp2 = new Temperature(-45);
        Temperature temp3 = new Temperature(-75);
        Temperature temp4 = new Temperature(76);
        Temperature temp5 = new Temperature(65);

        que.add(temp1);
        que.add(temp2);
        que.add(temp3);
        que.add(temp4);
        que.add(temp5);

        Assert.assertEquals("Min temperature is incorrect", que.getMinTemp(), temp3.getTemp(), 0.0);

    }

}
