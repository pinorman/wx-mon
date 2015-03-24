package com.pinorman.wxmon.message;

import java.time.LocalDateTime;

public class RainSensorReading implements SensorReading {

    private final double value;
    private final LocalDateTime time;

    public RainSensorReading(double value, LocalDateTime time) {
        this.value = value;
        this.time = time;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }
}
