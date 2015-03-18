package com.pinorman.raspberry.wxmon.sensors;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * Created by Paul on 3/29/2014.
 */

public class TempHistoryImpl implements TempHistory {

    public static final int MIN_TEMP = -40;
    public static final int MAX_TEMP = 120;

    private Deque<TempReading> qTemp;


    public TempHistoryImpl() {

        qTemp = new ConcurrentLinkedDeque<>();
    }


    public void add(TempReading t) {
        qTemp.add(t);
    }

    private boolean queEmpty() {
        return (qTemp.size() < 1);
    }

    public int queSize() {
        return (qTemp.size());
    }

    public TempReading[] toArray() {
        return (qTemp.toArray(new TempReading[qTemp.size()]));
    }

    public double getCurrentTemp() {
        return (!queEmpty() ? qTemp.getLast().getTemp() : MIN_TEMP);

    }

    /*
      Returns the maxium temperature found between the two dates give.
      If the begTime is before the first date on the que, the first temp on the que will be used.
      if the endTime is after the last date on the que then the last temp will be used.
      returns MIN_TEMP if
           endTime is before the first date in the que or
           begTim is after the last date in the que
     */
    public double getMaxTemp(LocalDateTime userBeginDate, LocalDateTime userEndDate) {

        double tMax = MIN_TEMP;
        if (this.queEmpty()) return (tMax);

        LocalDateTime qBeginDate, qEndDate;
        qBeginDate = qTemp.getFirst().getTempTime();
        qEndDate = qTemp.getLast().getTempTime();

        // Test some end conditions
        if (userEndDate.isBefore(qBeginDate) || userBeginDate.isAfter(qEndDate)) {
            return (tMax);
        }
        TempReading tArray[] = this.toArray();
        int len = this.queSize();
        LocalDateTime tArrayValue;
        for (int i = 0; i < len; i++) {
            tArrayValue = tArray[i].getTempTime();
            if ((tArrayValue.isAfter(userBeginDate) || tArrayValue.equals(userBeginDate)) &&
                    (tArrayValue.isBefore(userEndDate) || tArrayValue.equals(userEndDate))) {               // the test or "equals" is a bit overkill, but it made testing easier
                if (tMax < tArray[i].getTemp()) {
                    tMax = tArray[i].getTemp();
                }

            }
        }
        return (tMax);
    }


    public double getMinTemp(LocalDateTime userBeginDate, LocalDateTime userEndDate) {

        double tMin = MAX_TEMP;
        if (this.queEmpty()) return (tMin);

        LocalDateTime qBeginDate, qEndDate;
        qBeginDate = qTemp.getFirst().getTempTime();
        qEndDate = qTemp.getLast().getTempTime();

        // Test some end conditions
        if (userEndDate.isBefore(qBeginDate) || userBeginDate.isAfter(qEndDate)) {
            return (tMin);
        }
        TempReading tArray[] = this.toArray();
        int len = this.queSize();
        LocalDateTime tArrayValue;
        for (int i = 0; i < len; i++) {
            tArrayValue = tArray[i].getTempTime();
            if ((tArrayValue.isAfter(userBeginDate) || tArrayValue.equals(userBeginDate)) &&
                    (tArrayValue.isBefore(userEndDate) || tArrayValue.equals(userEndDate))) {               // the test or "equals" is a bit overkill, but it made testing easier
                if (tMin > tArray[i].getTemp()) {
                    tMin = tArray[i].getTemp();
                }

            }
        }
        return (tMin);
    }
}

