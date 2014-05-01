package sensors;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by Paul on 4/28/2014.
 */
public interface RainSensor {
    double getRainLevel();
    double getRainPerHour(ChronoUnit timePer);
    LocalDateTime getLastTimeSawRain();
    LocalDateTime getWhenStartedRaining();
}
