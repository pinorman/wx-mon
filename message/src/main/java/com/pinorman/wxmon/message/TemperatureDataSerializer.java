package com.pinorman.wxmon.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.ServiceLoader;

public enum TemperatureDataSerializer implements TemperatureSensorReadingSerializer {
    instance;

    private final ServiceLoader<TemperatureSensorReadingSerializer> serializers = ServiceLoader.load(
            TemperatureSensorReadingSerializer.class);

    @Override
    public List<TemperatureSensorReading> unmarshal(InputStream input) throws IOException, SerializeException {
        for (SensorReadingSerializer<TemperatureSensorReading> serializer : serializers) {
            try {
                return serializer.unmarshal(input);
            } catch (SerializeException ignored) {
            }
        }

        throw new SerializeException("Could not find a serializer for the temperature XML data from [" + input + "]");
    }

    @Override
    public void marshal(Iterable<? extends TemperatureSensorReading> data, Writer writer) throws SerializeException {
        for (SensorReadingSerializer<TemperatureSensorReading> serializer : serializers) {
            try {
                serializer.marshal(data, writer);
                return;
            } catch (SerializeException ignored) {
            }
        }

        throw new SerializeException("Could not find a serializer for the temperature XML data from [" + data + "]");
    }
}
