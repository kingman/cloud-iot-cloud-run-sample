package simulator;

import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class IoTCoreMqttClient {
    public static enum Host {
        STD("mqtt.googleapis.com"),
        LTS("mqtt.2030.ltsapis.goog");

        private String hostName;

        private Host(String hostName) {
            this.hostName = hostName;
        }

        public String getHostName() {
            return hostName;
        }
    }

    public static enum Port {
        MQTT(8883),
        TLS(443);

        private int portNumber;

        private Port(int portNumber) {
            this.portNumber = portNumber;
        }

        public int getPortNumber() {
            return portNumber;
        }
    }

    public static enum Region {
        AMEC("us-central1"),
        APAC("asia-east1"),
        EMEA("europe-west1");

        private String name;

        private Region(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    private MqttConnectOptions connectOptions;
    private IoTCoreJwtGenerator passwordGenerator;
    private MqttClient mqttClient;
    private String eventTopic;

    private IoTCoreMqttClient() {}

    public void connect() throws MqttSecurityException, MqttException {
        if (!mqttClient.isConnected()) {
            connectOptions.setPassword(passwordGenerator.generateJwt(60).toCharArray());
            mqttClient.connect(connectOptions);
        }
    }

    public void disconnect() throws MqttException {
        if(mqttClient.isConnected()) {
            mqttClient.disconnect();
        }
    }

    public void sendEventData(String data) throws MqttPersistenceException, MqttException {
        if(mqttClient.isConnected()) {
            MqttMessage message = new MqttMessage(data.getBytes());
            mqttClient.publish(eventTopic, message);
        } else {
            throw new RuntimeException("MQTT client not connected!");
        }
    }

    public static class Builder {
        private Host host;
        private Port port;
        private String projectId;
        private String registryId;
        private Region region;
        private String deviceId;
        private String privateKeyPath;
        private String algorithm;

        public Builder(String projectId) {
            this.projectId = projectId;
        }

        public Builder withHost(String hostType) {
            this.host = Host.valueOf(hostType);
            return this;
        }

        public Builder withPort(String portType) {
            this.port = Port.valueOf(portType);
            return this;
        }

        public Builder withRegistry(String registryId) {
            this.registryId = registryId;
            return this;
        }

        public Builder withRegion(String regionType) {
            this.region = Region.valueOf(regionType);
            return this;
        }

        public Builder withDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder withPrivateKeyPath(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
            return this;
        }

        public Builder withKeyAlgorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public IoTCoreMqttClient build() throws MqttException {
            IoTCoreMqttClient ioTCoreMqttClient = new IoTCoreMqttClient();

            ioTCoreMqttClient.connectOptions = getConnectOptions();
            
            ioTCoreMqttClient.passwordGenerator = new IoTCoreJwtGenerator.Builder(projectId)
            .withAlgorithm(algorithm)
            .withPrivateKey(privateKeyPath)
            .build();

            ioTCoreMqttClient.mqttClient = new MqttClient(getMqttServiceAddress(), getMqttClientId(), new MemoryPersistence());

            ioTCoreMqttClient.eventTopic = getDeviceEventTopicName();

            return ioTCoreMqttClient;
        }

        private String getMqttServiceAddress() {
            return String.format("ssl://%s:%s", this.host.getHostName(), this.port.getPortNumber());
        }

        private String getMqttClientId() {
            return String.format("projects/%s/locations/%s/registries/%s/devices/%s", this.projectId,
                this.region.getName(), this.registryId, this.deviceId);
        }

        private String getDeviceEventTopicName() {
            return String.format("/devices/%s/%s", this.deviceId, "events");
        }

        private static MqttConnectOptions getConnectOptions() {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
    
            connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
    
            Properties sslProps = new Properties();
            sslProps.setProperty("com.ibm.ssl.protocol", "TLSv1.2");
            connectOptions.setSSLProperties(sslProps);
    
            connectOptions.setUserName("unused");
            return connectOptions;
        }
     }
}