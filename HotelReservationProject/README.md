# Hotel Reservation System

A Java console application for managing hotel rooms and reservations with full CRUD operations and a real SQLite database.

## Features

- Add, view, update, and delete rooms
- Create, view, update, and cancel reservations
- Prevent overlapping bookings for the same room
- Calculate booking amount automatically from room price and stay duration
- Persist data in a SQLite database file
- Show a dashboard with inventory and revenue summary
- Expose database path, tables, and record counts from the menu

## Project Structure

- `src/Room.java` stores room data
- `src/Reservation.java` stores reservation data
- `src/DatabaseManager.java` initializes the SQLite database
- `src/RoomDAO.java` handles room CRUD operations
- `src/ReservationDAO.java` handles reservation CRUD operations
- `src/HotelReservationSystem.java` contains the menu-driven interface
- the SQLite file is created automatically in the user's local app-data folder for reliable Windows writes
- `lib/sqlite-jdbc-3.46.1.3.jar` is the required SQLite JDBC driver

## Technologies Used

- Java
- JDBC
- SQLite
- `java.time`

## Compile And Run

```bash
javac -cp "lib/sqlite-jdbc-3.46.1.3.jar" -d out src\*.java
java -cp "out;lib/sqlite-jdbc-3.46.1.3.jar" HotelReservationSystem
```

At runtime, the exact database file location is shown in menu option `10. Show database exposure`.

## Sample Menu

```text
===== Hotel Reservation System =====
1. View rooms
2. Add room
3. Update room
4. Delete room
5. View reservations
6. Create reservation
7. Update reservation
8. Cancel reservation
9. View dashboard
10. Show database exposure
11. Exit
```

## Submission Notes

This project is a strong internship-style submission because it demonstrates:

- object-oriented design with model and DAO classes
- CRUD operations for two related entities
- relational database usage with JDBC
- validation for business rules like room availability and overlapping dates
- persistent storage reviewers can inspect directly
