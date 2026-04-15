import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Portfolio {
    private double cashBalance;
    private final Map<String, Holding> holdings = new LinkedHashMap<>();

    public Portfolio(double startingCash) {
        this.cashBalance = startingCash;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public Collection<Holding> getHoldings() {
        return holdings.values();
    }

    public Holding getHolding(String symbol) {
        return holdings.get(symbol);
    }

    public boolean buyStock(Stock stock, int quantity) {
        double totalCost = stock.getPrice() * quantity;
        if (totalCost > cashBalance) {
            return false;
        }

        cashBalance -= totalCost;
        Holding holding = holdings.get(stock.getSymbol());
        if (holding == null) {
            holdings.put(stock.getSymbol(), new Holding(stock, quantity));
        } else {
            holding.addQuantity(quantity);
        }
        return true;
    }

    public boolean sellStock(Stock stock, int quantity) {
        Holding holding = holdings.get(stock.getSymbol());
        if (holding == null || quantity > holding.getQuantity()) {
            return false;
        }

        double totalValue = stock.getPrice() * quantity;
        cashBalance += totalValue;
        holding.removeQuantity(quantity);

        if (holding.getQuantity() == 0) {
            holdings.remove(stock.getSymbol());
        }
        return true;
    }

    public double getPortfolioValue() {
        double holdingsValue = 0;
        for (Holding holding : holdings.values()) {
            holdingsValue += holding.getMarketValue();
        }
        return cashBalance + holdingsValue;
    }

    public void restoreHolding(Stock stock, int quantity) {
        if (quantity <= 0) {
            return;
        }
        holdings.put(stock.getSymbol(), new Holding(stock, quantity));
    }
}
