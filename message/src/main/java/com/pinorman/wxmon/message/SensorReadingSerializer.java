package com.pinorman.wxmon.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

public interface SensorReadingSerializer<R extends SensorReading> {

    List<R> unmarshal(InputStream input) throws IOException, SerializeException;

    void marshal(Iterable<? extends R> data, Writer writer) throws SerializeException;
}
