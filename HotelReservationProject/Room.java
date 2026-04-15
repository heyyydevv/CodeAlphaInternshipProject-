public class Room {
    private final int id;
    private final String roomNumber;
    private final String category;
    private final double pricePerNight;
    private final String status;

    public Room(int id, String roomNumber, String category, double pricePerNight, String status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public String getStatus() {
        return status;
    }
}
