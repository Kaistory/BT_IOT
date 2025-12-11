import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;

public class Producer {

    // Tên của hàng đợi (queue)
    private final static String QUEUE_NAME = "json_data_queue";

    public static void main(String[] argv) throws Exception {
        // 1. Khởi tạo ConnectionFactory
        ConnectionFactory factory = new ConnectionFactory();

        // Cài đặt để kết nối đến broker trên máy local
        factory.setHost("localhost");
        // Bạn cũng có thể set user/pass nếu cần:
         factory.setUsername("guest");
         factory.setPassword("guest");

        // Sử dụng try-with-resources để đảm bảo kết nối (Connection)
        // và kênh (Channel) được tự động đóng lại sau khi dùng.
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // 2. Khai báo một hàng đợi (queue)
            // Lệnh này có tính "idempotent" - nó sẽ chỉ tạo queue nếu chưa tồn tại.
            // (Tên queue, durable, exclusive, autoDelete, arguments)
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            // 3. Tạo thông điệp JSON
            String deviceName = "Producer-1";
            int temperature = 30;
            int humidity = 60;

            // Xây dựng chuỗi JSON
            // (Trong dự án thực tế, bạn nên dùng thư viện như Gson hoặc Jackson
            // để chuyển đổi từ Object sang JSON một cách an toàn)
            String message = String.format(
                    "{\"DeviceName\":\"%s\", \"temperature\":%d, \"humidity\":%d}",
                    deviceName, temperature, humidity
            );

            // 4. Gửi thông điệp
            channel.basicPublish(
                    "",           // exchange: Dùng default exchange (nameless)
                    QUEUE_NAME,   // routingKey: Tên của queue
                    null,         // props: Các thuộc tính khác (ví dụ: message persistence)
                    message.getBytes(StandardCharsets.UTF_8) // body: Nội dung message
            );

            System.out.println(" [x] Đã gửi thông điệp: '" + message + "'");

        } catch (Exception e) {
            System.err.println("Lỗi kết nối hoặc gửi thông điệp: " + e.getMessage());
            e.printStackTrace();
        }
    }
}