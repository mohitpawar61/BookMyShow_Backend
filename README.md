# 🎬 BookMyShow Backend (BMS)

A full-featured **movie ticket booking REST API** inspired by BookMyShow, built with **Spring Boot 4.0**, **Java 21**, **MySQL**, and **Spring Data JPA**. Covers the complete booking lifecycle — from city and theater setup through movie scheduling, seat management, and ticket booking with real-time seat availability checks.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Database Schema & Entity Relationships](#-database-schema--entity-relationships)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [API Endpoints — Complete Reference](#-api-endpoints--complete-reference)
- [Request & Response Examples](#-request--response-examples)
- [Booking Flow — End to End](#-booking-flow--end-to-end)
- [Business Logic Highlights](#-business-logic-highlights)
- [Enums](#-enums)
- [Error Handling](#-error-handling)
- [CORS Configuration](#-cors-configuration)
- [Author](#-author)

---

## 📖 Overview

The **BMS (BookMyShow) Backend** is a production-style REST API that manages an end-to-end movie ticket booking system. The domain is modelled in six interconnected layers:

```
City → Theater → Screen → Seat
Movie → Show (ties a Movie to a Screen on a date/time)
User → Booking (ties a User + Show + List<Seat>)
```

Users register, browse movies, find shows by date, check available seats, and create bookings — all via RESTful endpoints with full MySQL persistence.

---

## ✨ Features

- 🏙️ City and theater management (multi-city support)
- 🎭 Screen management per theater with seat capacity
- 🪑 Seat management with type (REGULAR / PREMIUM / VIP) and row/column layout
- 🎬 Movie catalog with search by title, genre, and language
- 📅 Show scheduling — link a movie to a screen with date, start/end time, and ticket price
- 🎟️ Booking with multi-seat selection and automatic total price calculation
- 🔒 Seat conflict detection — prevents double-booking at the DB query level
- ✅ Real-time available seat listing for any show
- ❌ Booking cancellation with status update
- 👤 User registration and login with email uniqueness enforcement
- 🌐 Global CORS configuration for all origins and methods
- 🛡️ Centralized global exception handling with structured error responses
- 🔄 `@Transactional` booking creation — atomic seat reservation

---

## 🛠 Tech Stack

| Layer            | Technology                              |
|------------------|-----------------------------------------|
| Language         | Java 21                                 |
| Framework        | Spring Boot 4.0.3                       |
| Web              | Spring MVC (`spring-boot-starter-webmvc`) |
| Persistence      | Spring Data JPA + Hibernate             |
| Database         | MySQL 8.0                               |
| Utilities        | Lombok 1.18.44                          |
| Build Tool       | Maven                                   |

---

## 🏗 Architecture

```
Client (Frontend / Postman)
          │
          │  HTTP REST calls
          ▼
┌─────────────────────────────────────────────────┐
│                  Controllers                     │
│  UserController        /api/users/**            │
│  CityController        /api/cities/**           │
│  MovieController       /api/movies/**           │
│  TheaterController     /api/theaters/**         │
│  ScreenController      /api/screens/**          │
│  SeatController        /api/seats/**            │
│  ShowController        /api/shows/**            │
│  BookingController     /api/bookings/**         │
└───────────────────────┬─────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────┐
│                   Services                       │
│  UserService    MovieService    CityService      │
│  TheaterService ScreenService   SeatService      │
│  ShowService    BookingService                   │
│                                                  │
│  BookingService:                                 │
│   ├─ Validates seats not already booked          │
│   ├─ Calculates totalPrice = seats × ticketPrice │
│   └─ @Transactional — atomic save               │
└───────────────────────┬─────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────┐
│                  Repositories                    │
│  (Spring Data JPA — JpaRepository<Entity, Long>) │
│                                                  │
│  Custom queries:                                 │
│  BookingRepository:                              │
│   findBookedSeatIdByShowId() — JPQL @Query       │
│   → SELECT seat ids for CONFIRMED bookings       │
│  MovieRepository:                                │
│   findByTitleContainingIgnoreCase()              │
│   findByGenre() / findByLanguage()               │
│  ShowRepository:                                 │
│   findByMovieIdAndShowDate()                     │
└───────────────────────┬─────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────┐
│               MySQL Database                     │
│  bms_project  (auto-created)                     │
│  8 tables — see schema below                     │
└─────────────────────────────────────────────────┘
```

---

## 🗄 Database Schema & Entity Relationships

### Entity Relationship Diagram

```
cities (1) ──────────< theaters (many)
                           │
                      (1) ─┤
                           ├──────────< screens (many)
                                            │
                                       (1) ─┤
                                            ├──────────< seats (many)
                                            │
movies (1) ──────────────────────────── shows (many) ──< (screen FK)
                                            │
users (1) ───────────────────────────── bookings (many)
                                            │
                                       booking_seats
                                    (booking_id, seat_id)
```

### `cities`

| Column | Type          | Notes             |
|--------|---------------|-------------------|
| id     | BIGINT (PK)   | Auto-increment    |
| name   | VARCHAR       | Unique, not null  |
| state  | VARCHAR       |                   |

### `theaters`

| Column  | Type        | Notes                    |
|---------|-------------|--------------------------|
| id      | BIGINT (PK) | Auto-increment           |
| name    | VARCHAR     | Unique, not null         |
| address | VARCHAR     |                          |
| city_id | BIGINT (FK) | → `cities.id`, not null  |

### `screens`

| Column      | Type        | Notes                      |
|-------------|-------------|----------------------------|
| id          | BIGINT (PK) | Auto-increment             |
| name        | VARCHAR     | Not null                   |
| total_seats | INTEGER     |                            |
| theater_id  | BIGINT (FK) | → `theaters.id`, not null  |

### `seats`

| Column      | Type        | Notes                                    |
|-------------|-------------|------------------------------------------|
| id          | BIGINT (PK) | Auto-increment                           |
| seat_number | VARCHAR     | Not null (e.g. "A1", "B5")              |
| seat_row    | VARCHAR     | Row label (e.g. "A", "B")               |
| seat_col    | INTEGER     | Column number                            |
| seat_type   | ENUM        | `REGULAR` / `PREMIUM` / `VIP`           |
| screen_id   | BIGINT (FK) | → `screens.id`, not null                |

### `movies`

| Column           | Type        | Notes              |
|------------------|-------------|--------------------|
| id               | BIGINT (PK) | Auto-increment     |
| title            | VARCHAR     | Not null           |
| description      | TEXT        |                    |
| genre            | VARCHAR     |                    |
| language         | VARCHAR     |                    |
| duration_minutes | INTEGER     |                    |
| rating           | DOUBLE      |                    |
| release_date     | DATE        |                    |
| poster_url       | VARCHAR     |                    |

### `shows`

| Column       | Type        | Notes                    |
|--------------|-------------|--------------------------|
| id           | BIGINT (PK) | Auto-increment           |
| movie_id     | BIGINT (FK) | → `movies.id`, not null  |
| screen_id    | BIGINT (FK) | → `screens.id`, not null |
| show_date    | DATE        |                          |
| start_time   | TIME        |                          |
| end_time     | TIME        |                          |
| ticket_price | DOUBLE      | Price per seat (₹)       |

### `users`

| Column     | Type        | Notes              |
|------------|-------------|--------------------|
| id         | BIGINT (PK) | Auto-increment     |
| name       | VARCHAR     | Not null           |
| email      | VARCHAR     | Unique, not null   |
| password   | VARCHAR     | Not null           |
| phone      | VARCHAR     |                    |
| created_at | DATETIME    | Auto-set on insert |

### `bookings`

| Column      | Type        | Notes                          |
|-------------|-------------|--------------------------------|
| id          | BIGINT (PK) | Auto-increment                 |
| user_id     | BIGINT (FK) | → `users.id`, not null         |
| show_id     | BIGINT (FK) | → `shows.id`, not null         |
| total_price | DOUBLE      | seats.size × show.ticketPrice  |
| status      | ENUM        | `CONFIRMED` / `CANCELLED`      |
| booked_at   | DATETIME    | Auto-set on insert via @PrePersist |

### `booking_seats` (Join Table — Many-to-Many)

| Column     | Type   | Notes              |
|------------|--------|--------------------|
| booking_id | BIGINT | → `bookings.id`    |
| seat_id    | BIGINT | → `seats.id`       |

---

## 📁 Project Structure

```
BookMyShow_Backend/
├── pom.xml
└── src/
    └── main/
        ├── java/com/cfs/BMS/
        │   ├── BmsApplication.java                    # @SpringBootApplication entry point
        │   ├── config/
        │   │   └── CorsConfig.java                    # Global CORS — allows all origins/methods
        │   ├── controller/
        │   │   ├── UserController.java                # /api/users — register, login, get
        │   │   ├── CityController.java                # /api/cities — get all, get by id
        │   │   ├── MovieController.java               # /api/movies — CRUD + search
        │   │   ├── TheaterController.java             # /api/theaters — add, get, get by city
        │   │   ├── ScreenController.java              # /api/screens — add, get, get by theater
        │   │   ├── SeatController.java                # /api/seats — add, get by screen
        │   │   ├── ShowController.java                # /api/shows — add, get, get by movie/date
        │   │   └── BookingController.java             # /api/bookings — book, cancel, available seats
        │   ├── dto/
        │   │   ├── UserRequest.java                   # name, email, password, phone
        │   │   ├── LoginRequest.java                  # email, password
        │   │   ├── MovieRequest.java                  # title, genre, language, rating, etc.
        │   │   ├── TheaterRequest.java                # name, address, cityId
        │   │   ├── ScreenRequest.java                 # name, totalSeats, theaterId
        │   │   ├── SeatRequest.java                   # seatNumber, row, col, seatType, screenId
        │   │   ├── ShowRequest.java                   # movieId, screenId, date, times, price
        │   │   └── BookingReq.java                    # userId, showId, List<Long> seatId
        │   ├── entity/
        │   │   ├── City.java
        │   │   ├── Theater.java                       # ManyToOne → City
        │   │   ├── Screen.java                        # ManyToOne → Theater
        │   │   ├── Seat.java                          # ManyToOne → Screen, Enum SeatType
        │   │   ├── Movie.java
        │   │   ├── Show.java                          # ManyToOne → Movie, Screen
        │   │   ├── User.java                          # @PrePersist sets createdAt
        │   │   └── Booking.java                       # ManyToOne → User, Show
        │   │                                          # ManyToMany → Seats (booking_seats)
        │   │                                          # @PrePersist sets bookedAt + CONFIRMED
        │   ├── enums/
        │   │   ├── SeatType.java                      # REGULAR, PREMIUM, VIP
        │   │   └── BookingStatus.java                 # CONFIRMED, CANCELLED
        │   ├── exception/
        │   │   └── GlobalExceptionHandler.java        # @RestControllerAdvice — structured errors
        │   ├── repository/
        │   │   ├── CityRepository.java
        │   │   ├── UserRepository.java                # findByEmail, existsByEmail
        │   │   ├── MovieRepository.java               # findByGenre, findByLanguage, findByTitle...
        │   │   ├── TheaterRepository.java             # findByCity_id
        │   │   ├── ScreenRepository.java              # findByTheaterId
        │   │   ├── SeatRepository.java                # findByScreenId
        │   │   ├── ShowRepository.java                # findByMovieId, findByMovieIdAndShowDate
        │   │   └── BookingRepository.java             # findByUserId, JPQL findBookedSeatIdByShowId
        │   └── service/
        │       ├── CityService.java
        │       ├── UserService.java                   # email uniqueness check, password match
        │       ├── MovieService.java                  # title/genre/language search
        │       ├── TheaterService.java                # resolve City before save
        │       ├── ScreenService.java                 # resolve Theater before save
        │       ├── SeatService.java                   # resolve Screen before save
        │       ├── ShowService.java                   # resolve Movie + Screen before save
        │       └── BookingService.java                # @Transactional, seat conflict check,
        │                                              # available seat diff logic
        └── resources/
            └── application.properties
```

---

## 📋 Prerequisites

- **Java 21+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/)
- **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/mohitpawar61/BookMyShow_Backend.git
cd BookMyShow_Backend
```

### 2. Configure the Database

The database `bms_project` is **auto-created** on first startup (via `createDatabaseIfNotExist=true`).

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bms_project?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

Or run the packaged JAR:
```bash
mvn clean package
java -jar target/BMS-0.0.1-SNAPSHOT.jar
```

Application starts at: **`http://localhost:8080`**

### 4. Recommended Setup Order

To populate the system correctly, follow this sequence:

```
1. Register users          POST /api/users/register
2. Add cities              (insert directly in DB or add a city endpoint)
3. Add theaters            POST /api/theaters/addTheater  (needs cityId)
4. Add screens             POST /api/screens/addScreen    (needs theaterId)
5. Add seats               POST /api/seats/addSeats       (needs screenId)
6. Add movies              POST /api/movies               (independent)
7. Add shows               POST /api/shows/addShows       (needs movieId + screenId)
8. Create bookings         POST /api/bookings             (needs userId + showId + seatIds)
```

---

## 🔧 Configuration

### `application.properties` — Full Reference

```properties
spring.application.name=BMS

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/bms_project?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password

# JPA / Hibernate — auto-creates and updates tables
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server
server.port=8080
```

---

## 📡 API Endpoints — Complete Reference

### 👤 Users — `/api/users`

| Method | Endpoint             | Description                       |
|--------|----------------------|-----------------------------------|
| POST   | `/api/users/register`| Register a new user               |
| POST   | `/api/users/login`   | Login with email & password       |
| GET    | `/api/users`         | Get all users                     |
| GET    | `/api/users/{id}`    | Get a user by ID                  |

### 🏙️ Cities — `/api/cities`

| Method | Endpoint            | Description         |
|--------|---------------------|---------------------|
| GET    | `/api/cities`       | Get all cities      |
| GET    | `/api/cities/{id}`  | Get a city by ID    |

### 🎬 Movies — `/api/movies`

| Method | Endpoint                    | Description                      |
|--------|-----------------------------|----------------------------------|
| POST   | `/api/movies`               | Add a new movie                  |
| GET    | `/api/movies`               | Get all movies                   |
| GET    | `/api/movies/{id}`          | Get a movie by ID                |
| GET    | `/api/movies/search?title=` | Search movies by title (partial) |
| GET    | `/api/movies/genre/{genre}` | Get movies by genre              |
| GET    | `/api/movies/genre/{language}` | Get movies by language        |

### 🏟️ Theaters — `/api/theaters`

| Method | Endpoint                      | Description                     |
|--------|-------------------------------|---------------------------------|
| POST   | `/api/theaters/addTheater`    | Add a new theater               |
| GET    | `/api/theaters`               | Get all theaters                |
| GET    | `/api/theaters/{id}`          | Get a theater by ID             |
| GET    | `/api/theaters/city/{id}`     | Get all theaters in a city      |

### 📽️ Screens — `/api/screens`

| Method | Endpoint                           | Description                      |
|--------|------------------------------------|----------------------------------|
| POST   | `/api/screens/addScreen`           | Add a screen to a theater        |
| GET    | `/api/screens`                     | Get all screens                  |
| GET    | `/api/screens/{id}`                | Get a screen by ID               |
| GET    | `/api/screens/theater/{theaterId}` | Get all screens in a theater     |

### 🪑 Seats — `/api/seats`

| Method | Endpoint                          | Description                    |
|--------|-----------------------------------|--------------------------------|
| POST   | `/api/seats/addSeats`             | Add a seat to a screen         |
| GET    | `/api/seats/{id}`                 | Get a seat by ID               |
| GET    | `/api/seats/screen/{screenId}`    | Get all seats in a screen      |

### 🎟️ Shows — `/api/shows`

| Method | Endpoint                                     | Description                          |
|--------|----------------------------------------------|--------------------------------------|
| POST   | `/api/shows/addShows`                        | Schedule a new show                  |
| GET    | `/api/shows`                                 | Get all shows                        |
| GET    | `/api/shows/{id}`                            | Get a show by ID                     |
| GET    | `/api/shows/movie/{id}`                      | Get all shows for a movie            |
| GET    | `/api/shows/movie/{movieId}/date?date=`      | Get shows for a movie on a date      |

### 📋 Bookings — `/api/bookings`

| Method | Endpoint                                  | Description                             |
|--------|-------------------------------------------|-----------------------------------------|
| POST   | `/api/bookings`                           | Create a booking (multi-seat)           |
| GET    | `/api/bookings/{id}`                      | Get a booking by ID                     |
| GET    | `/api/bookings/user/{userId}`             | Get all bookings for a user             |
| PUT    | `/api/bookings/{id}/cancel`               | Cancel a booking                        |
| GET    | `/api/bookings/show/{showId}/available-seats` | Get available seats for a show      |

---

## 📝 Request & Response Examples

### Register a User

```http
POST /api/users/register
Content-Type: application/json

{
  "name": "Mohit Pawar",
  "email": "mohit@example.com",
  "password": "secret123",
  "phone": "9876543210"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Mohit Pawar",
  "email": "mohit@example.com",
  "phone": "9876543210",
  "createdAt": "2026-06-27T10:00:00"
}
```

---

### Login

```http
POST /api/users/login
Content-Type: application/json

{
  "email": "mohit@example.com",
  "password": "secret123"
}
```

---

### Add a Movie

```http
POST /api/movies
Content-Type: application/json

{
  "title": "Pushpa 2",
  "description": "The rise continues",
  "genre": "Action",
  "language": "Telugu",
  "durationMinutes": 175,
  "rating": 8.5,
  "releaseDate": "2024-12-05",
  "posterUrl": "https://example.com/pushpa2.jpg"
}
```

---

### Add a Theater

```http
POST /api/theaters/addTheater
Content-Type: application/json

{
  "name": "PVR Cinemas",
  "address": "Phoenix Mall, Nagar Road",
  "cityId": 1
}
```

---

### Add a Screen

```http
POST /api/screens/addScreen
Content-Type: application/json

{
  "name": "Screen 1",
  "totalSeats": 150,
  "theaterId": 1
}
```

---

### Add a Seat

```http
POST /api/seats/addSeats
Content-Type: application/json

{
  "seatNumber": "A1",
  "row": "A",
  "col": 1,
  "seatType": "PREMIUM",
  "screenId": 1
}
```

---

### Schedule a Show

```http
POST /api/shows/addShows
Content-Type: application/json

{
  "movieId": 1,
  "screenId": 1,
  "showDate": "2026-07-01",
  "startTime": "18:00:00",
  "endTime": "20:55:00",
  "ticketPrice": 299.00
}
```

---

### Check Available Seats for a Show

```http
GET /api/bookings/show/1/available-seats
```

**Response:**
```json
[
  { "id": 1, "seatNumber": "A1", "row": "A", "col": 1, "seat_type": "PREMIUM" },
  { "id": 2, "seatNumber": "A2", "row": "A", "col": 2, "seat_type": "REGULAR" }
]
```

---

### Create a Booking

```http
POST /api/bookings
Content-Type: application/json

{
  "userId": 1,
  "showId": 1,
  "seatId": [1, 2, 3]
}
```

**Response:**
```json
{
  "id": 1,
  "user": { "id": 1, "name": "Mohit Pawar" },
  "show": { "id": 1, "showDate": "2026-07-01", "ticketPrice": 299.0 },
  "seats": [
    { "id": 1, "seatNumber": "A1" },
    { "id": 2, "seatNumber": "A2" },
    { "id": 3, "seatNumber": "A3" }
  ],
  "totalPrice": 897.0,
  "status": "CONFIRMED",
  "bookedAt": "2026-06-27T10:30:00"
}
```

---

### Cancel a Booking

```http
PUT /api/bookings/1/cancel
```

**Response:**
```json
{
  "id": 1,
  "status": "CANCELLED",
  ...
}
```

---

### Search Movies by Title

```http
GET /api/movies/search?title=pushpa
```

---

### Get Shows for a Movie on a Specific Date

```http
GET /api/shows/movie/1/date?date=2026-07-01
```

---

## 🎟️ Booking Flow — End to End

```
1. User registers
   POST /api/users/register

2. User browses movies
   GET /api/movies
   GET /api/movies/search?title=...

3. User selects a movie → finds shows
   GET /api/shows/movie/{movieId}
   GET /api/shows/movie/{movieId}/date?date=2026-07-01

4. User selects a show → checks available seats
   GET /api/bookings/show/{showId}/available-seats
   → Returns all screen seats MINUS already-booked seat IDs
     (only CONFIRMED bookings are excluded)

5. User selects seats and books
   POST /api/bookings
   { userId, showId, seatId: [1, 2, 3] }

6. BookingService (atomic @Transactional):
   a. Validates user exists
   b. Validates show exists
   c. Validates seatId list is not empty
   d. JPQL query: fetches already-booked seat IDs for this show
   e. Checks each requested seat against booked list → throws if conflict
   f. Fetches all Seat entities by IDs
   g. Validates count matches (guards against invalid seat IDs)
   h. totalPrice = seats.size() × show.ticketPrice
   i. Saves Booking (status=CONFIRMED, bookedAt auto-set by @PrePersist)

7. User views their bookings
   GET /api/bookings/user/{userId}

8. User cancels if needed
   PUT /api/bookings/{id}/cancel
   → Sets status to CANCELLED (does NOT delete — keeps history)
   → Cancelled seats become available again for new bookings
```

---

## 🧠 Business Logic Highlights

### Seat Conflict Detection

The core of the booking system — prevents two users from booking the same seat for the same show:

```java
// JPQL in BookingRepository:
@Query("SELECT s.id FROM Booking b JOIN b.seats s WHERE b.show.id = :showId AND b.status = 'CONFIRMED'")
List<Long> findBookedSeatIdByShowId(@Param("showId") Long showId);

// In BookingService.createBooking():
List<Long> alreadyBookedSeats = bookingRepository.findBookedSeatIdByShowId(show.getId());
for (Long seatId : request.getSeatId()) {
    if (alreadyBookedSeats.contains(seatId)) {
        throw new RuntimeException("Seat with id " + seatId + " is already Booked");
    }
}
```

Only **CONFIRMED** bookings block seats — cancelled bookings free up those seats automatically.

### Available Seat Calculation

```java
// Gets all seats for the show's screen, then filters out booked ones:
List<Seat> allSeats = seatRepository.findByScreenId(show.getScreen().getId());
List<Long> bookedSeatIds = bookingRepository.findBookedSeatIdByShowId(showId);
return allSeats.stream()
    .filter(seat -> !bookedSeatIds.contains(seat.getId()))
    .toList();
```

### Total Price Calculation

```java
double totalPrice = seats.size() * show.getTicketPrice();
```

All seats in a show share the same ticket price. Seat type (REGULAR / PREMIUM / VIP) is stored for display but does not affect pricing in the current implementation.

### Auto-timestamps via `@PrePersist`

Both `User` and `Booking` use `@PrePersist` lifecycle hooks:
- `User.createdAt` — set automatically on registration
- `Booking.bookedAt` — set automatically on booking creation
- `Booking.status` — defaults to `CONFIRMED` if not explicitly set

---

## 🏷️ Enums

### `SeatType`

| Value     | Description                       |
|-----------|-----------------------------------|
| `REGULAR` | Standard seats                    |
| `PREMIUM` | Better view / recliner seats      |
| `VIP`     | Best seats in the house           |

### `BookingStatus`

| Value       | Description                                        |
|-------------|----------------------------------------------------|
| `CONFIRMED` | Active booking — seats are reserved                |
| `CANCELLED` | Booking cancelled — seats available for rebooking  |

---

## 🛡️ Error Handling

All errors are handled by `GlobalExceptionHandler` (`@RestControllerAdvice`) and return a structured JSON response:

### `RuntimeException` (e.g. not found, seat taken, invalid input)

```json
{
  "timestamp": "2026-06-27T10:30:00",
  "message": "Seat with id 5 is already Booked",
  "status": 400
}
```

### `Exception` (unexpected server errors)

```json
{
  "timestamp": "2026-06-27T10:30:00",
  "Something went wrong": "...",
  "status": 500
}
```

### Common Error Scenarios

| Scenario                          | Error Message                                   |
|-----------------------------------|-------------------------------------------------|
| Email already registered          | `Email already exists: john@example.com`        |
| Wrong password at login           | `Invalid password`                              |
| User not found                    | `User not found with email: <id>`               |
| Movie not found                   | `Movie not found with id: <id>`                 |
| Theater not found                 | `Theater not found with id: <id>`               |
| Screen not found                  | `Screen not found with id: <id>`                |
| Seat not found                    | `Seat not found with id: <id>`                  |
| Show not found                    | `Show not found with id: <id>`                  |
| Booking not found                 | `Booking not found with id: <id>`               |
| No seats selected                 | `At least one seat must be selected`            |
| Seat already booked               | `Seat with id <id> is already Booked`           |
| Invalid seat IDs in request       | `Some Seats Are Invalid`                        |

---

## 🌐 CORS Configuration

Configured in `CorsConfig.java` — allows all origins and all standard HTTP methods:

```java
registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*");
```

For production, restrict `allowedOrigins` to your frontend domain:
```java
.allowedOrigins("https://your-frontend.com")
```

---

## 👨‍💻 Author

**Mohit Pawar**
- GitHub: [@mohitpawar61](https://github.com/mohitpawar61)

---

## 📄 License

This project is for educational and development purposes, inspired by the BookMyShow platform.
ENDOFFILE
