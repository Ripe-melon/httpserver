package com.gustaf.client;

import com.gustaf.shared.exceptions.ResourceNotFoundException;
import com.gustaf.shared.models.Post;

public class App {
    public static void main(String[] args) {
        BlogClient client = new BlogClient("http://localhost:8080");

        System.out.println("--- 1. Creating a Post ---");
        Post myPost = new Post(1, "Java Client Test", "I created this via my own SDK!");

        boolean success = client.createPost(myPost);
        System.out.println("Creation Success: " + success);

        System.out.println("\n--- 2. Fetching the Post ---");
        try {
            // We ask for ID 1 (since your server assigns IDs sequentially)
            Post fetchedPost = client.getPost(1);
            System.out.println("Title: " + fetchedPost.getTitle());
            System.out.println("Body:  " + fetchedPost.getBody());
        } catch (ResourceNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("\n--- 3. Deleting the Post---");
        try {
            boolean successTwo = client.deletePost(1);
            System.out.println("Deletion Succes:" + successTwo);
        } catch (ResourceNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}