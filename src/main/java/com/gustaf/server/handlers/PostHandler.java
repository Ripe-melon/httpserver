package com.gustaf.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.gustaf.shared.models.Post;
import com.sun.net.httpserver.*;

public class PostHandler implements HttpHandler {
    private static List<Post> posts = new ArrayList<>();
    private Gson gson;

    public PostHandler() {
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        System.out.println("Received a " + method + " request!");

        if ("GET".equals(method)) {
            handleGet(exchange);
        } else if ("POST".equals(method)) {
            handlePost(exchange);
            System.out.println();
        } else {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        Map<String, String> params = parseQuery(query);

        if (params.containsKey("id")) {
            int idParam = Integer.parseInt(params.get("id"));

            Post foundPost = null;
            for (Post post : posts) {
                if (post.getId() != null && post.getId() == idParam) {
                    foundPost = post;
                    break;
                }
            }
            if (foundPost != null) {
                // Success: Return just the found object
                String json = gson.toJson(foundPost);
                sendResponse(exchange, 200, json);
            } else {
                // Fail: ID doesn't exist
                sendResponse(exchange, 404, "{\"error\": \"Post not found\"}");
            }
        } else {
            String json = gson.toJson(posts);
            sendResponse(exchange, 200, json);
        }

    }

    private void handlePost(HttpExchange exchange) throws IOException {
        // Read request
        InputStreamReader input = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(input);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        // convert JSON string to Java object
        Post newPost = gson.fromJson(sb.toString(), Post.class);

        // Assign ID
        newPost.setId(posts.size() + 1);
        posts.add(newPost);

        // Respond
        System.out.println("Created new post: " + newPost.getTitle());
        sendResponse(exchange, 201, "{\"message\": \"Post created\", \"id\": " + newPost.getId() + "}");

    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }

    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();

        if (query == null || query.isEmpty()) {
            return result;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                result.put(key, value);
            }
        }
        return result;
    }
}
