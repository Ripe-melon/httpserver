
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

        Post insertPost = new Post("DB Insert title", "Insert Body Content");

        client.createPost(insertPost);

        System.out.println("Inserted post:" + insertPost.getId());

        System.out.println("Inserted post:" + insertPost.toString());
        System.out.println("Fetched post:" + client.getPost(insertPost.getId()).toString());

        assertEquals("DB Insert title", client.getPost(insertPost.getId()).getTitle(),
                "Titles should match database data");

    }

    @Test
    @Order(4)
    void testDeletePost() throws Exception {
        System.out.println("Test 4: Delete (Post) from DB");

        Post setUpPost = new Post("DB Post To Delete", "This is the body of the post to delete");

        client.createPost(setUpPost);

        System.out.println("Inserted post:" + setUpPost.getId());

        boolean deleted = client.deletePost(setUpPost.getId());

        assertTrue(deleted, "Should return true!");
        assertThrows(RuntimeException.class, () -> {
            client.getPost(setUpPost.getId());
        });
    }

    @Test
    @Order(5)
    void testUpdatePost() throws Exception {
        System.out.println("Test 5: Update (Post) in DB");

        Post setUpPost = new Post("DB Post To Update", "This is the body of the post to update");

        client.createPost(setUpPost);
        System.out.println("Inserted post:" + setUpPost.getId());

        Post updatePost = new Post("DB Post That Is Updated", "This is the body of the post that is updated");

        boolean success = client.updatePost(setUpPost.getId(), updatePost);

        // 3. Assert: Verify the method returned true
        assertTrue(success, "Update should return true");

        // Verify that the post actually updated changed
        Post fetchedPost = client.getPost(setUpPost.getId());

        // Check that the title is now "Updated Title" and NOT the old title
        assertEquals("DB Post That Is Updated", fetchedPost.getTitle(), "Title should have been updated");
        assertEquals("This is the body of the post that is updated", fetchedPost.getBody(),
                "Body should have been updated");

        // Optional: Ensure the ID didn't accidentally change
        assertEquals(setUpPost.getId(), fetchedPost.getId(), "ID should remain the same");

    }
}
