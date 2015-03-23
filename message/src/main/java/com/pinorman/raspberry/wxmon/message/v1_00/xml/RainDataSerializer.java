package com.pinorman.raspberry.wxmon.message.v1_00.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.pinorman.raspberry.wxmon.message.RainSensorReading;
import com.pinorman.raspberry.wxmon.message.RainSensorReadingSerializer;
import com.pinorman.raspberry.wxmon.message.SerializeException;

class RainDataSerializer implements RainSensorReadingSerializer {

    private static JAXBContext context;

    private JAXBContext getContext() throws SerializeException {
        try {
            if (context == null) {
                synchronized (this) {
                    if (context == null) {
                        context = JAXBContext.newInstance(RainData.class.getPackage().getName());
                    }
                }
            }
        } catch (JAXBException e) {
            throw new SerializeException("There was a problem creating the JAXB context for the parser.", e);
        }

        return context;
    }

    @Override
    public List<RainSensorReading> unmarshal(InputStream input) throws IOException, SerializeException {
        RainData data;
        try {
            data = (RainData) getContext().createUnmarshaller().unmarshal(input);
        } catch (JAXBException e) {
            throw new SerializeException("There was a problem unmarshaling the XML rain data.", e);
        }

        List<RainSensorReading> readings = new ArrayList<>(data.getReadings().size());
        for (RainReading reading : data.getReadings()) {
            readings.add(new RainSensorReading(reading.getValue(), reading.getTime()));
        }
        return readings;
    }

    @Override
    public void marshal(Iterable<? extends RainSensorReading> data, Writer writer) throws SerializeException {
        RainData rainData = new RainData();
        for (RainSensorReading reading : data) {
            RainReading rainReading = new RainReading();
            rainReading.setValue(reading.getValue());
            rainReading.setTime(reading.getTime());
            rainData.getReadings().add(rainReading);
        }

        try {
            getContext().createMarshaller().marshal(rainData, writer);
        } catch (JAXBException e) {
            throw new SerializeException("There was a problem marshaling the XML rain data.", e);
        }
    }
}
