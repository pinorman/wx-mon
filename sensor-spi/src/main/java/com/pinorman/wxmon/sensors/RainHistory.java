package com.pinorman.wxmon.sensors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by Paul on 4/28/2014.
 */
public interface RainHistory extends Serializable {
    void incrementRain(LocalDateTime t);
    void incrementRain();

    double getRainTotal();

    double getRainPerHour(ChronoUnit timePer);

    LocalDateTime getLastTimeSawRain();

    LocalDateTime getWhenStartedRaining();

    double getAccumulatedRainLevel( ChronoUnit interval, int gap);
}
