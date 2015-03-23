package com.pinorman.raspberry.wxmon.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public enum TemperatureDataSerializer implements SensorReadingSerializer<TemperatureSensorReading> {
    instance;

    private static final List<SensorReadingSerializer<TemperatureSensorReading>> SERIALIZERS = Arrays.asList(
            com.pinorman.raspberry.wxmon.message.v1_00.xml.TemperatureDataSerializer.instance);

    @Override
    public List<TemperatureSensorReading> unmarshal(InputStream input) throws IOException, SerializeException {
        for (SensorReadingSerializer<TemperatureSensorReading> serializer : SERIALIZERS) {
            try {
                return serializer.unmarshal(input);
            } catch (SerializeException ignored) {
            }
        }

        throw new SerializeException("Could not find a serializer for the temperature XML data from [" + input + "]");
    }

    @Override
    public void marshal(Iterable<? extends TemperatureSensorReading> data, Writer writer) throws SerializeException {
        for (SensorReadingSerializer<TemperatureSensorReading> serializer : SERIALIZERS) {
            try {
                serializer.marshal(data, writer);
                return;
            } catch (SerializeException ignored) {
            }
        }

        throw new SerializeException("Could not find a serializer for the temperature XML data from [" + data + "]");
    }
}