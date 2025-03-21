package fintrek;

import fintrek.misc.MessageDisplayer;

public class Expense {
    private final String description;
    private final double amount;
    private final String category;

    public Expense(String description, double amount, String category) {
        if (amount <= 0) {
            throw new IllegalArgumentException(MessageDisplayer.INVALID_AMOUNT);
        }
        assert amount > 0 : MessageDisplayer.INVALID_AMOUNT;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        assert amount > 0 : MessageDisplayer.INVALID_AMOUNT;
        return amount;
    }

    @Override
    public String toString() {
        return description + " | $" + String.format("%.2f", amount) + " | " + category;
    }
}
