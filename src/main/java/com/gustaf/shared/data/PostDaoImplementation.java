package com.gustaf.shared.data;

import java.sql.*;

import com.gustaf.shared.models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDaoImplementation implements PostDAOInterface {
    private Database db;

    public PostDaoImplementation(Database db) {
        this.db = db;
    }

    @Override
    public Post get(int id) throws SQLException {
        Post post = null;
        try (Connection conn = db.getConnection()) {
            String sql = "SELECT id, title, body FROM posts WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int postId = rs.getInt("id");
                        String title = rs.getString("title");
                        String body = rs.getString("body");

                        post = new Post(postId, title, body);
                    }
                }
            }
        }
        return post;
    }

    @Override
    public List<Post> getAll() throws SQLException {
        List<Post> posts = new ArrayList();
        try (Connection conn = db.getConnection()) {
            String sql = "SELECT id, title, body FROM posts";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String title = rs.getString("title");
                        String body = rs.getString("body");

                        Post post = new Post(id, title, body);
                        posts.add(post);
                    }
                }
            }
        }
        return posts;
    }

    @Override
    public int update(Post post) throws SQLException {
        int result = 0;
        try (Connection conn = db.getConnection()) {
            String sql = "UPDATE posts SET title = ?, body = ? WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, post.getTitle());
                ps.setString(2, post.getBody());
                ps.setInt(3, post.getId());

                result = ps.executeUpdate();
            }
        }
        return result;
    }

    @Override
    public int save(Post post) {
        return 0;
    }

    @Override
    public int insert(Post post) throws SQLException {
        int result = 0;
        try (Connection conn = db.getConnection()) {

            String sql = "INSERT INTO posts (title, body) VALUES (?, ?) RETURNING id";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, post.getTitle());
                ps.setString(2, post.getBody());

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    result = rs.getInt("id");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                conn.close();
                ps.close();
            }
        }
        return result;
    }

    @Override
    public int delete(Post post) {
        int result = 0;
        try (Connection conn = db.getConnection()) {

            String sql = "DELETE FROM posts WHERE post WHERE id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, post.getId());

            result = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
