import sensors.RainSensor;
import sensors.TempReading;
import sensors.TempSensor;

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

    private final static int TEMP_PORT = 8080;
    private final static int RAIN_PORT = 8081;

    DecimalFormat decForm = new DecimalFormat("##0.00");
    DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");


    private TempSensor temp;
    private RainSensor wxStation;


    public WxClient() {
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println("Usage: WxClient <Local IP address>");
            System.exit(1);
        }
        WxClient client = new WxClient();
        client.getAndDisplayData(args[0]);
    }

    public void getAndDisplayData(String serverIP) {

        temp = (TempSensor) readData( serverIP, TEMP_PORT);
        wxStation = (RainSensor) readData(serverIP, RAIN_PORT);

        System.out.println("Print last 50 from history");
        TempReading tArray[] = temp.toArray();
        int len = temp.queSize();
        int begin = 0;
        if (len >= 50) begin = len - 50;
        for (int i = begin; i < len; i++) {
            System.out.println("Index " + i + " Temp is " + decForm.format(tArray[i].getTemp()) +
                    " Time was " + dateForm.format(tArray[i].getTempTime()));
        }
        System.out.println("Rain level is: " + decForm.format(wxStation.getRainLevel()) +
                " Rate/Hour (by min & Hr): " +
                decForm.format(wxStation.getRainPerHour(ChronoUnit.MINUTES)) + " " +
                decForm.format(wxStation.getRainPerHour(ChronoUnit.HOURS)));
        System.out.println("Last time it Rained " + dateForm.format(wxStation.getLastTimeSawRain()));
        System.out.println("When this rain started " + dateForm.format(wxStation.getWhenStartedRaining()));

        System.out.println("Latest Temp " + decForm.format(temp.getCurrentTemp()) +
                " Max " + decForm.format(temp.getMaxTemp()) +
                " Min " + decForm.format(temp.getMinTemp()));

    }

    private Object readData(String ip, int port) {
        ObjectInputStream inputStream;
        Socket socket;
        Object obj = null;

        try {
            socket = new Socket(ip, port);
            inputStream = new ObjectInputStream(socket.getInputStream());
            try {
                obj = inputStream.readObject();
            } catch (ClassNotFoundException ex) {
                System.out.println(ex.toString());
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return obj;
    }
}