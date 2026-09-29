# Digital Space — Backend

The backend for **Digital Space**, a room-based study and productivity platform that enables users to create or join study rooms, collaborate in real time, manage goals, track study sessions, exchange messages, and use AI-powered word lookup.

The backend is built using **Java and Spring Boot** and provides REST APIs for persistent data operations along with **WebSocket/STOMP** communication for real-time room collaboration.

---

## Tech Stack

| Technology      | Purpose                         |
| --------------- | ------------------------------- |
| Java 17         | Backend programming language    |
| Spring Boot     | Backend framework               |
| Spring Web      | REST API development            |
| Spring Data JPA | Database access                 |
| Hibernate       | ORM implementation              |
| MySQL           | Relational database             |
| WebSocket       | Real-time communication         |
| STOMP           | WebSocket messaging protocol    |
| Gradle          | Build and dependency management |
| Lombok          | Reducing boilerplate code       |
| Gemini API      | AI-powered word lookup          |

---

## Backend Architecture

The backend follows a layered architecture:

```text
Client
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
JPA / Hibernate
  ↓
MySQL
```

### Layer Responsibilities

**Controller**

* Receives HTTP requests from the frontend.
* Maps requests to appropriate service methods.
* Returns responses to the client.
* Handles REST API endpoints.

**Service**

* Contains the application's business logic.
* Validates operations before modifying data.
* Coordinates between controllers and repositories.

**Repository**

* Communicates with the database through Spring Data JPA.
* Provides database queries without writing most SQL manually.

**Entity**

* Represents database tables as Java classes.
* Uses JPA annotations to define table and relationship mappings.

**DTO**

* Defines the data exchanged between the frontend and backend.
* Prevents database entities from being directly exposed through APIs.

---

# Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com/digitalspace/backend/
    │       │
    │       ├── config/
    │       │   └── WebSocketConfig.java
    │       │
    │       ├── controller/
    │       │   ├── RoomController.java
    │       │   └── WebSocketController.java
    │       │
    │       ├── dto/
    │       │   ├── RoomJoinRequestDTO.java
    │       │   ├── RoomMemberResponseDTO.java
    │       │   ├── RoomResponseDTO.java
    │       │   ├── StatusMessageDTO.java
    │       │   └── ChatMessageDTO.java
    │       │
    │       ├── entity/
    │       │   ├── Room.java
    │       │   ├── RoomMember.java
    │       │   ├── Message.java
    │       │   ├── Goal.java
    │       │   ├── Word.java
    │       │   ├── Quote.java
    │       │   └── StudySession.java
    │       │
    │       ├── repository/
    │       │   ├── RoomRepository.java
    │       │   ├── RoomMemberRepository.java
    │       │   ├── MessageRepository.java
    │       │   ├── GoalRepository.java
    │       │   └── ...
    │       │
    │       ├── service/
    │       │   ├── RoomService.java
    │       │   ├── MessageService.java
    │       │   └── ...
    │       │
    │       └── BackendApplication.java
    │
    └── resources/
        ├── application.properties
        └── application-example.properties
```

---

# Database Design

The application uses **MySQL** for persistent storage.

The main database relationships are:

```text
Room
 │
 ├── RoomMembers
 │
 ├── Goals
 │
 ├── Words
 │
 ├── Quotes
 │
 ├── Messages
 │
 └── StudySessions
```

A `Room` acts as the central entity for the collaborative study environment.

### Room Members

Each member belongs to a specific room and has:

* `memberId`
* `nickname`
* `displayName`
* `status`
* `joinedAt`

A nickname must be unique within a room.

### Messages

Each chat message stores:

* Message ID
* Room
* Member
* Message content
* Timestamp

Messages are persisted in MySQL so that previous chat messages can be loaded when a user enters or refreshes a room.

---

# REST APIs

The backend exposes REST APIs for room management, members, goals, words, quotes, and study sessions.

## Room APIs

| Method   | Endpoint                                   | Purpose               |
| -------- | ------------------------------------------ | --------------------- |
| `POST`   | `/api/rooms`                               | Create a new room     |
| `GET`    | `/api/rooms/{roomCode}`                    | Get room details      |
| `POST`   | `/api/rooms/join`                          | Join an existing room |
| `GET`    | `/api/rooms/{roomCode}/members`            | Get room members      |
| `DELETE` | `/api/rooms/{roomCode}/members/{memberId}` | Leave a room          |

---

## Chat APIs

| Method | Endpoint                         | Purpose          |
| ------ | -------------------------------- | ---------------- |
| `GET`  | `/api/rooms/{roomCode}/messages` | Get chat history |

Chat messages are sent through WebSocket rather than a normal REST `POST` request.

---

## Goal APIs

| Method   | Endpoint                               | Purpose        |
| -------- | -------------------------------------- | -------------- |
| `GET`    | `/api/rooms/{roomCode}/goals`          | Get room goals |
| `POST`   | `/api/rooms/{roomCode}/goals`          | Create a goal  |
| `PUT`    | `/api/rooms/{roomCode}/goals/{goalId}` | Update a goal  |
| `DELETE` | `/api/rooms/{roomCode}/goals/{goalId}` | Delete a goal  |

---

## Word APIs

Word-related APIs handle storing and retrieving words and their AI-generated meanings.

```text
GET     /api/rooms/{roomCode}/words
POST    /api/rooms/{roomCode}/words
DELETE  /api/rooms/{roomCode}/words/{wordId}
```

The AI lookup is handled by the backend so that API credentials are not exposed directly to the frontend.

---

## Quote APIs

```text
GET     /api/rooms/{roomCode}/quotes
POST    /api/rooms/{roomCode}/quotes
DELETE  /api/rooms/{roomCode}/quotes/{quoteId}
```

---

## Study Session APIs

```text
GET     /api/rooms/{roomCode}/study-sessions
POST    /api/rooms/{roomCode}/study-sessions
```

Study session data is persisted in the database.

---

# WebSocket Communication

Digital Space uses **WebSocket + STOMP** for real-time room collaboration.

The same WebSocket connection is used for both member status updates and chat.

### WebSocket Endpoint

```text
ws://localhost:8080/ws
```

### Application Prefix

```text
/app
```

### Topic Prefix

```text
/topic
```

---

## Member Status

### Send

```text
/app/status
```

The frontend sends the member's new status to the backend.

Example:

```json
{
  "roomCode": "95B60B",
  "memberId": 41,
  "status": "STUDYING"
}
```

### Receive

```text
/topic/room/{roomCode}
```

The backend saves the updated status and broadcasts the change to all users currently connected to that room.

---

# Real-Time Chat

### Send Message

```text
/app/chat
```

Example:

```json
{
  "roomCode": "95B60B",
  "memberId": 41,
  "content": "Let's study!"
}
```

### Receive Messages

```text
/topic/room/{roomCode}/chat
```

The backend:

```text
Receive message
      ↓
Validate room
      ↓
Validate member
      ↓
Check cooldown
      ↓
Save message to MySQL
      ↓
Broadcast saved message
      ↓
All connected room members receive it
```

---

## Chat Cooldown

To prevent message spam, the backend enforces a **20-second cooldown per member**.

The cooldown is enforced by the backend rather than relying on the frontend.

```text
Member sends message
        ↓
Find latest message
        ↓
Calculate elapsed time
        ↓
       < 20 sec?
       /       \
     YES        NO
      ↓          ↓
   Reject      Save
                 ↓
             Broadcast
```

---

# Room Capacity

Each study room supports a maximum of **6 members**.

The backend validates room capacity when a member attempts to join.

Nicknames are also unique within an individual room.

---

# Chat History

Chat messages are stored in MySQL.

When a user opens or refreshes a study room:

```text
React
  ↓
GET /api/rooms/{roomCode}/messages
  ↓
RoomController
  ↓
MessageService
  ↓
MessageRepository
  ↓
MySQL
  ↓
Chat history
  ↓
React
```

Messages are returned in chronological order.

---

# Configuration

Create:

```text
src/main/resources/application.properties
```

Example configuration:

```properties
spring.application.name=backend

spring.datasource.url=jdbc:mysql://localhost:3306/digital_space
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

gemini.api.key=${GEMINI_API_KEY}
```

Sensitive values such as database passwords and API keys should be provided through environment variables and should **not be committed to Git**.

An `application-example.properties` file can be used as a safe template.

---

# Running the Backend

## 1. Clone the repository

```bash
git clone <repository-url>
cd digital-space/backend
```

## 2. Configure MySQL

Create the database:

```sql
CREATE DATABASE digital_space;
```

## 3. Configure environment variables

Set:

```text
DB_PASSWORD=your_mysql_password
GEMINI_API_KEY=your_api_key
```

## 4. Run the application

Using Gradle:

```bash
./gradlew bootRun
```

On Windows:

```bash
gradlew.bat bootRun
```

The backend will run on:

```text
http://localhost:8080
```

WebSocket connections are available through:

```text
ws://localhost:8080/ws
```

---

# API Architecture

For normal REST requests:

```text
React
  ↓
HTTP Request
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
JPA / Hibernate
  ↓
MySQL
```

For real-time communication:

```text
React
  ↓
STOMP / WebSocket
  ↓
WebSocketController
  ↓
Service
  ↓
MySQL
  ↓
STOMP Broadcast
  ↓
All users in the room
```

---

# Current Backend Features

* Room creation and joining
* Room capacity management
* Unique room-level nicknames
* Member status management
* Real-time member status updates
* Real-time chat using WebSocket/STOMP
* Persistent chat messages
* Chat history
* Backend-enforced 20-second chat cooldown
* Goals management
* Word storage and AI-powered word lookup
* Quotes
* Study session tracking
* MySQL persistence
* REST API-based data operations
* Layered Spring Boot architecture
* DTO-based request/response handling

---

# Future Improvements

* Authentication and authorization
* JWT-based user identity
* Improved WebSocket error handling
* Real-time synchronization for goals, words, and quotes
* Automated cleanup of messages older than 7 days
* Production database configuration
* Dockerized deployment
* API documentation using Swagger/OpenAPI

---

## Backend Development Principles

The backend is designed around:

* Separation of concerns
* Layered architecture
* Business logic inside services
* Database access through repositories
* DTOs for API communication
* Persistent data storage
* Backend-side validation
* Real-time communication through WebSocket/STOMP
* Environment-based secret management
