package com.gustaf.shared.data;

import java.sql.SQLException;
import com.gustaf.shared.models.Post;
import java.util.List;

public interface PostDAOInterface {
    Post get(int id) throws SQLException;

    List<Post> getAll() throws SQLException;

    int update(Post post) throws SQLException;

    int save(Post post) throws SQLException;

    int insert(Post post) throws SQLException;

    int delete(int id);

}
