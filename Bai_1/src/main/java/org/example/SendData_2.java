package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SendData_2 {

    void main() throws IOException {
        URL url = new URL("https://api.thingspeak.com/update?api_key=T7H40F0X82VGW7L5");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setDoOutput(true);

        String jsonData = "{\"field1\":20,\"field2\":33}";
        con.getOutputStream().write(jsonData.getBytes("utf-8"));
        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if(responseCode == HttpURLConnection.HTTP_OK){
            System.out.println("Data sent successfully.");
        } else {
            System.out.println("Failed to send data.");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        con.disconnect();
    }
}
