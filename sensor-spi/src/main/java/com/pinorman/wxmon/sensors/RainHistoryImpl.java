package com.pinorman.wxmon.sensors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Deque;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * Created by Paul on 4/14/2014.
 */
public class RainHistoryImpl implements RainHistory {

    private final double RAIN_STEP = 0.01; // 1/100 of an inch rain sensor
    private final int RAIN_GAP = 2; // is the gap in hours in the que (between dates)
    private static final Logger log = LoggerFactory.getLogger(TempHistoryImpl.class);
    private static DateTimeFormatter dateParser = DateTimeFormatter.ofPattern(" yyyy-MM-d H:m:ss.nnnnnnnnn");       // notice the " " in front of yyyy

    /*
    * tRain catcher
     */

    private Deque<LocalDateTime> qRain;
    private LocalDateTime lastTime;     // Used when we traverse the que
    private LocalDateTime firstTime;    // used when we traverse the que
    private int accumulatedRain = 0;    // dito
    private boolean fileWrite;
    private String fileName;

    /*
    * Constructor -
    * The Deque is build such that we add to the end of the queue - with .add()
    *
    */
    public RainHistoryImpl() {
        qRain = new ConcurrentLinkedDeque<>();
        fileWrite = false;
    }

    public RainHistoryImpl(String dataFile) {
        qRain = new ConcurrentLinkedDeque<>();
        fileWrite = true;
        fileName = dataFile;
        log.info("Read in any existing Rain data for {} ", dataFile);
        File file = new File(dataFile);
        if (file.isFile()) {                            // if the file is there, read from it
            try (Scanner s = new Scanner(file)) {
                while (s.hasNext()) {
                    qRain.add(LocalDateTime.parse(s.nextLine(), dateParser)); // read in date and add in the records we parse from the fileName
                }
            } catch (IOException e) {
                log.warn("Error reading Rain fileName ", e);
            }
        }
    }

    /*
    * Simply put the date/time on the que for this increment of rain
    */
    public void incrementRain() {
        incrementRain(LocalDateTime.now());
    }

    public void incrementRain(LocalDateTime t) {
        qRain.add(t);
        if (fileWrite) {
            StringBuilder sb = new StringBuilder();
            // build string with temp and date;
            sb.append(dateParser.format(t)).append("\n");
            // write it to the file
            try (BufferedWriter tOut = new BufferedWriter(new FileWriter(this.fileName, true))) {
                tOut.write(sb.toString());
            } catch (IOException e) {
                log.warn("Error on Outside Rain fileName", e);
            }
        }
    }

    /*
     * First call findGapQue looking for a gap
     * then Look for a MINUTE or HOUR delta in time (only supports RainUnit of MINUTE OR HOUR)
     * and calc the rate/hour
     * return 0 if the rate cannot be calculated yet.
     */
    public double getRainPerHour(ChronoUnit timePer) {
        if (qRain.isEmpty()) return (0.0);
        if (timePer != ChronoUnit.MINUTES && timePer != ChronoUnit.HOURS) return (0.0);
        findGapQue(ChronoUnit.HOURS, RAIN_GAP);
        boolean enoughTime;
        if (firstTime.until(lastTime, timePer) < 1) return (0.0);  // Rain must be going for 1 time unit to make the ca
        double rain = (double) (accumulatedRain) * RAIN_STEP / (double) firstTime.until(lastTime, ChronoUnit.SECONDS);
        if (timePer == ChronoUnit.HOURS)
            return (rain * 3600);  // seconds to hours
        return (rain * 60);        // seconds to minutes
    }

    public LocalDateTime getWhenStartedRaining() {
        if (qRain.isEmpty()) return (LocalDateTime.MIN);
        findGapQue(ChronoUnit.HOURS, RAIN_GAP);
        return (firstTime);

    }

    public LocalDateTime getLastTimeSawRain() {
        if (qRain.isEmpty()) return (LocalDateTime.MAX);
        return (qRain.getLast());
    }

    public double getRainTotal() {
        return ((double) qRain.size() * RAIN_STEP);
    }

    public double getAccumulatedRainLevel(ChronoUnit interval, int gap) {
        if (qRain.isEmpty()) return (0.0);
        findGapQue(interval, gap);
        return (accumulatedRain * RAIN_STEP);
    }

    public long hoursBeenRaining() {
        if (qRain.isEmpty()) return (0);
        findGapQue(ChronoUnit.HOURS, RAIN_GAP);
        return (firstTime.until(lastTime, ChronoUnit.HOURS));
    }

    /*
     * look  through the que - looking for a gap in rain/date of given gap
     * either we find a gap or we reach the beginning of the que.
     *  Set firstTime and lastTime appropreaitely. from the last rain entry (lastTime) back to either a gap or
     *  the beginning (firstTime).
     *
     */
    private void findGapQue(ChronoUnit interval, int gap) {
        accumulatedRain = 0;
        Iterator rainIterator = qRain.descendingIterator();     // processed last to first in the Deque
        lastTime = qRain.getLast();                             //
        LocalDateTime gapBegin = lastTime;
        do {
            accumulatedRain++;
            firstTime = gapBegin;                              // look between firstTime and gapBegin for gap
            gapBegin = (LocalDateTime) rainIterator.next();    // move to next time
        }
        while (rainIterator.hasNext() && (gapBegin.until(firstTime, interval) < gap));    //have we hit the end or found a gpa?
        if (!rainIterator.hasNext()) firstTime = gapBegin;     // if we reached the end then first should be set here
    }

}
