package com.example.school_managment.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Minimal REST client to interact with the Spring Boot backend.
 * Stores JWT in a static field after successful login.
 */
public class ApiClient {

    // Adjust if your backend runs on a different host/port
    private static final String BASE_URL = "http://localhost:8083";

    // We'll store the token globally once we login
    public static String jwtToken = null;

    private static final HttpClient CLIENT = HttpClient.newBuilder()
                                                       .connectTimeout(Duration.ofSeconds(5))
                                                       .build();

    /**
     * Send a POST /auth/login request, store the JWT if success.
     *
     * @param username the user’s username
     * @param password the user’s password
     * @return the entire JSON response body if successful
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public static String login(String username, String password) throws IOException, InterruptedException {
        // Minimal JSON body building
        String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String body = response.body();
            // parse out the token from JSON
            String tokenValue = extractJsonField(body, "token");
            jwtToken = tokenValue;
            return body;
        } else {
            throw new IOException("Login failed");
        }
    }

   public static HttpRequest buildRequest(String endpoint, String method, String body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint));

        // add JWT if we have it
        if (jwtToken != null) {
            builder.header("Authorization", "Bearer " + jwtToken);
        }

        builder.header("Content-Type", "application/json");

        switch (method.toUpperCase()) {
            case "GET":
                builder.GET();
                break;
            case "POST":
                builder.POST(HttpRequest.BodyPublishers.ofString(body == null ? "" : body));
                break;
            case "PUT":
                builder.PUT(HttpRequest.BodyPublishers.ofString(body == null ? "" : body));
                break;
            case "DELETE":
                builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("Unknown HTTP method: " + method);
        }

        return builder.build();
    }

    public static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }


    /**
     * Example GET request with optional JWT auth header if present.
     * @param endpoint e.g. "/students"
     * @return response body if success
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public static String get(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET();

        if (jwtToken != null) {
            builder.header("Authorization", "Bearer " + jwtToken);
        }
        HttpRequest request = builder.build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("GET " + endpoint + " failed: " + response.statusCode()
                    + " body: " + response.body());
        }
    }

    /**
     * Quick naive approach to parse JSON field from the response,
     * purely to keep it minimal. For real usage, use a JSON library.
     */
    private static String extractJsonField(String json, String fieldName) {
        // e.g. looking for: "token":"someJWT"
        String needle = "\"" + fieldName + "\":\"";
        int startIndex = json.indexOf(needle);
        if (startIndex == -1) return null;
        startIndex += needle.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return null;
        return json.substring(startIndex, endIndex);
    }
}
