
import com.gustaf.client.BlogClient;
import com.gustaf.server.SimpleHttpServer;
import com.gustaf.shared.data.Database;
import com.gustaf.shared.data.PostDaoImplementation;
import com.gustaf.shared.models.Post;

import org.checkerframework.checker.units.qual.s;
import org.junit.jupiter.api.*; // Import JUnit magic

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*; // Import Assertions

// We want to run tests in a specific order (Create -> Get -> Delete)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BlogApiTest {

    private static SimpleHttpServer server;
    private static BlogClient client;
    private static Database db;
    private static PostDaoImplementation postDao;

    // "Run this ONCE before any test starts"
    @BeforeAll
    static void setup() throws IOException {
        db = new Database();
        postDao = new PostDaoImplementation(db);
        // 1. Start the Server
        server = new SimpleHttpServer(db);
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
    void testGetPost() throws Exception { // Add throws Exception just in case
        System.out.println("Test 2: Get (GET) from DB");

        // 1. Arrange: "Seed" the database directly (Bypassing the API)
        // We do this because we haven't updated 'handleCreate' yet!
        Post setupPost = new Post("DB Title", "DB Body Content");

        // We use the DAO to force this into Postgres immediately.
        // Assuming your insert method returns the new ID (e.g., 1)
        int newId = postDao.insert(setupPost);

        System.out.println("Inserted Post ID: " + newId);

        // 2. Act: Ask the API to fetch it
        Post fetched = client.getPost(newId);

        // 3. Assert: The API should have found what the DAO inserted
        assertNotNull(fetched, "Should return a Post object, not null");
        assertEquals("DB Title", fetched.getTitle(), "Titles should match database data");
        assertEquals(newId, fetched.getId(), "IDs should match");
    }

    @Test
    @Order(3)
    void testInsertPost() throws Exception {
        System.out.println("Test 3: Insert (Post) to DB");

        Post insertPost = new Post("DB Insert title", "Inser Body Content");

        client.createPost(insertPost);

        System.out.println("Inserted post:" + insertPost.getId());

        System.out.println("Inserted post:" + insertPost.toString());
        System.out.println("Fetched post:" + client.getPost(insertPost.getId()).toString());

        assertEquals("DB Insert title", client.getPost(insertPost.getId()).getTitle(),
                "Titles should match database data");

    }
}

// @Test
// @Order(2)
// void testGetPost() {
// // Act
// // We assume ID is 1 because it's the first post we created
// Post fetched = client.getPost(1);

// // Assert
// assertNotNull(fetched, "Should return a Post object, not null");
// assertEquals("JUnit Test", fetched.getTitle(), "Titles should match");
// }

// @Test
// @Order(3)
// void testUpdatePost() {
// System.out.println("Test 3: Update (PUT)");

// // 1. Arrange: Prepare the "New Box" with updated content
// // Note: The ID here doesn't matter much if your server forces the ID from
// the
// // URL,
// // but it's good practice to keep it consistent.
// Post updatedData = new Post(1, "Updated Title", "This content was changed via
// PUT!");

// // 2. Act: Send the PUT request targeting ID 1
// // (Assuming your method is named 'updatePost')
// boolean success = client.updatePost(1, updatedData);

// // 3. Assert: Verify the method returned true
// assertTrue(success, "Update should return true");

// // 4. Verification: Fetch the post again to prove the Server's memory
// actually
// // changed
// Post fetchedPost = client.getPost(1);

// // Check that the title is now "Updated Title" and NOT the old title
// assertEquals("Updated Title", fetchedPost.getTitle(), "Title should have been
// updated");
// assertEquals("This content was changed via PUT!", fetchedPost.getBody(),
// "Body should have been updated");

// // Optional: Ensure the ID didn't accidentally change
// assertEquals(1, fetchedPost.getId(), "ID should remain 1");
// }

// @Test
// @Order(4)
// void testDeletePost() {
// // Act
// boolean deleted = client.deletePost(1);

// // Assert
// assertTrue(deleted, "deletePost should return true");

// // Extra Verification: Try to fetch it again, should fail!
// // This is a "Negative Test" - we expect an error.
// assertThrows(RuntimeException.class, () -> {
// client.getPost(1);
// });
// }

// @Test
// @Order(5)
// void testUniqueIds() {
// // Arrange: Create two different post objects
// Post post1 = new Post(0, "First", "Body");
// Post post2 = new Post(0, "Second", "Body");

// // Act: Send both
// client.createPost(post1); // Server assigns ID 1
// client.createPost(post2); // Server assigns ID 2

// // Act 2: Fetch them back to check IDs
// Post fetched1 = client.getPost(1);
// Post fetched2 = client.getPost(2);

// // Assert: Check that IDs are not the same
// assertNotEquals(fetched1.getId(), fetched2.getId());
// }
// }