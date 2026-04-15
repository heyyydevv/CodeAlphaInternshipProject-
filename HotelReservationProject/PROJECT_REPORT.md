# Hotel Reservation System Project Report

## Project Title

Hotel Reservation System

## Objective

The objective of this project is to build a Java application that manages hotel room inventory and customer reservations using CRUD operations connected to a real database.

## Description

This is a console-based Java application designed for internship task submission. It allows hotel staff to manage room details and reservations from a menu-driven interface. The application stores data in a SQLite database using JDBC, which makes the project stronger than a purely in-memory solution.

Rooms can be added, updated, viewed, and deleted. Reservations can be created, modified, viewed, and cancelled. The system also validates stay dates, prevents overlapping bookings for the same room, calculates total booking charges, and displays a dashboard with room and reservation statistics.

An additional database exposure screen shows the database file path, table names, and record counts so the reviewer can clearly see that the system is truly database-backed. On Windows, the database is stored in the local app-data area for more reliable SQLite file access than cloud-synced folders.

## Key Features

- Room CRUD operations
- Reservation CRUD operations
- SQLite database integration using JDBC
- Validation for date ranges and duplicate room numbers
- Prevention of overlapping reservations
- Revenue and occupancy summary dashboard
- Database exposure screen for submission/demo clarity

## Concepts Used

- Classes and objects
- Encapsulation
- JDBC
- SQLite database design
- Exception handling
- Input validation
- Loops and conditional logic
- `java.time` date handling

## Expected Outcome

The application helps a hotel maintain room inventory and customer bookings in a structured way. It demonstrates practical Java development skills by combining OOP, relational data handling, validation, and a user-friendly console interface.

## Conclusion

The Hotel Reservation System is a practical and submission-ready Java mini-project. It goes beyond a basic menu program by including real database persistence and meaningful business rules, which makes it a stronger signal for internship evaluation.
