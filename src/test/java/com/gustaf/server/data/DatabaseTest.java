package com.gustaf.server.data;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import com.gustaf.shared.data.Database;
import com.gustaf.shared.data.PostDaoImplementation;
import com.gustaf.shared.models.Post;

import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @Test
    @Order(1)
    void testCanConnectToDatabase() throws SQLException {
        System.out.println("Testing Database Connection...");

        // 1. Arrange & Act: Ask your class for a connection
        // (If your method is static, call Database.getConnection())
        // (If it's an instance, do new Database().getConnection())
        Database db = new Database();
        Connection conn = db.getConnection();

        // 2. Assert: Check if it's alive
        assertNotNull(conn, "Connection should not be null");

        // isValid(2) sends a tiny "ping" to the DB.
        // "2" is the timeout in seconds.
        assertTrue(conn.isValid(2), "Connection should be valid and open");

        // 3. Cleanup: Always close connections in tests!
        conn.close();
        assertTrue(conn.isClosed(), "Connection should be closed");

        System.out.println("Connection Successful!");
    }

    @Test
    @Order(2)
    void testInsert() throws SQLException {

        Post post = new Post("My post", "Testing my post in the DB!");

        PostDaoImplementation postDaoImplementation = new PostDaoImplementation();

        int result = postDaoImplementation.insert(post);

        assertEquals(1, result, "Should return 1!");

        if (result == 1) {
            System.out.println("Insert Successful!");
        }
    }
}