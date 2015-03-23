package com.pinorman.raspberry.wxmon.message;

import java.time.LocalDateTime;

public interface SensorReading {

    double getValue();

    LocalDateTime getTime();
}
