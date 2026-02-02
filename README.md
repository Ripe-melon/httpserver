# Java HTTP Server & Client (No Frameworks)

A raw HTTP Server and a Client SDK from scratch using Java 11+ and Gson.

## Project Structure
* Server: `SimpleHttpServer` listens on port 8080.
* Client: `BlogClient` is a reusable SDK to talk to the server.

* Shared: Uses `Post` model and custom Exceptions for error handling.

## How to Run

### 1. Build & Setup
You must run this command to compile the code and download the required libraries (like gson) into the build folder

```bash
mvn clean package dependency:copy-dependencies
```

### 2. Start the Server
The server must be running first.
```bash
# Run the SimpleHttpServer class
java -cp target/classes:target/dependency/* com.gustaf.SimpleHttpServer
```

