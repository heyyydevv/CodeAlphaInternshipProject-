import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class StudentGradeTracker {
    private static final Path DATA_FILE = Path.of("data", "students.csv");
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final List<Student> students = new ArrayList<>();

    public static void main(String[] args) {
        loadStudentRecords();
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewStudents();
                    break;
                case 3:
                    showSummaryReport();
                    break;
                case 4:
                    saveStudentRecords();
                    System.out.println("Student records saved successfully.");
                    break;
                case 5:
                    running = false;
                    System.out.println("Exiting Student Grade Tracker. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please choose between 1 and 5.");
            }
        }

        SCANNER.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Student Grade Tracker =====");
        System.out.println("1. Add student grade");
        System.out.println("2. View all student records");
        System.out.println("3. Show summary report");
        System.out.println("4. Save records");
        System.out.println("5. Exit");
    }

    private static void addStudent() {
        System.out.println();
        System.out.print("Enter student name: ");
        String name = SCANNER.nextLine().trim();

        while (name.isEmpty()) {
            System.out.print("Name cannot be empty. Enter student name: ");
            name = SCANNER.nextLine().trim();
        }

        double marks = readDouble("Enter marks (0-100): ", 0, 100);
        students.add(new Student(name, marks));
        System.out.println("Student record added successfully.");
    }

    private static void viewStudents() {
        System.out.println();

        if (students.isEmpty()) {
            System.out.println("No student records available.");
            return;
        }

        System.out.printf("%-5s %-20s %-10s %-10s%n", "No.", "Name", "Marks", "Grade");
        System.out.println("------------------------------------------------");

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            System.out.printf(
                "%-5d %-20s %-10.2f %-10s%n",
                i + 1,
                student.getName(),
                student.getMarks(),
                student.getGrade()
            );
        }
    }

    private static void showSummaryReport() {
        System.out.println();

        if (students.isEmpty()) {
            System.out.println("No student records available to summarize.");
            return;
        }

        double total = 0;
        int passCount = 0;
        Student highest = students.stream()
            .max(Comparator.comparingDouble(Student::getMarks))
            .orElse(null);
        Student lowest = students.stream()
            .min(Comparator.comparingDouble(Student::getMarks))
            .orElse(null);

        for (Student student : students) {
            total += student.getMarks();
            if (student.hasPassed()) {
                passCount++;
            }
        }

        double average = total / students.size();
        int failCount = students.size() - passCount;

        System.out.println("===== Summary Report =====");
        System.out.println("Total students : " + students.size());
        System.out.printf("Average marks  : %.2f%n", average);
        System.out.println("Pass count     : " + passCount);
        System.out.println("Fail count     : " + failCount);
        if (highest != null) {
            System.out.printf("Highest score  : %s (%.2f)%n", highest.getName(), highest.getMarks());
        }
        if (lowest != null) {
            System.out.printf("Lowest score   : %s (%.2f)%n", lowest.getName(), lowest.getMarks());
        }
    }

    private static void loadStudentRecords() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(DATA_FILE);
            for (String line : lines) {
                Student student = parseStudent(line);
                if (student != null) {
                    students.add(student);
                }
            }
            System.out.println("Loaded " + students.size() + " student record(s) from file.");
        } catch (IOException exception) {
            System.out.println("Could not load saved records: " + exception.getMessage());
        }
    }

    private static void saveStudentRecords() {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            List<String> rows = new ArrayList<>();
            for (Student student : students) {
                rows.add(student.toCsvRow());
            }
            Files.write(DATA_FILE, rows);
        } catch (IOException exception) {
            System.out.println("Could not save records: " + exception.getMessage());
        }
    }

    private static Student parseStudent(String row) {
        if (row == null || row.isBlank()) {
            return null;
        }

        int separatorIndex = row.lastIndexOf(',');
        if (separatorIndex <= 0 || separatorIndex >= row.length() - 1) {
            return null;
        }

        String rawName = row.substring(0, separatorIndex).trim();
        String rawMarks = row.substring(separatorIndex + 1).trim();

        if (rawName.startsWith("\"") && rawName.endsWith("\"") && rawName.length() >= 2) {
            rawName = rawName.substring(1, rawName.length() - 1).replace("\"\"", "\"");
        }

        try {
            return new Student(rawName, Double.parseDouble(rawMarks));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                if (value < min || value > max) {
                    System.out.printf("Please enter a value between %.0f and %.0f.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
