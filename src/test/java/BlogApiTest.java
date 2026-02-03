
import com.gustaf.client.BlogClient;
import com.gustaf.server.SimpleHttpServer;
import com.gustaf.shared.models.Post;
import org.junit.jupiter.api.*; // Import JUnit magic

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*; // Import Assertions

// We want to run tests in a specific order (Create -> Get -> Delete)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BlogApiTest {

    private static SimpleHttpServer server;
    private static BlogClient client;

    // "Run this ONCE before any test starts"
    @BeforeAll
    static void setup() throws IOException {
        // 1. Start the Server
        server = new SimpleHttpServer();
        server.start();

        // 2. Setup the Client
        client = new BlogClient("http://localhost:8080");
    }

    // "Run this ONCE after all tests are done"
    @AfterAll
    static void tearDown() {
        server.stop(); // Kill the server cleanly
    }

    @Test
    @Order(1)
    void testCreatePost() {
        // Arrange (Prepare data)
        Post post = new Post(0, "JUnit Test", "This is an auto-test");
        Post post2 = new Post(0, "JUnit Test2", "This is an auto-test");

        // Act (Do the action)
        boolean result = client.createPost(post);

        // Assert (Verify the result)
        assertTrue(result, "createPost should return true");
    }

    @Test
    @Order(2)
    void testGetPost() {
        // Act
        // We assume ID is 1 because it's the first post we created
        Post fetched = client.getPost(1);

        // Assert
        assertNotNull(fetched, "Should return a Post object, not null");
        assertEquals("JUnit Test", fetched.getTitle(), "Titles should match");
    }

    @Test
    @Order(3) 
    void testUpdatePost() {
        System.out.println("Test 3: Update (PUT)");

        // 1. Arrange: Prepare the "New Box" with updated content
        // Note: The ID here doesn't matter much if your server forces the ID from the
        // URL,
        // but it's good practice to keep it consistent.
        Post updatedData = new Post(1, "Updated Title", "This content was changed via PUT!");

        // 2. Act: Send the PUT request targeting ID 1
        // (Assuming your method is named 'updatePost')
        boolean success = client.updatePost(1, updatedData);

        // 3. Assert: Verify the method returned true
        assertTrue(success, "Update should return true");

        // 4. Verification: Fetch the post again to prove the Server's memory actually
        // changed
        Post fetchedPost = client.getPost(1);

        // Check that the title is now "Updated Title" and NOT the old title
        assertEquals("Updated Title", fetchedPost.getTitle(), "Title should have been updated");
        assertEquals("This content was changed via PUT!", fetchedPost.getBody(), "Body should have been updated");

        // Optional: Ensure the ID didn't accidentally change
        assertEquals(1, fetchedPost.getId(), "ID should remain 1");
    }

    @Test
    @Order(4)
    void testDeletePost() {
        // Act
        boolean deleted = client.deletePost(1);

        // Assert
        assertTrue(deleted, "deletePost should return true");

        // Extra Verification: Try to fetch it again, should fail!
        // This is a "Negative Test" - we expect an error.
        assertThrows(RuntimeException.class, () -> {
            client.getPost(1);
        });
    }

    @Test
    @Order(5)
    void testUniqueIds() {
        // Arrange: Create two different post objects
        Post post1 = new Post(0, "First", "Body");
        Post post2 = new Post(0, "Second", "Body");

        // Act: Send both
        client.createPost(post1); // Server assigns ID 1
        client.createPost(post2); // Server assigns ID 2

        // Act 2: Fetch them back to check IDs
        Post fetched1 = client.getPost(1);
        Post fetched2 = client.getPost(2);

        // Assert: Check that IDs are not the same
        assertNotEquals(fetched1.getId(), fetched2.getId());
    }
}