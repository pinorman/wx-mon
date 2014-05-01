package sensors;

/**
 * Created by Paul on 4/20/2014.
 */
public class TempSimulate implements TempSensor {
    private int temp = -10;

    public TempSimulate() {
        return;

    }

    public double readTemp() {
        return( temp++);
    }


}

