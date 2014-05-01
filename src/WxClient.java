import sensors.RainSensorImpl;
import sensors.TempSensorQue;
import sensors.Temperature;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by Paul on 4/30/2014.
 */
public class WxClient {

    static TempSensorQue tempQue;
    static RainSensorImpl wxStation;


    public static void main(String args[]) {

        DecimalFormat decForm = new DecimalFormat("##0.00");
        DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

        try {
            //IP is hard coded
            //port is user entry
            Socket socket = new Socket("192.168.1.105", 8080 );

            ObjectInputStream objectInputStream =
                    new ObjectInputStream(socket.getInputStream());
            try {
                Object objectTemp = objectInputStream.readObject();
                tempQue = (TempSensorQue) objectTemp;

            } catch (ClassNotFoundException ex) {
                System.out.println("on Temperature Obj" + ex.toString());
            }
            socket = new Socket("192.168.1.105", 8081 );

            objectInputStream =
                    new ObjectInputStream(socket.getInputStream());
            try {
                Object objectRain = objectInputStream.readObject();
                wxStation = (RainSensorImpl) objectRain;

            } catch (ClassNotFoundException ex) {
                System.out.println("on Rain Obj" + ex.toString());
            }
            System.out.println("Rain level is: " + decForm.format(wxStation.getRainLevel()) +
                    " Rate/Hour (by min & Hr): " +
                    decForm.format(wxStation.getRainPerHour(ChronoUnit.MINUTES)) + " " +
                    decForm.format(wxStation.getRainPerHour(ChronoUnit.HOURS)));
            System.out.println("Last time it Rained " + dateForm.format(wxStation.getLastTimeSawRain()));
            System.out.println("When this rain started " + dateForm.format(wxStation.getWhenStartedRaining()));

            System.out.println("Latest Temp " + decForm.format(tempQue.getCurrentTemp()) +
                    " Max " + decForm.format(tempQue.getMaxTemp()) +
                    " Min " + decForm.format(tempQue.getMinTemp()));
            System.out.println("Now print out last 50");
            Temperature tArray[] = tempQue.toArray();
            int len = tempQue.queSize();
            int begin = 0;
            if (len >= 50) begin = len - 50;
            for (int i = begin; i < len; i++) {
                System.out.println("Index is " + i + " Temp is " + decForm.format(tArray[i].getTemp()) +
                        " Time was " + dateForm.format(tArray[i].getTempTime()));
            }

            socket.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
