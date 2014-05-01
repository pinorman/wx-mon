package sensors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * Created by Brian on 3/29/2014.
 */

public class TempSensorQue implements Serializable {

    private static final int QUE_DEPTH = 10; /* keep this number by default */
    public static final int MIN_TEMP = -40;
    public static final int MAX_TEMP = 120;

    private Temperature minTemp = new Temperature(MAX_TEMP); /* keep a min/max for this queue */
    private Temperature maxTemp = new Temperature(MIN_TEMP);

    private int qDepth;      /* How many readings to keep in the queue*/
    private Deque<Temperature> qTemp;


    public TempSensorQue(int qDepth) {

        qTemp = new ConcurrentLinkedDeque<>();
        if (qDepth > QUE_DEPTH) this.qDepth = qDepth;
        qDepth = QUE_DEPTH;
    }

    public TempSensorQue() {
        this(QUE_DEPTH);
    }

    private boolean queEmpty() {
        return (qTemp.size() < 1);
    }


    public void add(Temperature t) {
        qTemp.add(t);
        double temp = t.getTemp();
        if (temp > maxTemp.getTemp()) {
            maxTemp.setTemp(temp);
        }
        if (temp < minTemp.getTemp()) {
            minTemp.setTemp(temp);
        }
        /* keep the queue at the correct length */
        if (qTemp.size() > qDepth) qTemp.removeFirst();  /* throw the oldest away */
    }

    public int queSize() {
        return (qTemp.size());
    }

    public Temperature[] toArray() {
        return (qTemp.toArray(new Temperature[0]));
    }

    public double getCurrentTemp() {
        return (!queEmpty() ? qTemp.getLast().getTemp() : MIN_TEMP);

    }


    public double getMaxTemp() {
        return (maxTemp.getTemp());
    }

    public LocalDateTime getMaxTempTime() {
        return (getMaxTempTime());
    }

    public double getMinTemp() {
        return (minTemp.getTemp());
    }

    public LocalDateTime getMinTempTime() {
        return (getMaxTempTime());
    }

    public void resetMinTemp() {
        minTemp.setTemp(MAX_TEMP);
    }

    public void resetMaxTemp() {
        maxTemp.setTemp(MIN_TEMP);
    }

}