package simulator;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import legacy.jaxb.Measurement;
import legacy.jaxb.Measurements;
import org.eclipse.paho.client.mqttv3.MqttException;

public class App {
    public static void main(String[] args) throws JAXBException, MqttException {

        IoTCoreMqttClient mqttClient = new IoTCoreMqttClient.Builder("[replace with project id]")
            .withHost("STD")
            .withPort("MQTT")
            .withRegistry("test_reg")
            .withRegion("EMEA")
            .withDeviceId("sensor1")
            .withKeyAlgorithm("RS256")
            .withPrivateKeyPath("/Users/chwa/wip/cloud-iot-cloud-run-sample/client/keys/rsa_private_pkcs8")
            .build();

        JAXBContext jaxbContext = JAXBContext.newInstance(Measurements.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        StringWriter stringWriter = null;
        mqttClient.connect();

        for (int i = 0; i < 100; i++) {
            stringWriter = new StringWriter();
            jaxbMarshaller.marshal(generateMeasurements(), stringWriter);
            mqttClient.sendEventData(stringWriter.toString());
        }

        mqttClient.disconnect();
    }

    private static Measurements generateMeasurements() {
        List<Measurement> measurementList = new ArrayList<Measurement>();
        IntStream.range(0, 20).forEach($ -> {
            measurementList.add(generateMeasurement());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        Measurements measurements = new Measurements();
        measurements.setMeasurements(measurementList);
        return measurements;
    }

    private static Measurement generateMeasurement() {
        Measurement measurement = new Measurement();
        measurement.setHumidity(generateHumidity());
        measurement.setLightLevel(generateLightLevel());
        measurement.setPressure(generatePressure());
        measurement.setTemperature(generateTemperature());
        measurement.setTimestamp(new Date());
        return measurement;
    }

    private static Double generateHumidity() {
        return Math.round(Math.random()*10000)/100.0;
    }

    private static Double generateLightLevel() {
        return Math.round(Math.random()*100000)/10.0;
    }

    private static Double generatePressure() {
        return Math.round((Math.random()-0.5)*1000)/100.0+98;
    }

    private static Double generateTemperature() {
        return Math.round((Math.random()-0.5)*10000)/100.0;
    }
}