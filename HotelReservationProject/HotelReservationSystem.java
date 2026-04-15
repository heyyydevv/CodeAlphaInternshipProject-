import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final RoomDAO ROOM_DAO = new RoomDAO();
    private static final ReservationDAO RESERVATION_DAO = new ReservationDAO();

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> viewRooms();
                case 2 -> addRoom();
                case 3 -> updateRoom();
                case 4 -> deleteRoom();
                case 5 -> viewReservations();
                case 6 -> createReservation();
                case 7 -> updateReservation();
                case 8 -> cancelReservation();
                case 9 -> showDashboard();
                case 10 -> showDatabaseExposure();
                case 11 -> {
                    running = false;
                    System.out.println("Exiting Hotel Reservation System. Goodbye!");
                }
                default -> System.out.println("Invalid option. Please choose between 1 and 11.");
            }
        }

        SCANNER.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Hotel Reservation System =====");
        System.out.println("1. View rooms");
        System.out.println("2. Add room");
        System.out.println("3. Update room");
        System.out.println("4. Delete room");
        System.out.println("5. View reservations");
        System.out.println("6. Create reservation");
        System.out.println("7. Update reservation");
        System.out.println("8. Cancel reservation");
        System.out.println("9. View dashboard");
        System.out.println("10. Show database exposure");
        System.out.println("11. Exit");
    }

    private static void viewRooms() {
        System.out.println();
        List<Room> rooms = ROOM_DAO.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
            return;
        }

        System.out.printf("%-4s %-10s %-12s %-15s %-12s%n", "ID", "Room No.", "Category", "Price/Night", "Status");
        System.out.println("--------------------------------------------------------------");
        for (Room room : rooms) {
            System.out.printf("%-4d %-10s %-12s Rs. %-11.2f %-12s%n",
                room.getId(),
                room.getRoomNumber(),
                room.getCategory(),
                room.getPricePerNight(),
                room.getStatus());
        }
    }

    private static void addRoom() {
        System.out.println();
        System.out.print("Enter room number: ");
        String roomNumber = readRequiredText();
        System.out.print("Enter category (Standard/Deluxe/Suite): ");
        String category = readRequiredText();
        double price = readDouble("Enter price per night: ", 1);
        String status = readRoomStatus();

        boolean created = ROOM_DAO.createRoom(roomNumber, category, price, status);
        if (created) {
            System.out.println("Room created successfully.");
        } else {
            System.out.println("Could not create room. Room number may already exist.");
        }
    }

    private static void updateRoom() {
        System.out.println();
        int roomId = readInt("Enter room ID to update: ");
        Room room = ROOM_DAO.getRoomById(roomId);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }

        System.out.print("Enter new category: ");
        String category = readRequiredText();
        double price = readDouble("Enter new price per night: ", 1);
        String status = readRoomStatus();

        if (ROOM_DAO.updateRoom(roomId, category, price, status)) {
            System.out.println("Room updated successfully.");
        } else {
            System.out.println("Room update failed.");
        }
    }

    private static void deleteRoom() {
        System.out.println();
        int roomId = readInt("Enter room ID to delete: ");
        Room room = ROOM_DAO.getRoomById(roomId);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }

        if (RESERVATION_DAO.countReservationsForRoom(roomId) > 0) {
            System.out.println("Cannot delete room while reservations exist for it.");
            return;
        }

        if (ROOM_DAO.deleteRoom(roomId)) {
            System.out.println("Room deleted successfully.");
        } else {
            System.out.println("Room deletion failed.");
        }
    }

    private static void viewReservations() {
        System.out.println();
        List<Reservation> reservations = RESERVATION_DAO.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        System.out.printf("%-4s %-8s %-18s %-12s %-12s %-12s%n",
            "ID", "Room", "Guest", "Check-in", "Check-out", "Amount");
        System.out.println("--------------------------------------------------------------------");
        for (Reservation reservation : reservations) {
            Room room = ROOM_DAO.getRoomById(reservation.getRoomId());
            String roomNumber = room != null ? room.getRoomNumber() : "N/A";
            System.out.printf("%-4d %-8s %-18s %-12s %-12s Rs. %-8.2f%n",
                reservation.getId(),
                roomNumber,
                reservation.getGuestName(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getTotalAmount());
        }
    }

    private static void createReservation() {
        System.out.println();
        int roomId = readInt("Enter room ID: ");
        Room room = ROOM_DAO.getRoomById(roomId);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        if (!"AVAILABLE".equalsIgnoreCase(room.getStatus())) {
            System.out.println("This room is not available for booking.");
            return;
        }

        System.out.print("Enter guest name: ");
        String guestName = readRequiredText();
        LocalDate checkIn = readDate("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkOut = readDate("Enter check-out date (YYYY-MM-DD): ");

        if (!isValidStay(checkIn, checkOut)) {
            System.out.println("Check-out date must be after check-in date.");
            return;
        }
        if (RESERVATION_DAO.hasOverlappingReservation(roomId, checkIn, checkOut, null)) {
            System.out.println("This room is already booked for the selected dates.");
            return;
        }

        double totalAmount = calculateTotalAmount(room.getPricePerNight(), checkIn, checkOut);
        if (RESERVATION_DAO.createReservation(roomId, guestName, checkIn, checkOut, totalAmount)) {
            System.out.printf("Reservation created successfully. Total amount: Rs. %.2f%n", totalAmount);
        } else {
            System.out.println("Reservation creation failed.");
        }
    }

    private static void updateReservation() {
        System.out.println();
        int reservationId = readInt("Enter reservation ID to update: ");
        Reservation reservation = RESERVATION_DAO.getReservationById(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }

        LocalDate checkIn = readDate("Enter new check-in date (YYYY-MM-DD): ");
        LocalDate checkOut = readDate("Enter new check-out date (YYYY-MM-DD): ");
        if (!isValidStay(checkIn, checkOut)) {
            System.out.println("Check-out date must be after check-in date.");
            return;
        }
        if (RESERVATION_DAO.hasOverlappingReservation(reservation.getRoomId(), checkIn, checkOut, reservationId)) {
            System.out.println("Updated dates conflict with another reservation.");
            return;
        }

        Room room = ROOM_DAO.getRoomById(reservation.getRoomId());
        double totalAmount = calculateTotalAmount(room.getPricePerNight(), checkIn, checkOut);
        if (RESERVATION_DAO.updateReservation(reservationId, checkIn, checkOut, totalAmount)) {
            System.out.printf("Reservation updated successfully. New amount: Rs. %.2f%n", totalAmount);
        } else {
            System.out.println("Reservation update failed.");
        }
    }

    private static void cancelReservation() {
        System.out.println();
        int reservationId = readInt("Enter reservation ID to cancel: ");
        Reservation reservation = RESERVATION_DAO.getReservationById(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }

        if (RESERVATION_DAO.deleteReservation(reservationId)) {
            System.out.println("Reservation cancelled successfully.");
        } else {
            System.out.println("Reservation cancellation failed.");
        }
    }

    private static void showDashboard() {
        System.out.println();
        List<Room> rooms = ROOM_DAO.getAllRooms();
        int totalRooms = rooms.size();
        int availableRooms = 0;
        int maintenanceRooms = 0;

        for (Room room : rooms) {
            if ("AVAILABLE".equalsIgnoreCase(room.getStatus())) {
                availableRooms++;
            } else if ("MAINTENANCE".equalsIgnoreCase(room.getStatus())) {
                maintenanceRooms++;
            }
        }

        int reservationCount = RESERVATION_DAO.countReservations();
        double totalRevenue = RESERVATION_DAO.getTotalRevenue();
        System.out.println("===== Dashboard =====");
        System.out.println("Total rooms        : " + totalRooms);
        System.out.println("Available rooms    : " + availableRooms);
        System.out.println("Maintenance rooms  : " + maintenanceRooms);
        System.out.println("Reservations       : " + reservationCount);
        System.out.printf("Revenue booked     : Rs. %.2f%n", totalRevenue);
    }

    private static void showDatabaseExposure() {
        System.out.println();
        System.out.println("===== Database Exposure =====");
        System.out.println("Database engine    : SQLite");
        System.out.println("Database file      : " + DatabaseManager.getDatabasePath());
        System.out.println("Tables             : rooms, reservations");
        System.out.println("Room records       : " + ROOM_DAO.countRooms());
        System.out.println("Reservation records: " + RESERVATION_DAO.countReservations());
        System.out.println("Primary relations  : reservations.room_id -> rooms.id");
    }

    private static boolean isValidStay(LocalDate checkIn, LocalDate checkOut) {
        return checkOut.isAfter(checkIn);
    }

    private static double calculateTotalAmount(double pricePerNight, LocalDate checkIn, LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return nights * pricePerNight;
    }

    private static String readRequiredText() {
        while (true) {
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.print("Value cannot be empty. Enter again: ");
        }
    }

    private static String readRoomStatus() {
        while (true) {
            System.out.print("Enter status (AVAILABLE/MAINTENANCE): ");
            String value = SCANNER.nextLine().trim().toUpperCase();
            if ("AVAILABLE".equals(value) || "MAINTENANCE".equals(value)) {
                return value;
            }
            System.out.println("Please enter either AVAILABLE or MAINTENANCE.");
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException exception) {
                System.out.println("Please enter a valid date in YYYY-MM-DD format.");
            }
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

    private static double readDouble(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min) {
                    return value;
                }
                System.out.println("Please enter a value greater than or equal to " + min + ".");
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
