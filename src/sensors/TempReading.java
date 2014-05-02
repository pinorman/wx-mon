package sensors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Brian on 3/24/14.
 */
public class TempReading implements Serializable {


    private double temp;
    private LocalDateTime tempTime;


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
    }

    public LocalDateTime getTempTime() {
        return (tempTime);
    }
}




