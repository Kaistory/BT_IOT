import com.google.gson.annotations.SerializedName;

public class SensorData {

    private String deviceName;

    private int temperature;

    private int humidity;

    // Constructor
    public SensorData(String deviceName, int temperature, int humidity) {
        this.deviceName = deviceName;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    // Getters/Setters (Gson cần chúng)
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    // Phương thức toString để in cho đẹp (tùy chọn)
    @Override
    public String toString() {
        return "SensorData{" +
                "deviceName='" + deviceName + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}