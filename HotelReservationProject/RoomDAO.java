import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    public List<Room> getAllRooms() {
        String query = "SELECT id, room_number, category, price_per_night, status FROM rooms ORDER BY room_number";
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rooms.add(mapRoom(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to fetch rooms.", exception);
        }

        return rooms;
    }

    public Room getRoomById(int roomId) {
        String query = "SELECT id, room_number, category, price_per_night, status FROM rooms WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRoom(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to fetch room.", exception);
        }
    }

    public boolean createRoom(String roomNumber, String category, double pricePerNight, String status) {
        String query = """
            INSERT INTO rooms (room_number, category, price_per_night, status)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, roomNumber);
            statement.setString(2, category);
            statement.setDouble(3, pricePerNight);
            statement.setString(4, status);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            return false;
        }
    }

    public boolean updateRoom(int roomId, String category, double pricePerNight, String status) {
        String query = """
            UPDATE rooms
            SET category = ?, price_per_night = ?, status = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);
            statement.setDouble(2, pricePerNight);
            statement.setString(3, status);
            statement.setInt(4, roomId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to update room.", exception);
        }
    }

    public boolean deleteRoom(int roomId) {
        String query = "DELETE FROM rooms WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, roomId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to delete room.", exception);
        }
    }

    public int countRooms() {
        String query = "SELECT COUNT(*) FROM rooms";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to count rooms.", exception);
        }
    }

    private Room mapRoom(ResultSet resultSet) throws SQLException {
        return new Room(
            resultSet.getInt("id"),
            resultSet.getString("room_number"),
            resultSet.getString("category"),
            resultSet.getDouble("price_per_night"),
            resultSet.getString("status")
        );
    }
}
