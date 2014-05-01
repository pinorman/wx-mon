package sensors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * Created by Paul on 4/14/2014.
 */
public class RainSensorQue implements Serializable {

    private final double RAIN_STEP = 0.01; // 1/100 of an inch rain sensor
    private final int RAIN_GAP = 2; // is the gap in hours in the que (between dates)

    /*
    * tRain catcher
     */

    private Deque<LocalDateTime> qRain;
    private LocalDateTime lastTime;     // Used when we traverse the que
    private LocalDateTime firstTime;    // used when we traverse the que
    private int accumulatedRain = 0;    // dito


    /*
    * Constructor -
    *
    */
    public RainSensorQue() {
        qRain = new ConcurrentLinkedDeque<>();
    }

    /*
    * Simply put the date/time on the que for this increment of rain
    */
    public void incrementRain() {
        qRain.addFirst(LocalDateTime.now());
    }

    /*
     * First call findGapQue looking for a gap
     * then Look for a MINUTE or HOUR delta in time (only supports RainUnit of MINUTE OR HOUR)
     * and calc the rate/hour
     * return 0 if the rate cannot be calculated yet.
     */
    public double getRainPerHour(ChronoUnit timePer) {
        if (timePer != ChronoUnit.MINUTES && timePer != ChronoUnit.HOURS) return (0.0);
        if (!findGapQue(ChronoUnit.HOURS, RAIN_GAP)) return (0.0);
        boolean enoughTime;
        enoughTime = lastTime.until(firstTime, timePer) >= 1;
        return (enoughTime ? (((double) accumulatedRain * RAIN_STEP) / (double) lastTime.until(firstTime, ChronoUnit.SECONDS)) * 3600.0 : 0.0);

    }

    public LocalDateTime getWhenStartedRaining() {
        if (!findGapQue(ChronoUnit.HOURS, RAIN_GAP)) return (LocalDateTime.MIN);
        return (lastTime);

    }

    public LocalDateTime getLastTimeSawRain() {
        if (qRain.isEmpty()) return (LocalDateTime.MAX);
        return ((LocalDateTime) qRain.getFirst());
    }

    public double getRainLevel() {
        return ((double) qRain.size() * RAIN_STEP);
    }

    public long hoursBeenRaining() {
        if (!findGapQue(ChronoUnit.HOURS, RAIN_GAP)) return (0);
        return (lastTime.until(firstTime, ChronoUnit.HOURS));
    }

    /*
     * look  through the que - looking for a gap in rain input (increment) of RAIN_GAP
     * either we find a gap or we reach the beginning of the que.
     */
    private boolean findGapQue( ChronoUnit interval, int gap) {
        if (qRain.isEmpty()) return (false);
        accumulatedRain = 0;
        Iterator rainIterator = qRain.iterator();
        firstTime = lastTime = (LocalDateTime) qRain.getFirst();  // last time for an increment
        do {
            accumulatedRain++;
            lastTime = (LocalDateTime) rainIterator.next();
        } while (rainIterator.hasNext() && !(lastTime.until(firstTime, interval) >= gap));
        return (true);
    }

    private boolean gapOfTime(LocalDateTime firstT, LocalDateTime lastT) {
        return (lastT.until(firstT, ChronoUnit.HOURS) >= RAIN_GAP);
    }


}
