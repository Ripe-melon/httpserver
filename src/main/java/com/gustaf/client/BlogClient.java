package com.gustaf.client;

import java.net.http.*;
import com.google.gson.Gson;
import com.gustaf.shared.exceptions.*;
import com.gustaf.shared.models.Post;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class BlogClient {
    private HttpClient httpClient;
    private String baseUrl;
    private Gson gson;

    public BlogClient(String url) {
        this.gson = new Gson();
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = url;
    }

    public Post getPost(int id) {
        String endpoint = String.format("%s/api/posts?id=%d", baseUrl, id);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(endpoint)).build();
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            ensureSuccess(response);
            return gson.fromJson(response.body(), Post.class);
        } catch (IOException | InterruptedException e) {
            throw new BlogApiException("Network failed: ", 0);
        }
    }

    public boolean createPost(Post post) {
        String postJson = gson.toJson(post);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(String.format("%s/api/posts", baseUrl)))
                .header("Content-Type", "application/json").POST(BodyPublishers.ofString(postJson)).build();
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            return ensureSuccess(response);
        } catch (IOException | InterruptedException e) {
            throw new BlogApiException("Network failed: " + e.getMessage(), 0);
        }
    }

    public boolean deletePost(int id) {
        String endpoint = String.format("%s/api/posts?id=%d", baseUrl, id);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .DELETE().build();
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            return ensureSuccess(response);
        } catch (IOException | InterruptedException e) {
            throw new BlogApiException("Network failed: " + e.getMessage(), 0);
        }
    }

    private boolean ensureSuccess(HttpResponse<String> response) {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return true;

        }
        if (status == 404) {
            throw new ResourceNotFoundException("The requested resource could not be found.");
        }
        if (status >= 400 && status < 500) {
            throw new BlogApiException("Client Error: ", status);
        }
        if (status >= 500) {
            throw new ApiServerException("Remothe Server Error", status);
        }
        throw new BlogApiException("Unexpected error occurred", status);
    }
}