package fintrek.parser;

import fintrek.expense.core.CategoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FileDataParserTest {

    @BeforeEach
    void setUp() {
        // Clear custom categories before each test
        CategoryManager.clearCustomCategories();
    }

    @Test
    void parseValidBudgetLine_success() {
        String line = "Monthly Budget: $2000.50";
        ParseResult<Void> result = FileDataParser.parseFileData(line);
        assertTrue(result.isSuccess());
    }

    @Test
    void parseInvalidBudgetLine_failure() {
        String line = "Monthly Budget: $abc";
        ParseResult<Void> result = FileDataParser.parseFileData(line);
        assertFalse(result.isSuccess());
    }

    @Test
    void parseInvalidCategoryLine_failure() {
        String repeatedLetters = "a".repeat(101);
        ParseResult<Void> result = FileDataParser.parseFileData(repeatedLetters);
        assertFalse(result.isSuccess());
    }

    @Test
    void parseValidExpenseLine_success() {
        String line = "Lunch | $12.50 | FOOD | 01-04-2024";
        ParseResult<Void> result = FileDataParser.parseFileData(line);
        assertTrue(result.isSuccess());
    }

    @Test
    void parseValidRecurringExpenseLine_success() {
        String line = "Netflix | $15.00 | ENTERTAINMENT | 01-03-2024 | R";
        ParseResult<Void> result = FileDataParser.parseFileData(line);
        assertTrue(result.isSuccess());
    }

    @Test
    void parseExpenseLineMissingDate_failure() {
        String line = "Lunch | $12.50 | FOOD";
        ParseResult<Void> result = FileDataParser.parseFileData(line);
        assertFalse(result.isSuccess());
    }

    @Test
    void parseExpenseLineInvalidAmount_failure() {
        String line = "Groceries | $abc | FOOD | 01-01-2024";
        ParseResult<Void> result = FileDataParser.parseFileData(line);
        assertFalse(result.isSuccess());
    }

    @Test
    void parseExpenseLineInvalidDate_failure() {
        String line = "Taxi | $8.00 | TRANSPORT | 2024-01-01";
        ParseResult<Void> result = FileDataParser.parseFileData(line);
        assertFalse(result.isSuccess());
    }
}

