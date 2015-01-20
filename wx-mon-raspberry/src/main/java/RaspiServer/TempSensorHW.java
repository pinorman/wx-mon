package RaspiServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;


/**
 * Created by Paul on 4/20/2014.
 */
public class TempSensorHW {

    private static final Logger log = LoggerFactory.getLogger(WxRaspiServer.class);
    private static String w1DirPath = "/sys/bus/w1/devices";

    private File probeFilename;
    private boolean tProbeFound = true;


    public TempSensorHW(String sensorId) {
        String filePath = w1DirPath + "/" + sensorId + "/w1_slave";
        try {
            probeFilename = new File(filePath);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            tProbeFound = false;
        }
    }

    /*
    * Empty Constructor? then
    * Find the first tSensor in the module dir (driver) and set it as the one to use
    * This directory created by 1-wire kernel modules
    * If no temp sensor Id is found then we don'tabc have one to record from
    *
    */
   public TempSensorHW() {

        String filePath;
        File dir = new File(w1DirPath);
        File[] files = dir.listFiles(new DirectoryFileFilter());
        if (files != null && files.length >= 1)
            filePath = w1DirPath + "/" + files[0].getName() + "/w1_slave";
        else
            filePath = w1DirPath + "/w1_slave"; //which should fail on the try/catch
        try {
            probeFilename = new File(filePath);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            tProbeFound = false;
        }
    }

    public double readTemp() {
        double tempF = 0;
        double tempC;
        try {
            BufferedReader br = new BufferedReader(new FileReader(probeFilename));
            String output;
            // Multiple lines from the device
            // "t=" is the line of interest
            while ((output = br.readLine()) != null) {
                if (output.contains("t=")) {
                    // Temp data (multiplied by 1000) in 5 chars after tabc=
                    tempC = (double) Float.parseFloat(
                            output.substring(output.indexOf("t=") + 2));
                    // Divide by 1000 to get degrees Celsius
                    tempC /= 1000;
                    tempF = tempC * 9 / 5 + 32;
                }
            }
            return (tempF);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            return (-100); // for now
        }
    }

// This FileFilter selects subdirs with name beginning with 28-
// Kernel module gives each 1-wire temp sensor a name starting with 28-

    private class DirectoryFileFilter implements FileFilter {
        public boolean accept(File file) {
            String dirName = file.getName();
            String startOfName = dirName.substring(0, 3);
            return (file.isDirectory() && startOfName.equals("28-"));
        }

    }
}


