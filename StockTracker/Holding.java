public class Holding {
    private final Stock stock;
    private int quantity;

    public Holding(Stock stock, int quantity) {
        this.stock = stock;
        this.quantity = quantity;
    }

    public Stock getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        quantity += amount;
    }

    public void removeQuantity(int amount) {
        quantity -= amount;
    }

    public double getMarketValue() {
        return quantity * stock.getPrice();
    }
}
