package com.pinorman.wxmon.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.ServiceLoader;

public enum RainDataSerializer implements RainSensorReadingSerializer {
    instance;

    private final ServiceLoader<RainSensorReadingSerializer> serializers;

    RainDataSerializer() {
        this.serializers = ServiceLoader.load(RainSensorReadingSerializer.class);
    }

    @Override
    public List<RainSensorReading> unmarshal(InputStream input) throws IOException, SerializeException {
        for (SensorReadingSerializer<RainSensorReading> serializer : serializers) {
            try {
                return serializer.unmarshal(input);
            } catch (SerializeException ignored) {
            }
        }

        throw new SerializeException("Could not find a serializer for the rain XML data from [" + input + "]");
    }

    @Override
    public void marshal(Iterable<? extends RainSensorReading> data, Writer writer) throws SerializeException {
        for (SensorReadingSerializer<RainSensorReading> serializer : serializers) {
            try {
                serializer.marshal(data, writer);
                return;
            } catch (SerializeException ignored) {
            }
        }

        throw new SerializeException("Could not find a serializer for the rain XML data from [" + data + "]");
    }
}
