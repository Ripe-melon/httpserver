package com.gustaf.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.gustaf.shared.models.Post;
import com.gustaf.shared.data.*;
import com.sun.net.httpserver.*;

public class PostHandler implements HttpHandler {
    private Gson gson;
    private Database db;
    private PostDaoImplementation postDaoImpl;

    public PostHandler(Database db) {
        this.gson = new Gson();
        this.db = db;
        this.postDaoImpl = new PostDaoImplementation(db);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        System.out.println("Received a " + method + " request!");

        if ("GET".equals(method)) {
            handleGet(exchange);
        } else if ("POST".equals(method)) {
            handlePost(exchange);
        } else if ("DELETE".equals(method)) {
            handleDelete(exchange);
        } else if ("PUT".equals(method)) {
            handlePut(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Post foundPost;

        Map<String, String> params = parseQuery(query);

        if (params.containsKey("id")) {
            int idParam = Integer.parseInt(params.get("id"));

            try {
                foundPost = postDaoImpl.get(idParam);

            } catch (SQLException e) {
                sendResponse(exchange, 400, "{\"error\": \"Database access error\"}");
                return;
            }

            if (foundPost != null) {
                String json = gson.toJson(foundPost);
                sendResponse(exchange, 200, json);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Post not found\"}");
            }
        } else {
            try {
                List<Post> dbPosts = postDaoImpl.getAll();
                String json = gson.toJson(dbPosts);
                sendResponse(exchange, 200, json);
            } catch (SQLException e) {
                sendResponse(exchange, 500, "{\"error\": \"Database error\"}");
            }
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

        // Add a new post to the database
        try {
            postDaoImpl.insert(newPost);
        } catch (SQLException e) {
            sendResponse(exchange, 400, "{\"error\": \"Database access error\"}");
        }

        // Respond
        System.out.println("Created new post: " + newPost.getTitle());
        sendResponse(exchange, 201, "{\"message\": \"Post created\", \"id\": " + newPost.getId() + "}");

    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        int removed = 0;
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);

        // 1. Validation
        if (!params.containsKey("id")) {
            sendResponse(exchange, 400, "{\"error\": \"Missing ID\"}");
            return;
        }

        int idParam;
        try {
            idParam = Integer.parseInt(params.get("id"));
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid ID format\"}");
            return;
        }
        // 2. Deletion

        try {
            removed = postDaoImpl.delete(idParam);
        } catch (SQLException e) {
            sendResponse(exchange, 400, "{\"error\": \"Database access error\"}");
            return;
        }
        if (removed == 1) {
            sendResponse(exchange, 204, null);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Post not found\"}");
        }
    }

    public void handlePut(HttpExchange exchange) throws IOException {
        int found = 0;
        int idParam;
        String query = exchange.getRequestURI().getQuery();

        Map<String, String> params = parseQuery(query);
        if (!params.containsKey("id")) {
            sendResponse(exchange, 400, "{\"error\": \"Missing ID\"}");
            return;
        }

        try {
            idParam = Integer.parseInt(params.get("id"));
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid ID format\"}");
            return;
        }
        InputStreamReader input = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(input);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        Post incomingPost = gson.fromJson(sb.toString(), Post.class);
        try {
            found = postDaoImpl.update(idParam, incomingPost);
        } catch (SQLException e) {
            sendResponse(exchange, 400, "{\"error\": \"Database access error\"}");
        }
        if (found == 1) {
            sendResponse(exchange, 200, "{\"message\": \"Post updated\", \"id\": " + incomingPost.getId() + "}");
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Post not found\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        if (statusCode == 204) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

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
