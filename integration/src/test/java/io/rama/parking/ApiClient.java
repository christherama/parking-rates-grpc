package io.rama.parking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    private String server;

    public ApiClient(String server) {
        this.server = server;
    }

    public ApiRateResponse get(String uri) throws IOException {
        URL url = new URL(server + uri);
        System.out.println("URL: " + url.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();
        InputStream inputStream = connection.getResponseCode() < 400 ?
                connection.getInputStream() :
                connection.getErrorStream();
        String body = IOUtils.toString(inputStream);
        return new ApiRateResponse(connection.getResponseCode(), body);
    }

    @Getter
    public static class ApiRateResponse {
        private int code;
        private RateDto rate;

        public ApiRateResponse(int code, String body) throws IOException {
            this.code = code;
            this.rate = new ObjectMapper().readValue(body, RateDto.class);
        }
    }

    @Data
    public static class RateDto {
        private String status;
        private int rate;
    }
}