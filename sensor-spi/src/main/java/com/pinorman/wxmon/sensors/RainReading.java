package com.pinorman.wxmon.sensors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Paul on 3/8/2016.
 */
public class RainReading implements Serializable {

    private double reading;
    private LocalDateTime time;

    public RainReading(double value, LocalDateTime time) {
        this.reading = value;
        this.time = time;
    }

    public RainReading(double value) {
        this(value, LocalDateTime.now());
    }

    public double getRain() {
        return reading;
    }

    public LocalDateTime getRainTime() {
        return time;
    }
}
