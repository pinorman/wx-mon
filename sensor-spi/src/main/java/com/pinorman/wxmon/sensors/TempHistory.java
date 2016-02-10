package com.pinorman.wxmon.sensors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Paul on 4/20/2014.
 */
public interface TempHistory extends Serializable {
    double getCurrentTemp();
    void add( TempReading t );
    TempReading getMaxTemp(LocalDateTime beg, LocalDateTime end );
    TempReading getMinTemp(LocalDateTime beg, LocalDateTime end );
    TempReading[] toArray();
    int queSize();

}
