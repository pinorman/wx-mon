package sensors;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


/**
 * Created by Paul on 4/24/2014.
 */
public class RainSensorImpl implements RainSensor, Serializable {


    /*
    * Constructor -
    * Create the listener for Pin 2 (for now)
    * where we gather rain into
    * */
    private RainSensorHistory rainData = new RainSensorHistory();

    public RainSensorImpl(GpioController gpio) {

        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
        System.out.println("declared pin as input");

        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) rainData.incrementRain();
                // display pin state on console
                //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            }

        });
    }

    @Override
    public double getRainLevel() {
        return rainData.getRainLevel();

    }

    @Override
    public void incrementRain(LocalDateTime t) {
        rainData.incrementRain(t);
    }

    @Override
    public double getRainPerHour(ChronoUnit t) {
        return rainData.getRainPerHour(t);
    }

    @Override
    public LocalDateTime getLastTimeSawRain() {
        return rainData.getLastTimeSawRain();
    }

    @Override
    public LocalDateTime getWhenStartedRaining() {
        return (rainData.getWhenStartedRaining());
    }

    @Override
    public double getAccumulatedRainLevel(ChronoUnit interval, int gap) {
        return (rainData.getAccumulatedRainLevel(interval, gap));
    }
}
