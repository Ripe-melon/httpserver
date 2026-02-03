
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
    @Order(4)
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