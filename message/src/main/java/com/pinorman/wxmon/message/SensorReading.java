package com.pinorman.wxmon.message;

import java.time.LocalDateTime;

public interface SensorReading {

    double getValue();

    LocalDateTime getTime();
}
