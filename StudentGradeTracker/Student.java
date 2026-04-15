public class Student {
    private final String name;
    private final double marks;

    public Student(String name, double marks) {
        this.name = name;
        this.marks = marks;
    }

    public String getName() {
        return name;
    }

    public double getMarks() {
        return marks;
    }

    public String getGrade() {
        if (marks >= 90) {
            return "A+";
        }
        if (marks >= 80) {
            return "A";
        }
        if (marks >= 70) {
            return "B";
        }
        if (marks >= 60) {
            return "C";
        }
        if (marks >= 50) {
            return "D";
        }
        return "F";
    }

    public boolean hasPassed() {
        return marks >= 40;
    }

    public String toCsvRow() {
        String escapedName = name.replace("\"", "\"\"");
        return "\"" + escapedName + "\"," + marks;
    }
}
