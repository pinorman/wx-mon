package sensors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Paul on 3/24/14.
 */
public class TempReading implements Serializable {


    private double temp;
    private LocalDateTime tempTime;
    boolean sentToServer;


    public TempReading(double temp, LocalDateTime time) {
        this.temp = temp;
        this.tempTime = time;
    }


    public TempReading(double temp) {
        this( temp, LocalDateTime.now());
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
        this.tempTime = LocalDateTime.now();
        this.sentToServer = false;
    }

    public void setSentToServer() {
        this.sentToServer = true;
    }

    public boolean isSentToServer() {
        return sentToServer;
    }

    public LocalDateTime getTempTime() {
        return (tempTime);
    }
}




