# Student Grade Tracker

A simple Java console application to manage student grades. It allows users to add student records, view all records, save data, and generate a summary report with average, highest, and lowest marks.

## Features

- Add student names and marks
- Store records using `ArrayList`
- Display all student records with letter grades
- Calculate average marks
- Show highest and lowest scores
- Show pass and fail counts
- Save student records to a CSV file
- Load saved records automatically when the program starts

## Project Structure

- `src/Student.java` stores each student's name, marks, grade, and CSV conversion logic
- `src/StudentGradeTracker.java` contains the menu, validation, summary logic, and file handling
- `data/students.csv` is created automatically when records are saved

## Technologies Used

- Java
- `ArrayList`
- `Scanner`
- `java.nio.file.Files`

## How It Works

1. The program starts and loads any previously saved student records.
2. Users can add student names and marks through a menu-driven console.
3. Records are stored in an `ArrayList<Student>`.
4. The summary report calculates:
   - average marks
   - highest score
   - lowest score
   - pass count
   - fail count
5. Records can be saved to a CSV file for later use.

## Run

```bash
javac -d out src\\Student.java src\\StudentGradeTracker.java
java -cp out StudentGradeTracker
```

## Sample Menu

```text
===== Student Grade Tracker =====
1. Add student grade
2. View all student records
3. Show summary report
4. Save records
5. Exit
```

## Submission Notes

This project is suitable for a basic Java internship task submission because it demonstrates:

- object-oriented programming with a `Student` class
- collection handling using `ArrayList`
- conditional logic for grade calculation
- file handling for persistent storage
- user interaction through a console-based menu

## Author

- Name: Raj
- Project: CodeAlpha Java Programming Internship Task 1
