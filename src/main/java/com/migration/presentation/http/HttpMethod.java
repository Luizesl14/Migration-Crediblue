package com.migration.presentation.http;

import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Resource
public class HttpMethod {

    private static final Integer MINUTO = (60 * 1000) * 30;


    public Object get(String url) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Authorization", "Bearer "+this.getToken())
                .uri(URI.create(url))
                .build();
        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }


//    @Scheduled(fixedDelay = MINUTO)
    public String getToken() throws IOException, InterruptedException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", "crediblue");
        parameters.put("password", "123");
        parameters.put("grant_type", "password");

        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/oauth/token"))
                .header("Authorization", basicAuth("username", "password"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder().build();
        httpClient.authenticator();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }


    private static String basicAuth(String username, String password) {
        username = "angular";
        password = "@ngul@r0";
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

}
