import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class StockTradingPlatform {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Random RANDOM = new Random();
    private static final Path DATA_FILE = Path.of("stock-trading-platform", "data", "portfolio.csv");

    private static final Map<String, Stock> market = new LinkedHashMap<>();
    private static final Portfolio portfolio = new Portfolio(100000.00);

    public static void main(String[] args) {
        seedMarket();
        loadPortfolio();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    showMarketStocks();
                    break;
                case 2:
                    buyStock();
                    break;
                case 3:
                    sellStock();
                    break;
                case 4:
                    viewPortfolio();
                    break;
                case 5:
                    updateMarketPrices();
                    break;
                case 6:
                    savePortfolio();
                    System.out.println("Portfolio saved successfully.");
                    break;
                case 7:
                    savePortfolio();
                    System.out.println("Portfolio saved successfully.");
                    running = false;
                    System.out.println("Exiting Stock Trading Platform. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please choose between 1 and 7.");
            }
        }

        SCANNER.close();
    }

    private static void seedMarket() {
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 192.45));
        market.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 417.20));
        market.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 166.85));
        market.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 182.60));
        market.put("TSLA", new Stock("TSLA", "Tesla Inc.", 171.30));
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Stock Trading Platform =====");
        System.out.println("1. View market stocks");
        System.out.println("2. Buy stock");
        System.out.println("3. Sell stock");
        System.out.println("4. View portfolio");
        System.out.println("5. Simulate market update");
        System.out.println("6. Save portfolio");
        System.out.println("7. Exit");
    }

    private static void showMarketStocks() {
        System.out.println();
        System.out.printf("%-8s %-22s %-12s%n", "Symbol", "Company", "Price");
        System.out.println("------------------------------------------------");
        for (Stock stock : market.values()) {
            System.out.printf("%-8s %-22s $%-11.2f%n",
                stock.getSymbol(),
                stock.getCompanyName(),
                stock.getPrice());
        }
    }

    private static void buyStock() {
        System.out.println();
        String symbol = readSymbol();
        Stock stock = market.get(symbol);
        if (stock == null) {
            System.out.println("Stock symbol not found in the market list.");
            return;
        }

        int quantity = readPositiveInt("Enter quantity to buy: ");
        double totalCost = stock.getPrice() * quantity;

        if (!portfolio.buyStock(stock, quantity)) {
            System.out.printf("Insufficient balance. You need $%.2f but have $%.2f.%n",
                totalCost,
                portfolio.getCashBalance());
            return;
        }

        System.out.printf("Successfully bought %d shares of %s for $%.2f.%n",
            quantity,
            stock.getSymbol(),
            totalCost);
    }

    private static void sellStock() {
        System.out.println();
        String symbol = readSymbol();
        Stock stock = market.get(symbol);
        if (stock == null) {
            System.out.println("Stock symbol not found in the market list.");
            return;
        }

        int quantity = readPositiveInt("Enter quantity to sell: ");
        if (!portfolio.sellStock(stock, quantity)) {
            System.out.println("Sale failed. Check whether you own enough shares.");
            return;
        }

        double totalValue = stock.getPrice() * quantity;
        System.out.printf("Successfully sold %d shares of %s for $%.2f.%n",
            quantity,
            stock.getSymbol(),
            totalValue);
    }

    private static void viewPortfolio() {
        System.out.println();
        System.out.printf("Cash balance     : $%.2f%n", portfolio.getCashBalance());

        if (portfolio.getHoldings().isEmpty()) {
            System.out.println("No holdings in the portfolio yet.");
            System.out.printf("Total value      : $%.2f%n", portfolio.getPortfolioValue());
            return;
        }

        System.out.printf("%-8s %-10s %-12s %-14s%n", "Symbol", "Quantity", "Price", "Market Value");
        System.out.println("----------------------------------------------------");
        for (Holding holding : portfolio.getHoldings()) {
            System.out.printf("%-8s %-10d $%-11.2f $%-13.2f%n",
                holding.getStock().getSymbol(),
                holding.getQuantity(),
                holding.getStock().getPrice(),
                holding.getMarketValue());
        }
        System.out.printf("Total value      : $%.2f%n", portfolio.getPortfolioValue());
    }

    private static void updateMarketPrices() {
        System.out.println();
        System.out.println("Market update:");
        for (Stock stock : market.values()) {
            double percentChange = -5 + (10 * RANDOM.nextDouble());
            double updatedPrice = stock.getPrice() * (1 + (percentChange / 100));
            stock.setPrice(Math.max(10.0, updatedPrice));
            System.out.printf("%s moved by %.2f%% and is now $%.2f%n",
                stock.getSymbol(),
                percentChange,
                stock.getPrice());
        }
    }

    private static void loadPortfolio() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        try {
            List<String> rows = Files.readAllLines(DATA_FILE);
            for (String row : rows) {
                String[] parts = row.split(",", -1);
                if (parts.length != 2) {
                    continue;
                }

                if ("CASH".equals(parts[0])) {
                    double savedCash = Double.parseDouble(parts[1]);
                    portfolio.setCashBalance(savedCash);
                    continue;
                }

                Stock stock = market.get(parts[0]);
                if (stock == null) {
                    continue;
                }

                int quantity = Integer.parseInt(parts[1]);
                if (quantity > 0) {
                    portfolio.restoreHolding(stock, quantity);
                }
            }
            System.out.println("Loaded saved portfolio data.");
        } catch (IOException | NumberFormatException exception) {
            System.out.println("Could not load portfolio data: " + exception.getMessage());
        }
    }

    private static void savePortfolio() {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            StringBuilder content = new StringBuilder();
            content.append("CASH,").append(portfolio.getCashBalance()).append(System.lineSeparator());
            for (Holding holding : portfolio.getHoldings()) {
                content.append(holding.getStock().getSymbol())
                    .append(",")
                    .append(holding.getQuantity())
                    .append(System.lineSeparator());
            }
            Files.writeString(DATA_FILE, content.toString());
        } catch (IOException exception) {
            System.out.println("Could not save portfolio data: " + exception.getMessage());
        }
    }

    private static String readSymbol() {
        System.out.print("Enter stock symbol: ");
        return SCANNER.nextLine().trim().toUpperCase();
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

    private static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Quantity must be greater than zero.");
        }
    }
}
