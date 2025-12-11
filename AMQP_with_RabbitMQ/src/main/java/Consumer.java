import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject; // Thư viện để parse JSON

public class Consumer {

    // Tên của hàng đợi, PHẢI GIỐNG với tên Producer đã dùng
    private final static String QUEUE_NAME = "json_data_queue";

    public static void main(String[] argv) throws Exception {
        // 1. Khởi tạo ConnectionFactory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // 2. Tạo kết nối và kênh
        // Không dùng try-with-resources ở đây vì chúng ta muốn
        // kết nối và kênh sống sót để lắng nghe thông điệp liên tục.
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 3. Khai báo hàng đợi (queue)
        // Đảm bảo rằng queue tồn tại. Lệnh này an toàn để gọi ở cả 2 phía.
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println(" [*] Đang chờ thông điệp. Để thoát nhấn CTRL+C");

        // 4. Thiết lập callback để xử lý thông điệp
        // DeliverCallback sẽ được gọi bất đồng bộ khi có thông điệp đến.
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // Lấy nội dung thông điệp (dưới dạng byte[])
            byte[] body = delivery.getBody();
            // Chuyển về chuỗi String UTF-8
            String message = new String(body, StandardCharsets.UTF_8);

            System.out.println("\n---------------------------------");
            System.out.println(" [x] Đã nhận: '" + message + "'");

            try {
                // 5. Bóc tách (parse) JSON
                JSONObject json = new JSONObject(message);

                String deviceName = json.getString("DeviceName");
                int temperature = json.getInt("temperature");
                int humidity = json.getInt("humidity");

                // 6. Hiển thị dữ liệu đã bóc tách
                System.out.println("   -> Device: " + deviceName);
                System.out.println("   -> Temp:   " + temperature);
                System.out.println("   -> Humi:   " + humidity);

            } catch (Exception e) {
                System.err.println(" [!] Lỗi parse JSON: " + e.getMessage());
            } finally {
                // Gửi xác nhận (acknowledgment) rằng đã xử lý xong thông điệp
                // (Trong ví dụ này, autoAck=true nên không bắt buộc,
                // nhưng đây là thói quen tốt khi autoAck=false)
                // channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        // 5. Bắt đầu lắng nghe (consume)
        // autoAck = true: Tự động gửi xác nhận (ack) ngay khi nhận được thông điệp.
        // Nếu Consumer bị crash, thông điệp sẽ bị MẤT.
        // (Trong thực tế, bạn nên đặt autoAck = false và gửi ack thủ công
        // sau khi xử lý xong)
        channel.basicConsume(
                QUEUE_NAME,       // Tên queue
                true,             // autoAck
                deliverCallback,  // Callback khi nhận được thông điệp
                consumerTag -> {} // Callback khi consumer bị hủy (cancel)
        );
    }
}