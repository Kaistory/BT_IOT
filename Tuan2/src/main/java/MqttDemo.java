import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.json.JSONObject;
import javax.net.ssl.SSLSocketFactory;

public class MqttDemo {

    private static final String BROKER_URL = "ssl://76bf78e5731240cbb090e3284089c085.s1.eu.hivemq.cloud:8883";
    private static final String MQTT_USERNAME = "carController";
    private static final String MQTT_PASSWORD = "Khaito4224!";
    private static final String CLIENT_ID = "JavaCarControllerClient";
    private static final String TOPIC = "car/controller/data";

    public static void main(String[] args) {
        MqttClient client = null;
        try {
            client = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());

            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setCleanStart(true);
            options.setUserName(MQTT_USERNAME);
            options.setPassword(MQTT_PASSWORD.getBytes());
            options.setSocketFactory(SSLSocketFactory.getDefault());

            MqttClient finalClient = client;
            client.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("\n[INFO] Tin nhắn nhận được!");
                    String payload = new String(message.getPayload());
                    System.out.println("-> Topic: " + topic);
                    System.out.println("-> Payload: " + payload);
                    parseAndDisplay(payload);
                }

                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    System.out.println("[SUCCESS] Kết nối tới broker thành công.");
                    try {
                        finalClient.subscribe(TOPIC, 1);
                        System.out.println("[INFO] Đã subscribe tới topic: " + TOPIC);
                        publishData(finalClient);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void disconnected(MqttDisconnectResponse disconnectResponse) {
                    System.out.println("[WARN] Đã ngắt kết nối: " + disconnectResponse.getReasonString());
                }

                @Override
                public void mqttErrorOccurred(MqttException exception) {
                    System.err.println("[ERROR] Lỗi MQTT: " + exception.getMessage());
                }

                @Override
                public void deliveryComplete(IMqttToken iMqttToken) { }
                @Override
                public void authPacketArrived(int i, MqttProperties mqttProperties) { }
            });

            System.out.println("[INFO] Đang kết nối tới broker: " + BROKER_URL);
            client.connect(options);

            // Keep the program running to listen for messages
            Thread.sleep(Long.MAX_VALUE);

        } catch (MqttException | InterruptedException e) {
            System.err.println("Lý do: " + (e instanceof MqttException ? ((MqttException) e).getReasonCode() : ""));
            System.err.println("Thông báo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.disconnect();
                    client.close();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void publishData(MqttClient client) throws MqttException {
        JSONObject jsonData = new JSONObject();
        jsonData.put("DeviceName", "JavaController");
        jsonData.put("speed", 80);
        jsonData.put("status", "running");

        String payload = jsonData.toString();
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);

        System.out.println("\n[INFO] Đang gửi tin nhắn...");
        System.out.println("-> Topic: " + TOPIC);
        System.out.println("-> Payload: " + payload);
        client.publish(TOPIC, message);
        System.out.println("[SUCCESS] Gửi tin nhắn thành công.");
    }

    private static void parseAndDisplay(String jsonPayload) {
        try {
            JSONObject data = new JSONObject(jsonPayload);
            String deviceName = data.getString("DeviceName");
            int speed = data.getInt("speed");
            String status = data.getString("status");

            System.out.println("--- Dữ liệu đã xử lý ---");
            System.out.println("  Thiết bị: " + deviceName);
            System.out.println("  Tốc độ: " + speed);
            System.out.println("  Trạng thái: " + status);
            System.out.println("------------------------\n");
        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi xử lý JSON: " + e.getMessage());
        }
    }
}
