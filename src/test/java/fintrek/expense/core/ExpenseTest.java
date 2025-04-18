package fintrek.expense.core;

import fintrek.command.add.AddCommand;
import fintrek.command.registry.CommandResult;
import fintrek.util.RecurringExpenseProcessor;
import fintrek.misc.MessageDisplayer;
import fintrek.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpenseTest {
    private static final RegularExpenseManager regularExpenseManager =
            RegularExpenseManager.getInstance();
    private static final RecurringExpenseManager recurringExpenseManager =
            RecurringExpenseManager.getInstance();

    @BeforeEach
    public void setUp() {
        regularExpenseManager.clear();
        recurringExpenseManager.clear();
    }

    /**
     * Verifies that the getter in the Expense class for the expense description works.
     * @param description a valid description for the expense
     */
    @ParameterizedTest
    @ValueSource(strings = {"mrt", "eat", "laptop for CS2113", "123"})
    public void testGetValidExpensesDescription(String description) {
        Expense expense = new Expense(description, 10.0,
                "uncategorized", LocalDate.now()); // Dummy amount & category
        assertEquals(description, expense.getDescription());
    }
    /**
     * Verifies that the getter in the Expense class for the expense category works.
     * @param category a valid category for the expense
     */
    @ParameterizedTest
    @ValueSource(strings = {"Studies", "Food", "baaaaaa", "Computer Science", "transport"})
    public void testGetValidExpensesCategory(String category) {
        Expense expense = new Expense("for testing", 10.0,
                category, LocalDate.now()); // Dummy amount & category
        assertEquals(category.toUpperCase(), expense.getCategory());
    }

    /**
     * Verifies that the getter in the Expense class for the expense amount works.
     * @param amount a valid description for the expense
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.50, 1.50, 2.50, 12.50, 250.00 })
    public void testGetValidExpensesAmount(double amount) {
        Expense expense = new Expense("for testing", amount,
                "uncategorized", LocalDate.now()); // Dummy amount & category
        assertEquals(amount, expense.getAmount());
    }

    /**
     * Tests whether inputting zero or negative amounts for expenses
     * results in an exception being thrown
     */
    @ParameterizedTest
    @CsvSource({
        "'gift from friend', -5.0, 'gifts'",
        "'just nothing', 0.0, 'uncategorized'",
        "'monthly allowance', -750.0, 'allowance'",
        "'salary', -1250, 'salary'"
    })
    public void testSetInvalidExpensesAmount_returnsError(String description, double amount, String category) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Expense(description, amount, category, LocalDate.now())
        );
        assertEquals(MessageDisplayer.INVALID_AMOUNT, exception.getMessage());
    }

    /**
     * Tests whether the toString() method for the Expense class
     * effectively converts it to a string format of form
     * "{description} | ${amount} | {category} | {date}
     * where the date format is "dd-MM-yyyy"
     */
    @Test
    public void testExpensesToStringConversion() {
        String dateToday = "02-04-2025";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(dateToday, formatter);
        assertEquals("eat | $10.00 | FOOD | " + dateToday,
                new Expense("eat", 10.0, "food", date).toString());
        assertEquals("mrt | $2.30 | TRANSPORT | " + dateToday,
                new Expense("mrt", 2.30, "transport", date).toString());
        assertEquals("dinner | $15.90 | FOOD | " + dateToday,
                new Expense("dinner", 15.90, "food", date).toString());
    }

    /**
     * Verifies that a recurring expense will not be added into the main
     * list of expenses if the date on the expense is still beyond today's date.
     */
    @Test
    public void checkRecurringExpenseTest_existingRecurringAfterTodayDate_success() {
        int month = LocalDate.now().getMonthValue();
        int day = LocalDate.now().getDayOfMonth();
        String oldDate = (day + 1) + "-" + (month + 1) +"-2025";
        AddCommand addCommand = new AddCommand(true);
        String input = "Spotify $9.99 /c entertainment /dt " + oldDate;
        CommandResult result = addCommand.execute(input);

        TestUtils.assertCommandFailure(result, input);
        RecurringExpenseProcessor.checkAndInsertDueExpenses(recurringExpenseManager,
                regularExpenseManager);

        TestUtils.assertCorrectListSize(0, input);
    }

    /**
     * Verifies if a recurring expense will be added into the main list of
     * expenses if the date on the recurring expense matches today's date.
     */
    @Test
    public void checkRecurringExpenseTest_existingRecurringMatchingDate_success() {
        String oldDate = "01-01-2025";
        AddCommand addCommand = new AddCommand(true);
        String input = "Spotify $9.99 /c entertainment /dt " + oldDate;
        LocalDate dateToday = LocalDate.now();
        CommandResult result = addCommand.execute(input);

        TestUtils.assertCommandSuccess(result, input);

        RecurringExpenseManager.getInstance().get(0).updateDate(dateToday);
        RecurringExpenseProcessor.checkAndInsertDueExpenses(RecurringExpenseManager.getInstance(),
                RegularExpenseManager.getInstance());

        TestUtils.assertCorrectListSize(1, input);
    }
}
