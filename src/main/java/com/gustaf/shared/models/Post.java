package com.gustaf.shared.models;

public class Post {
    int userId;
    int id;
    String title;
    String body;

    public Post(String title, String body) {
        this.id = 0; // 0 indicates "Not saved in DB yet"
        this.title = title;
        this.body = body;
    }

    public Post(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Post [" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ']';
    }
}
