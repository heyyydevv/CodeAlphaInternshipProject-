import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {
    private static final Path DATA_DIRECTORY = resolveDataDirectory();
    private static final Path DATABASE_PATH = DATA_DIRECTORY.resolve("hotel_reservation.db");
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH.toString();

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("PRAGMA journal_mode = MEMORY");
            statement.execute("PRAGMA temp_store = MEMORY");
        }
        return connection;
    }

    public static void initializeDatabase() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            Class.forName("org.sqlite.JDBC");
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load SQLite JDBC driver.", exception);
        }

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    room_number TEXT NOT NULL UNIQUE,
                    category TEXT NOT NULL,
                    price_per_night REAL NOT NULL CHECK (price_per_night > 0),
                    status TEXT NOT NULL CHECK (status IN ('AVAILABLE', 'MAINTENANCE'))
                )
                """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS reservations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    room_id INTEGER NOT NULL,
                    guest_name TEXT NOT NULL,
                    check_in_date TEXT NOT NULL,
                    check_out_date TEXT NOT NULL,
                    total_amount REAL NOT NULL CHECK (total_amount > 0),
                    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT
                )
                """);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to initialize database.", exception);
        }

        seedRooms();
    }

    public static String getDatabasePath() {
        return DATABASE_PATH.toString();
    }

    private static Path resolveDataDirectory() {
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isBlank()) {
            return Paths.get(localAppData, "HotelReservationSystem", "data");
        }
        return Paths.get(System.getProperty("java.io.tmpdir"), "HotelReservationSystem", "data");
    }

    private static void seedRooms() {
        RoomDAO roomDAO = new RoomDAO();
        if (roomDAO.countRooms() > 0) {
            return;
        }

        roomDAO.createRoom("101", "Standard", 2500.00, "AVAILABLE");
        roomDAO.createRoom("102", "Standard", 2800.00, "AVAILABLE");
        roomDAO.createRoom("201", "Deluxe", 4200.00, "AVAILABLE");
        roomDAO.createRoom("202", "Deluxe", 4500.00, "MAINTENANCE");
        roomDAO.createRoom("301", "Suite", 6800.00, "AVAILABLE");
    }
}
