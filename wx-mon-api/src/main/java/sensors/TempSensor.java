package sensors;

import java.io.Serializable;

/**
 * Created by Brian on 4/20/2014.
 */
public interface TempSensor extends Serializable {
    double readTemp();
    double getCurrentTemp();
    void add( TempReading t );
    double getMaxTemp();
    double getMinTemp();
    TempReading[] toArray();
    int queSize();

}
