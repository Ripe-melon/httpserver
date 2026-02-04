package com.gustaf.shared.data;

import java.sql.*;

import com.gustaf.shared.models.Post;
import java.util.List;

public class PostDaoImplementation implements PostDAOInterface {

    @Override
    public Post get(int id) throws SQLException {
        // Implementation here
        return null;
    }

    @Override
    public List<Post> getAll() throws SQLException {
        // Implementation here
        return null;
    }

    @Override
    public int update(Post post) throws SQLException {
        // Implementation here
        return 0;
    }

    @Override
    public int save(Post post) throws SQLException {
        // Implementation here
        return 0;
    }

    @Override
    public int insert(Post post) throws SQLException {
        Connection conn = Database.getConnection();

        String sql = "INSERT INTO posts (title, body) VALUES (?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, post.getTitle());
        ps.setString(2, post.getBody());

        int result = ps.executeUpdate();

        conn.close();
        ps.close();

        return result;
    }

    @Override
    public int delete(Post post) {
        // Implementation here
        return 0;
    }
}
