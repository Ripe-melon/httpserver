package com.gustaf.server;

import com.gustaf.server.handlers.PostHandler;
import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
    private PostHandler postHandler;
    private HttpServer server;

    public SimpleHttpServer() {
        postHandler = new PostHandler();
    }

    public static void main(String[] args) throws IOException {
        new SimpleHttpServer().start();
    }

    public void start() throws IOException {
        // Create http server
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Route the traffic
        server.createContext("/api/posts", postHandler);

        // Enable thread pooling/Multi-threading
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));

        // Start the server
        server.start();
        System.out.println("Server started on port 8080");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped.");
        }
    }

}