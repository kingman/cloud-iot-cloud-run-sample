package processor;

import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import legacy.jaxb.Measurement;
import legacy.jaxb.Measurements;
import protobuf.MeasurementProtos;

@Component
public class PubSubPublisher {
    
    @Value("${PROJECT_ID}")
    private String projectId;

    @Value("${NORMALIZED_EVENT_TOPIC}")
    private String normalizedEventTopic;

    public void publish(String deviceId, Measurements measurements) throws Exception {
        ProjectTopicName topicName = ProjectTopicName.of(projectId, normalizedEventTopic);
        Publisher publisher =  Publisher.newBuilder(topicName).build();

        List<MeasurementProtos.Measurement> measurementProtos = measurements.getMeasurements().stream()
        .map(measurement -> transform(deviceId, measurement))
        .collect(Collectors.toList());

        for(MeasurementProtos.Measurement measurement : measurementProtos) {
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(measurement.toByteString()).build();
            publisher.publish(pubsubMessage);
        }
    }

    private MeasurementProtos.Measurement transform(String deviceId, Measurement measurement) {
        return protobuf.MeasurementProtos.Measurement.newBuilder() 
        .setDeviceId(deviceId)
        .setTimestamp(measurement.getTimestamp().getTime())
        .setTemperature(measurement.getTemperature().floatValue())
        .setPressure(measurement.getPressure().floatValue())
        .setLightLevel(measurement.getLightLevel().floatValue())
        .setHumidity(measurement.getHumidity().floatValue())
        .build();

    }
}