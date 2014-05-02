package sensors;

/**
 * Created by Brian on 4/20/2014.
 */
public interface TempSensor  {
    double readTemp();
    double getCurrentTemp();
    void add( TempReading t );
    double getMaxTemp();
    double getMinTemp();
    TempReading[] toArray();
    int queSize();

}
