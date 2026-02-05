package com.gustaf.server.data;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import com.gustaf.shared.data.Database;
import com.gustaf.shared.data.PostDaoImplementation;
import com.gustaf.shared.models.Post;

import java.sql.Statement;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    private static Database db;
    private static PostDaoImplementation postDaoImplementation;

    @BeforeAll
    static void setup() {
        db = new Database();
        postDaoImplementation = new PostDaoImplementation(db);
    }

    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (Connection conn = db.getConnection()) {
            Statement statement = conn.createStatement();
            statement.execute("TRUNCATE TABLE posts RESTART IDENTITY");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void testCanConnectToDatabase() throws SQLException {
        System.out.println("Testing Database Connection...");

        Connection localConn = db.getConnection();

        // 2. Assert: Check if it's alive
        assertNotNull(localConn, "Connection should not be null");

        // isValid(2) sends a tiny "ping" to the DB.
        // "2" is the timeout in seconds.
        assertTrue(localConn.isValid(2), "Connection should be valid and open");

        // 3. Cleanup: Always close connections in tests!
        localConn.close();
        assertTrue(localConn.isClosed(), "Connection should be closed");

        System.out.println("Connection Successful!");
    }

    @Test
    @Order(2)
    void testInsertToDatabase() throws SQLException {

        Post post = new Post("My post", "Testing my post in the DB!");

        int result = postDaoImplementation.insert(post);

        assertEquals(1, result, "Should return 1!");

        if (result == 1) {
            System.out.println("Insert Successful!");
        }
    }

    @Test
    @Order(3)
    void testGetPostDatabase() throws SQLException {
        Post testPost = new Post("My post number 1", "Testing my post in the DB!");
        Post testPost2 = new Post("My post number 2", "Testing my post in the DB!");
        Post testPost3 = new Post("My post number 3", "Testing my post in the DB!");
        Post testPost4 = new Post("My post number 4", "Testing my post in the DB!");

        postDaoImplementation.insert(testPost);
        postDaoImplementation.insert(testPost2);
        postDaoImplementation.insert(testPost3);
        postDaoImplementation.insert(testPost4);

        Post post = postDaoImplementation.get(4);

        assertNotNull(testPost4, "Should find the post we just inserted");
        assertEquals(testPost4.getTitle(), post.getTitle(), "Titles should match");

        System.out.println(testPost.toString());
        System.out.println(post.toString());

        if (post.equals(testPost)) {
            System.out.println("Get Post Successful!");
        }
    }

    @Test
    @Order(4)
    void testGetAllPosts() throws SQLException {
        // 1. Arrange: Create and Insert multiple posts
        Post p1 = new Post("First Post", "Content of first post");
        Post p2 = new Post("Second Post", "Content of second post");
        Post p3 = new Post("Third Post", "Content of third post");

        postDaoImplementation.insert(p1);
        postDaoImplementation.insert(p2);
        postDaoImplementation.insert(p3);

        // 2. Act: Fetch the whole list
        // (Make sure to import java.util.List at the top of your file)
        List<Post> allPosts = postDaoImplementation.getAll();

        // 3. Assert

        // Check the Count: We put in 3, we should get out 3.
        assertEquals(3, allPosts.size(), "Should return exactly 3 posts");

        // Check the Content: Spot check the first one to match 'p1'
        // Since we RESTART IDENTITY, the first one inserted is usually index 0
        Post firstResult = allPosts.get(0);
        assertEquals(p1.getTitle(), firstResult.getTitle());
        assertEquals(p1.getBody(), firstResult.getBody());

        // Spot check the last one
        Post lastResult = allPosts.get(2);
        assertEquals(p3.getTitle(), lastResult.getTitle());
    }

    @Test
    @Order(5)
    void testUpdatePost() throws SQLException {
        // 1. Arrange
        Post original = new Post("Original Title", "Original Body");
        postDaoImplementation.insert(original); // This becomes ID 1

        // 2. Act
        // FIX: Use the constructor that sets the ID to 1
        Post postToUpdate = new Post(1, "Updated Title", "Updated Body");

        // Now it sends: UPDATE ... WHERE id = 1;
        int rowsAffected = postDaoImplementation.update(postToUpdate);

        // 3. Assert
        // Verify the update actually happened!
        assertEquals(1, rowsAffected, "Update should affect exactly 1 row");

        Post updatedResult = postDaoImplementation.get(1);
        assertEquals("Updated Title", updatedResult.getTitle());
    }

}