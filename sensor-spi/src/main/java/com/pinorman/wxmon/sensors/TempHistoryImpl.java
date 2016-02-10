package com.pinorman.wxmon.sensors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * Created by Paul on 3/29/2014.
 */

public class TempHistoryImpl implements TempHistory {

    public static final int MIN_TEMP = -40;
    public static final int MAX_TEMP = 120;

    private static final Logger log = LoggerFactory.getLogger(TempHistoryImpl.class);

    private static DecimalFormat decForm = new DecimalFormat("##0.00");
    private static DateTimeFormatter dateParser = DateTimeFormatter.ofPattern(" yyyy-MM-d H:m:ss.nnnnnnnnn");       // notice the " " in front of yyyy
    private static String DATE_NOT_Found = "1900-1-1";

    private Deque<TempReading> qTemp;
    private String fileName;
    private boolean fileWrite;

    public TempHistoryImpl(String dataFile) {
        qTemp = new ConcurrentLinkedDeque<>();
        fileWrite = true;
        double temp;
        fileName = dataFile;
        log.info("Read in any existing temperature data for {} ", dataFile);
        File file = new File(dataFile);
        if (file.isFile()) {                            // if the file is there, read from it
            try (Scanner s = new Scanner(file)) {
                while (s.hasNext()) {
                    temp = s.nextDouble();      // read in temp from line in fileName
                    qTemp.add(new TempReading(temp, LocalDateTime.parse(s.nextLine(), dateParser))); // read in date and add in the records we parse from the fileName
                }
            } catch (IOException e) {
                log.warn("Error reading Temperature fileName ", e);
            }
        }
    }

    public TempHistoryImpl() {
        qTemp = new ConcurrentLinkedDeque<>();
        fileWrite = false;
    }


    public void add(TempReading temp) {
        qTemp.add(temp);

        if (fileWrite) {
            StringBuilder sb = new StringBuilder();
            // build string with temp and date;
            sb.append(decForm.format(temp.getTemp())).append(dateParser.format(temp.getTempTime())).append("\n");
            // write it to the file
            try (BufferedWriter tOut = new BufferedWriter(new FileWriter(this.fileName, true))) {
                tOut.write(sb.toString());
            } catch (IOException e) {
                log.warn("Error on Outside Temperature fileName", e);
            }
        }
    }

    private boolean queEmpty() {
        return (qTemp.size() < 1);
    }

    public int queSize() {
        return (qTemp.size());
    }

    public TempReading[] toArray() {
        return (qTemp.toArray(new TempReading[qTemp.size()]));
    }

    public double getCurrentTemp() {
        return (!queEmpty() ? qTemp.getLast().getTemp() : MIN_TEMP);

    }

    /*
      Returns the maxium temperature found between the two dates give.
      If the begTime is before the first date on the que, the first temp on the que will be used.
      if the endTime is after the last date on the que then the last temp will be used.
      returns MIN_TEMP (within TempReading) if
           endTime is before the first date in the que or
           begTim is after the last date in the que
     */
    public TempReading getMaxTemp(LocalDateTime userBeginDate, LocalDateTime userEndDate) {

        double tMax = MIN_TEMP;
        TempReading maxTemp = new TempReading(MIN_TEMP);
        if (this.queEmpty()) return maxTemp;

        LocalDateTime qBeginDate, qEndDate;
        qBeginDate = qTemp.getFirst().getTempTime();
        qEndDate = qTemp.getLast().getTempTime();

        // Test some end conditions
        if (userEndDate.isBefore(qBeginDate) || userBeginDate.isAfter(qEndDate)) {
            return maxTemp;
        }
        TempReading tArray[] = this.toArray();
        int len = this.queSize();
        LocalDateTime tArrayValue;
        for (int i = 0; i < len; i++) {
            tArrayValue = tArray[i].getTempTime();
            if ((tArrayValue.isAfter(userBeginDate) || tArrayValue.equals(userBeginDate)) &&
                    (tArrayValue.isBefore(userEndDate) || tArrayValue.equals(userEndDate))) {               // the test or "equals" is a bit overkill, but it made testing easier
                if (tMax < tArray[i].getTemp()) {
                    tMax = tArray[i].getTemp();
                    maxTemp = tArray[i];
                }

            }
        }
        return maxTemp;
    }


    public TempReading getMinTemp(LocalDateTime userBeginDate, LocalDateTime userEndDate) {

        double tMin = MAX_TEMP;
        TempReading minTemp = new TempReading(MAX_TEMP);

        if (this.queEmpty()) return minTemp;

        LocalDateTime qBeginDate, qEndDate;
        qBeginDate = qTemp.getFirst().getTempTime();
        qEndDate = qTemp.getLast().getTempTime();

        // Test some end conditions
        if (userEndDate.isBefore(qBeginDate) || userBeginDate.isAfter(qEndDate)) {
            return minTemp;
        }
        TempReading tArray[] = this.toArray();
        int len = this.queSize();
        LocalDateTime tArrayValue;
        for (int i = 0; i < len; i++) {
            tArrayValue = tArray[i].getTempTime();
            if ((tArrayValue.isAfter(userBeginDate) || tArrayValue.equals(userBeginDate)) &&
                    (tArrayValue.isBefore(userEndDate) || tArrayValue.equals(userEndDate))) {               // the test or "equals" is a bit overkill, but it made testing easier
                if (tMin > tArray[i].getTemp()) {
                    tMin = tArray[i].getTemp();
                    minTemp = tArray[i];
                }

            }
        }
        return minTemp;
    }

}

