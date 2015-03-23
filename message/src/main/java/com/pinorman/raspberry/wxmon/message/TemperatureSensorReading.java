package com.pinorman.raspberry.wxmon.message;

import java.time.LocalDateTime;

public class TemperatureSensorReading implements SensorReading {

    private final double value;
    private final LocalDateTime time;

    public TemperatureSensorReading(double value, LocalDateTime time) {
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
