# JsonDatabase

A client-server application built with Java that allows clients to store and retrieve data on a server in JSON format. This project demonstrates socket programming, concurrent request handling, and JSON-based data storage.

## Overview

JsonDatabase is a multi-threaded server-client system from JetBrains Academy that implements a JSON-based database using Java sockets. The server handles multiple client requests simultaneously, storing data in JSON files with support for nested structures and complex queries.

## Features

### Core Functionality
- **Client-Server Architecture** - Socket-based communication between clients and server
- **JSON Storage** - Data persisted in JSON format on the server
- **Nested JSON Support** - Set and retrieve values inside nested JSON structures using key arrays
- **Concurrent Request Handling** - Multi-threaded server using ExecutorService
- **CRUD Operations** - Set, get, and delete operations
- **File-Based Persistence** - Data stored in JSON files on server filesystem
- **Command-Line Interface** - Execute commands via CLI arguments
- **JSON Input Files** - Load commands from JSON files for complex operations

### Advanced Features
- **Nested Key Paths** - Access nested values using array keys like `["person", "name"]`
- **Automatic Object Creation** - Server creates intermediate objects if they don't exist
- **Selective Deletion** - Delete nested values without removing parent objects
- **Response Protocol** - Structured JSON responses with `OK` or `ERROR` status

## Technologies Used

- **Java** - Core programming language
- **Java Sockets** - Network communication
- **Gson/Jackson** - JSON parsing and serialization
- **ExecutorService** - Multi-threaded request handling
- **File I/O** - Data persistence
- **Gradle** - Build automation

## Architecture

### Client-Server Model
```
Client 1 ──┐
Client 2 ──┼──> [Server] ──> JSON Database File
Client 3 ──┘        │
                    └──> Thread Pool (ExecutorService)
```

### Request/Response Protocol
All communication happens via JSON:

**Request Format:**
```json
{
  "type": "set|get|delete|exit",
  "key": "simple-key" | ["nested", "key", "path"],
  "value": "any JSON value"
}
```

**Response Format:**
```json
{
  "response": "OK|ERROR",
  "value": "returned data (for get)",
  "reason": "error message (if ERROR)"
}
```

## Getting Started

### Prerequisites

- Java JDK 11 or higher
- Gradle (wrapper included)

### Installation & Running

```bash
# Clone the repository
git clone https://github.com/BlackMunda/JsonDatabase.git
cd JsonDatabase

# Build the project
./gradlew clean serverJar clientJar

# Start the server (runs in background)
java -jar build/libs/server-1.0-SNAPSHOT.jar
# Server started!

# In a new terminal, run client commands
java -jar build/libs/client-1.0-SNAPSHOT.jar -t set -k ["person","rocket","spaceOrg","scientist","name"] -v "DevLex"
# Client started!
# Sent: {
  "type": "set",
  "key": [
    "person",
    "rocket",
    "spaceOrg",
    "scientist",
    "name"
  ],
  "value": "DevLex"
  }
# Received: {
  "response": "OK"
  }

```

## Project Structure

```
JsonDatabase/
├── data/
│   └── db.json                     # JSON database file (persistent storage)
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/
│   │   │       └── example/
│   │   │           ├── Server/
│   │   │           │   ├── Main.java              # Server entry point
│   │   │           │   ├── network/
│   │   │           │   │   └── ClientHandler.java # Handles socket clients
│   │   │           │   ├── commands/
│   │   │           │   │   ├── SetCommand.java
│   │   │           │   │   └── DeleteCommand.java
│   │   │           │   └── data/
│   │   │           │       └── Database.java      # JSON DB logic
│   │   │           │
│   │   │           └── Client/
│   │   │               ├── Main.java              # Client entry point
│   │   │               └── data/
│   │   │                   └── file.txt           # Optional command input file
│   │   │
│   │   └── resources/                              # (optional)
│   │
│   └── test/
│       └── java/                                   # (optional tests)
│
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md

```


### 2. Nested JSON Structures
## Usage Examples

### 1. Simple Key-Value Operations

**Set a value:**
```bash
java -jar build/libs/client-1.0-SNAPSHOT.jar -t set -k "1" -v "Hello world!"
# Sent: {"type":"set","key":"1","value":"Hello world!"}
# Received: {"response":"OK"}
```

**Get a value:**
```bash
java -jar build/libs/client-1.0-SNAPSHOT.jar -t get -k "1"
# Sent: {"type":"get","key":"1"}
# Received: {"response":"OK","value":"Hello world!"}
```

**Delete a value:**
```bash
java -jar build/libs/client-1.0-SNAPSHOT.jar -t delete -k "1"
# Sent: {"type":"delete","key":"1"}
# Received: {"response":"OK"}
```

### 2. Nested JSON Structures (using array keys)

**Set a nested object:**
```bash
java -jar build/libs/client-1.0-SNAPSHOT.jar -t set -k "person" -v "{\"name\":\"Elon Musk\",\"car\":{\"model\":\"Tesla Roadster\",\"year\":\"2018\"}}"
# Received: {"response":"OK"}
```

**Access nested values with array key paths:**
```bash
java -jar build/libs/client-1.0-SNAPSHOT.jar -t get -k "[\"person\",\"name\"]"
# Received: {"response":"OK","value":"Elon Musk"}
```

**Update nested values:**
```bash
java -jar build/libs/client-1.0-SNAPSHOT.jar -t set -k "[\"person\",\"car\",\"year\"]" -v "2020"
# Received: {"response":"OK"}
```

**Delete nested values:**
```bash
java -jar build/libs/client-1.0-SNAPSHOT.jar -t delete -k "[\"person\",\"car\",\"year\"]"
# Received: {"response":"OK"}
```

Result: Only `year` is deleted, `car` object remains with just `model`.

### 3. Command-Line Arguments (using JCommander)
```bash
# Set operation
java -jar build/libs/client-1.0-SNAPSHOT.jar -t set -k "key" -v "value"

# Get operation
java -jar build/libs/client-1.0-SNAPSHOT.jar -t get -k "key"

# Delete operation
java -jar build/libs/client-1.0-SNAPSHOT.jar -t delete -k "key"

# Exit the server
java -jar build/libs/client-1.0-SNAPSHOT.jar -t exit
```

**Arguments:**
- `-t` - Request type: `set`, `get`, `delete`, or `exit`
- `-k` - The key (string or JSON array like `["person","name"]`)
- `-v` - The value to set (can be JSON string)

## How It Works

### Server Side
1. **Socket Listener** - Server listens on a port (typically 23456)
2. **Connection Handler** - Each client connection spawns a new task
3. **Thread Pool** - ExecutorService manages concurrent requests
4. **Request Parser** - Deserializes incoming JSON requests
5. **Database Operations** - Performs set/get/delete on JSON file
6. **Response** - Serializes response and sends back to client
7. **File Sync** - Writes changes to `db.json` file

### Client Side
1. **Parse Arguments** - Read command-line args or JSON file
2. **Create Request** - Build JSON request object
3. **Connect to Server** - Establish socket connection
4. **Send Request** - Serialize and transmit JSON
5. **Receive Response** - Read server response
6. **Display Result** - Print response to console
7. **Close Connection** - Clean up resources

### Database File Structure

The `db.json` file might look like:
```json
{
  "1": "Hello world!",
  "person": {
    "name": "Elon Musk",
    "car": {
      "model": "Tesla Roadster"
    },
    "rocket": {
      "name": "Falcon 9",
      "launches": "88"
    }
  },
  "config": {
    "theme": "dark",
    "language": "en"
  }
}
```

## Key Implementation Details

### Nested Key Handling
```java
// For key: ["person", "car", "model"]
// Navigate through: root -> "person" -> "car" -> "model"
// If intermediate objects don't exist, create them
```

### Concurrency
```java
// ExecutorService for handling multiple clients
ExecutorService executor = Executors.newFixedThreadPool(4);

// Each client request runs in separate thread
executor.submit(() -> handleRequest(clientSocket));
```

### JSON Parsing
```java
// Using Gson for serialization/deserialization
Gson gson = new Gson();
Request request = gson.fromJson(jsonString, Request.class);
Response response = new Response("OK", value);
String responseJson = gson.toJson(response);
```

## Learning Outcomes

This project helped me master:
- **Network Programming** - Java socket programming and TCP/IP communication
- **Multi-threading** - Concurrent request handling with ExecutorService
- **JSON Manipulation** - Parsing, serializing, and navigating nested JSON structures
- **Client-Server Architecture** - Request-response protocols and network design
- **File I/O** - Reading and writing JSON files with proper synchronization
- **Command-Line Parsing** - Processing CLI arguments and input files
- **Error Handling** - Network exceptions, file I/O errors, and invalid requests
- **Design Patterns** - Factory pattern for requests, Strategy for operations
- **Synchronization** - Thread-safe file operations and data access

## Future Enhancements

- [ ] Authentication and user permissions
- [ ] Query language for complex data retrieval
- [ ] Indexing for faster lookups
- [ ] Database backup and restore functionality
- [ ] RESTful API wrapper around socket server
- [ ] Data encryption for secure storage
- [ ] Transaction support with rollback
- [ ] Database replication for high availability
- [ ] Monitoring and logging dashboard
- [ ] Support for multiple database files

## Technical Challenges Solved

### 1. Concurrent File Access
**Problem:** Multiple threads accessing the same JSON file
**Solution:** Synchronized methods and locks to ensure data consistency

### 2. Nested Key Navigation
**Problem:** Accessing deeply nested JSON values with array keys
**Solution:** Recursive traversal algorithm that creates intermediate objects

### 3. Socket Management
**Problem:** Handling multiple client connections efficiently
**Solution:** Thread pool with ExecutorService to limit resource usage

### 4. Data Persistence
**Problem:** Balancing in-memory speed with file persistence
**Solution:** Write-through cache that updates file on each operation

## Project Highlights

**From JetBrains Academy:** This project is part of the JetBrains Academy curriculum, designed to teach:
- Network programming fundamentals
- Multi-threaded application development
- Working with JSON data structures
- Client-server architecture patterns
- Concurrent programming best practices

## Contributing

This is a personal learning project, but suggestions and feedback are welcome!

## License

MIT License - feel free to use this code for learning purposes.

## Contact

**Devashish Singh**
- GitHub: [@BlackMunda](https://github.com/BlackMunda)
- Email: devashishsingh488@gmail.com

---

*Built with ☕ and curiosity about database internals*
