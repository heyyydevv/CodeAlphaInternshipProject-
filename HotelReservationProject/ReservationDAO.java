import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    public List<Reservation> getAllReservations() {
        String query = """
            SELECT id, room_id, guest_name, check_in_date, check_out_date, total_amount
            FROM reservations
            ORDER BY check_in_date, id
            """;
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reservations.add(mapReservation(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to fetch reservations.", exception);
        }

        return reservations;
    }

    public Reservation getReservationById(int reservationId) {
        String query = """
            SELECT id, room_id, guest_name, check_in_date, check_out_date, total_amount
            FROM reservations
            WHERE id = ?
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, reservationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapReservation(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to fetch reservation.", exception);
        }
    }

    public boolean createReservation(int roomId, String guestName, LocalDate checkIn, LocalDate checkOut, double totalAmount) {
        String query = """
            INSERT INTO reservations (room_id, guest_name, check_in_date, check_out_date, total_amount)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, roomId);
            statement.setString(2, guestName);
            statement.setString(3, checkIn.toString());
            statement.setString(4, checkOut.toString());
            statement.setDouble(5, totalAmount);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to create reservation.", exception);
        }
    }

    public boolean updateReservation(int reservationId, LocalDate checkIn, LocalDate checkOut, double totalAmount) {
        String query = """
            UPDATE reservations
            SET check_in_date = ?, check_out_date = ?, total_amount = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, checkIn.toString());
            statement.setString(2, checkOut.toString());
            statement.setDouble(3, totalAmount);
            statement.setInt(4, reservationId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to update reservation.", exception);
        }
    }

    public boolean deleteReservation(int reservationId) {
        String query = "DELETE FROM reservations WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, reservationId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to delete reservation.", exception);
        }
    }

    public boolean hasOverlappingReservation(int roomId, LocalDate checkIn, LocalDate checkOut, Integer excludeReservationId) {
        String query = """
            SELECT COUNT(*)
            FROM reservations
            WHERE room_id = ?
              AND check_in_date < ?
              AND check_out_date > ?
            """ + (excludeReservationId != null ? " AND id <> ?" : "");

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, roomId);
            statement.setString(2, checkOut.toString());
            statement.setString(3, checkIn.toString());
            if (excludeReservationId != null) {
                statement.setInt(4, excludeReservationId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to check reservation conflicts.", exception);
        }
    }

    public int countReservations() {
        String query = "SELECT COUNT(*) FROM reservations";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to count reservations.", exception);
        }
    }

    public int countReservationsForRoom(int roomId) {
        String query = "SELECT COUNT(*) FROM reservations WHERE room_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to count room reservations.", exception);
        }
    }

    public double getTotalRevenue() {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM reservations";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getDouble(1) : 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to calculate revenue.", exception);
        }
    }

    private Reservation mapReservation(ResultSet resultSet) throws SQLException {
        return new Reservation(
            resultSet.getInt("id"),
            resultSet.getInt("room_id"),
            resultSet.getString("guest_name"),
            LocalDate.parse(resultSet.getString("check_in_date")),
            LocalDate.parse(resultSet.getString("check_out_date")),
            resultSet.getDouble("total_amount")
        );
    }
}
