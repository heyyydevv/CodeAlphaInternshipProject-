import java.time.LocalDate;

public class Reservation {
    private final int id;
    private final int roomId;
    private final String guestName;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final double totalAmount;

    public Reservation(
        int id,
        int roomId,
        String guestName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        double totalAmount
    ) {
        this.id = id;
        this.roomId = roomId;
        this.guestName = guestName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
