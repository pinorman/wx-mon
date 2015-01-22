package sensors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Paul on 4/20/2014.
 */
public interface TempSensor extends Serializable {
    double getCurrentTemp();
    void add( TempReading t );
    double getMaxTemp(LocalDateTime beg, LocalDateTime end );
    double getMinTemp(LocalDateTime beg, LocalDateTime end );
    TempReading[] toArray();
    int queSize();

}
