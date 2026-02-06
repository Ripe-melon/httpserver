# Java HTTP Server & Client (No Frameworks)

A raw HTTP Server and a Client SDK from scratch using Java 11+, Gson, and PostgreSQL. The project now implements the DAO pattern for persistent storage.

## Project Structure
* **Server**: `SimpleHttpServer` listens on port 8080 and delegates to `PostHandler`.
* **Data**: `PostDAO` and `Database` classes handle PostgreSQL connections and SQL queries.
* **Client**: `BlogClient` is a reusable SDK to talk to the server.
* **Shared**: Uses `Post` model (DTO) and custom Exceptions.

## Prerequisites
You must have **PostgreSQL** installed and running.

### 0. Database Setup
Before running the server, create the database and table:

```sql
CREATE DATABASE blogdb;

\c blogdb;

CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    title TEXT,
    body TEXT
);
```

## How to run

### 1. Build & Setup 
Run this command to compile the code and download required libraries (Gson, PostgreSQL Driver) into the build folder.

`mvn clean package dependency:copy-dependencies`


### 2. Start the Server
Make sure your Postgres database is running, then start the application.

# Run the SimpleHttpServer class
`java -cp "target/classes:target/dependency/*" com.gustaf.SimpleHttpServer`


