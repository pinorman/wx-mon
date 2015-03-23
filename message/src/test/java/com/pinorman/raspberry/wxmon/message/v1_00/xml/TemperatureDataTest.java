package com.pinorman.raspberry.wxmon.message.v1_00.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import com.pinorman.raspberry.wxmon.message.TemperatureSensorReading;
import org.junit.Assert;
import org.junit.Test;

public class TemperatureDataTest {

    @Test
    public void testUnmarshal() throws Exception {
        // @formatter:off
        String data = "<ns:temperatureData xmlns:ns=\"http://www.pinorman.com/raspberry/wxmon/message/v1_00/xml\">\n"
                    + "    <reading>\n"
                    + "        <value>32.0</value>\n"
                    + "        <time>2015-03-17T22:16:00</time>\n"
                    + "    </reading>\n"
                    + "    <reading>\n"
                    + "        <value>32.5</value>\n"
                    + "        <time>2015-03-17T22:17:00</time>\n"
                    + "    </reading>\n"
                    + "</ns:temperatureData>\n";
        // @formatter:on

        InputStream stream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        List<TemperatureSensorReading> readings = new TemperatureDataSerializer().unmarshal(stream);

        Assert.assertEquals(2, readings.size());

        { // readings[0]
            TemperatureSensorReading reading0 = readings.get(0);
            Assert.assertEquals(32.0, reading0.getValue(), 0.0);
            Assert.assertEquals(LocalDateTime.of(2015, Month.MARCH, 17, 22, 16), reading0.getTime());
        }

        { // readings[1]
            TemperatureSensorReading reading1 = readings.get(1);
            Assert.assertEquals(32.5, reading1.getValue(), 0.0);
            Assert.assertEquals(LocalDateTime.of(2015, Month.MARCH, 17, 22, 17), reading1.getTime());
        }
    }

    @Test
    public void testMarshal() throws Exception {
        // @formatter:off
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<ns2:temperatureData xmlns:ns2=\"http://www.pinorman.com/raspberry/wxmon/message/v1_00/xml\">"
                        + "<reading>"
                            + "<value>32.0</value>"
                            + "<time>2015-03-17T22:16</time>"
                        + "</reading>"
                        + "<reading>"
                            + "<value>32.5</value>"
                            + "<time>2015-03-17T22:17</time>"
                        + "</reading>"
                    + "</ns2:temperatureData>";
        // @formatter:on

        List<TemperatureSensorReading> readings = new ArrayList<>();
        readings.add(new TemperatureSensorReading(32.0, LocalDateTime.of(2015, Month.MARCH, 17, 22, 16)));
        readings.add(new TemperatureSensorReading(32.5, LocalDateTime.of(2015, Month.MARCH, 17, 22, 17)));

        StringWriter stringWriter = new StringWriter();
        new TemperatureDataSerializer().marshal(readings, stringWriter);

        Assert.assertEquals(data, stringWriter.toString());
    }
}
