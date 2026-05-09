# Backend Engineering Intern Assignment

**Author:** Rishav Pramanik  
**Role:** Backend & Systems Engineer Intern Candidate

## Overview

This project is a robust, high-performance Spring Boot microservice designed to act as a central API gateway and concurrency guardrail system. It demonstrates strict distributed state management, thread-safe atomic operations, and event-driven scheduling to prevent AI compute runaway and notification spam.

The application is completely stateless at the JVM level, utilizing PostgreSQL as the persistent source of truth and Redis as the high-speed gatekeeper for rate limiting and scoring.

---

## 🛠 Tech Stack

* **Language:** Java 17+ (Compiled with Java 21)
* **Framework:** Spring Boot 3.x
* **Database:** PostgreSQL (JPA / Hibernate)
* **Caching & Locks:** Redis (Spring Data Redis)
* **Build Tool:** Maven
* **Containerization:** Docker & Docker Compose

---

## 🏗 Architecture & Core Features

### Phase 1: Core API & Database

Implemented a strict relational schema mapping `User`, `Bot`, `Post`, and `Comment` entities.

* Exposed standard REST endpoints for post creation, commenting, and liking.
* Leveraged an automated `DataSeeder` to inject initial testing entities (Human ID: 1, Bot ID: 2) upon application startup to bypass the need for external user management endpoints, maintaining project purity.

### Phase 2: Redis Virality Engine & Atomic Locks (Concurrency Protection)

To ensure system stability against massive concurrent bot interactions, the application utilizes strict mathematical guardrails enforced entirely in Redis.

* **Virality Score:** Real-time scoring engine that instantly updates a post's virality based on interaction type (Bot Reply = +1, Human Like = +20, Human Comment = +50).
* **Vertical Cap:** Algorithmic validation ensuring a single comment thread never exceeds a depth level of 20.

#### 🛡 Guaranteed Thread Safety (The Spam Test)

To pass the 200-concurrent-request race condition test, the application bypasses standard Java memory locks and relies exclusively on Redis's single-threaded event loop for atomic operations:

1. **Horizontal Cap (100 Bot Limit):** Implemented using `redisTemplate.opsForValue().increment()`. Because the increment operation happens atomically within Redis, no race conditions can occur. Even if 200 requests hit the server at the exact same millisecond, Redis will sequence them perfectly, halting exactly at 100 and throwing a custom `429 Too Many Requests` exception.

2. **Cooldown Cap (10-Minute Throttling):** Implemented using the `setIfAbsent` (SET NX) command with a 10-minute TTL. This establishes a distributed lock `cooldown:bot_{id}:human_{id}`. The atomic nature of SET NX ensures that only the very first request succeeds, instantly blocking any subsequent spam from that specific bot-human pair.

### Phase 3: The Notification Engine (Smart Batching)

To optimize the user experience and prevent notification fatigue, the system batches bot interactions.

* **The Redis Throttler:** Checks for a 15-minute active lock via Redis. If a lock exists, incoming interactions are pushed into a Redis List (`user:{id}:pending_notifs`).

* **The CRON Sweeper:** A Spring `@Scheduled` background worker runs every 5 minutes, scanning Redis for pending notifications. It pops all messages atomically, aggregates them, logs a summarized push notification to the console, and clears the queue.

---

## 🚀 Setup & Installation

### 1. Start the Databases

Ensure Docker is installed and running on your system.

```bash
docker compose up -d
```

(This will start PostgreSQL on port 5432 and Redis on port 6379 in detached mode).

### 2. Run the Application

You do not need Maven installed globally; use the included wrapper.

```bash
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

(On startup, the application will automatically create the database schema and seed the initial User and Bot).

## 🧪 Testing

A Postman collection (`Postman_Collection.json`) is included in the root directory. Import this file into Postman to test the endpoints.

### Available Endpoints

* `POST /api/posts` - Creates a new post. (Body requires authorId and content).

* `POST /api/posts/{postId}/comments` - Adds a comment or reply. (Body requires authorId, content, and optional parentCommentId).

* `POST /api/posts/{postId}/like?authorId={id}` - Likes a post and increments the virality score.

**Note on Rate Limit Testing:** To verify the Redis cooldown lock, execute the "Add Comment (Bot)" request twice within 10 minutes. The first will return `201 Created`, and the second will be intercepted by the Redis guardrail, returning `429 Too Many Requests`.