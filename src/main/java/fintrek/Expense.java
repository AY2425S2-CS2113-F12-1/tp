package fintrek;

public class Expense {
    public static final String INVALID_AMOUNT = "Amount cannot be negative";
    private final String description;
    private final double amount;
    private final String category;

    public Expense(String description, double amount, String category) {
        if (amount < 0) {
            throw new IllegalArgumentException(INVALID_AMOUNT);
        }
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }
}
