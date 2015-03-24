package com.pinorman.wxmon.message;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemperatureParser {

    public static void main(String[] args) throws Exception {
        new TemperatureParser().start();
    }

    private static final Logger log = LoggerFactory.getLogger(TemperatureParser.class);

    public void start() throws JAXBException, IOException, DatatypeConfigurationException, SerializeException {
        Path path = getResource("temperature-sample.xml");
        List<TemperatureSensorReading> readings = TemperatureDataSerializer.instance.unmarshal(
                Files.newInputStream(path));

        log.info("Reading temperature from {}", path);
        for (TemperatureSensorReading reading : readings) {
            log.info("    Temperature was {} F at {}", reading.getValue(), reading.getTime());
        }

        log.info("Writing data read to a string using the JAXB marshaller");
        StringWriter writer = new StringWriter();
        TemperatureDataSerializer.instance.marshal(readings, writer);
        log.info("{}", writer);


        log.info("Writing data read to a string using the JAXB marshaller");
        StringWriter anotherWriter = new StringWriter();
        List<TemperatureSensorReading> anotherSetOfReadings = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TemperatureSensorReading reading = new TemperatureSensorReading(40 + i,
                                                                            LocalDateTime.of(2015, Month.MARCH, 17, 22,
                                                                                             i * 10, 0, 0));
            anotherSetOfReadings.add(reading);
        }
        TemperatureDataSerializer.instance.marshal(anotherSetOfReadings, anotherWriter);
        log.info("{}", anotherWriter);
    }

    public static Path getResource(String stringPath) {
        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                return null;
            }
            final URL resource = loader.getResource(stringPath);
            if (resource == null) {
                log.error("Could not find path [{}]", stringPath);
                return null;
            }

            final URI uri = resource.toURI();
            Path path;
            try {
                path = Paths.get(uri);
            } catch (FileSystemNotFoundException ignore) {
                FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                path = Paths.get(uri);
            }
            return path;
        } catch (URISyntaxException | IOException e) {
            log.error("There was a problem loading path [{}]", stringPath, e);
        }
        return null;
    }
}
