package fintrek.command.delete;

import fintrek.expense.core.Expense;
import fintrek.command.Command;
import fintrek.command.registry.CommandInfo;
import fintrek.command.registry.CommandResult;
import fintrek.misc.MessageDisplayer;

import fintrek.util.InputValidator;

/**
 * Handles the deletion of a specified expense from the list.
 *
 * <p>This command expects a single positive integer argument representing the
 * index of the expense to delete. It performs input validation, checks index bounds,
 * removes the specified expense, and returns a formatted success or error message.</p>
 *
 * <p>The index is 1-based (i.e., the first expense has index 1).</p>
 *
 * <p>Supports both normal and recurring expenses depending on the value of {@code isRecurring}
 * passed to the constructor.</p>
 *
 * <p>Example usage:
 * <pre>
 * /delete 2
 * </pre>
 * Deletes the expense at index 2 from the list.</p>
 *
 * @see Command
 * @see fintrek.expense.service.ExpenseService
 */
@CommandInfo(
        recurringFormat = "Format: /delete-recurring <RECURRING_EXPENSE_NUMBER>",
        regularFormat = "Format: /delete <EXPENSE_NUMBER>",
        description = """
            INDEX must be a positive integer > 0.
            """,
        recurringExample = "Example: /delete-recurring 2 - deletes the recurring expense with index 2 on the list.",
        regularExample = "Example: /delete 2 - deletes the regular expense with index 2 on the list."
)
public class DeleteCommand extends Command {

    public DeleteCommand(boolean isRecurring) {
        super(isRecurring);
    }

    /**
     * Delete a recurring or general expense of a valid index
     * @param arguments is the index of the expense to be removed from the list
     * @return a {@code CommandResult} object telling whether
     *      the execution is successful or not, and an error/success message
     */
    @Override
    public CommandResult execute(String arguments) {
        if (InputValidator.isNullOrBlank(arguments)) {
            return new CommandResult(false, MessageDisplayer.IDX_EMPTY_MESSAGE);
        }

        long longIndex;
        try {
            longIndex = Long.parseLong(arguments.trim());
        } catch (NumberFormatException e) {
            return new CommandResult(false, MessageDisplayer.INVALID_IDX_FORMAT_MESSAGE);
        }

        int smallestValidIndex = 1;
        int upperBound = service.countExpenses();

        if (longIndex < smallestValidIndex || longIndex > upperBound) {
            return new CommandResult(false, MessageDisplayer.IDX_OUT_OF_BOUND_MESSAGE);
        }

        int zeroBaseExpenseIndex = (int) longIndex - 1;
        Expense removedExpense = service.popExpense(zeroBaseExpenseIndex);
        int remaining = service.countExpenses();
        String expenseStr = '"' + removedExpense.toString() + '"';
        String message = (isRecurringExpense) ?
                String.format(MessageDisplayer.DELETE_RECURRING_SUCCESS_MESSAGE_TEMPLATE, expenseStr, remaining) :
                String.format(MessageDisplayer.DELETE_SUCCESS_MESSAGE_TEMPLATE, expenseStr, remaining);
        return new CommandResult(true, message);
    }
}
