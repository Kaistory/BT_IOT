package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiveData {
    void main() throws IOException {
        HttpClient client = HttpClient.newHttpClient();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.thingspeak.com/channels/1529099/feeds.json?results=2"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            String jsonString = response.body();

            System.out.println("Dữ liệu từ ThingSpeak:");
            System.out.println("--------------------");

            Pattern pattern = Pattern.compile("\"field1\":\"(.*?)\",\"field2\":\"(.*?)\"");
            Matcher matcher = pattern.matcher(jsonString);


            int entryCount = 1;
            while (matcher.find()) {
                String temperature = matcher.group(1);
                String humidity = matcher.group(2);
                if(entryCount == 2){
                    System.out.println("  Nhiệt độ (Temperature): " + temperature);
                    System.out.println("  Độ ẩm (Humidity): " + humidity);
                    System.out.println();
                }
                entryCount++;
            }

        } catch (Exception e) {
            System.out.println("Đã xảy ra lỗi khi lấy dữ liệu.");
            e.printStackTrace();
        }
    }
}
