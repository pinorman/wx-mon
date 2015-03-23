package com.pinorman.raspberry.wxmon.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public enum RainDataSerializer implements SensorReadingSerializer<RainSensorReading> {
    instance;

    private static final List<SensorReadingSerializer<RainSensorReading>> SERIALIZERS = Arrays.asList(
            com.pinorman.raspberry.wxmon.message.v1_00.xml.RainDataSerializer.instance);

    @Override
    public List<RainSensorReading> unmarshal(InputStream input) throws IOException, SerializeException {
        for (SensorReadingSerializer<RainSensorReading> serializer : SERIALIZERS) {
            try {
                return serializer.unmarshal(input);
            } catch (SerializeException ignored) {
            }
        }

        throw new SerializeException("Could not find a serializer for the rain XML data from [" + input + "]");
    }

    @Override
    public void marshal(Iterable<? extends RainSensorReading> data, Writer writer) throws SerializeException {
        for (SensorReadingSerializer<RainSensorReading> serializer : SERIALIZERS) {
            try {
                serializer.marshal(data, writer);
                return;
            } catch (SerializeException ignored) {
            }
        }

        throw new SerializeException("Could not find a serializer for the rain XML data from [" + data + "]");
    }
}
