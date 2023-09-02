package net.mcmerdith.loansign.storage;

import net.mcmerdith.loansign.LoanSignLogger;
import net.mcmerdith.loansign.model.Loan;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class DataSource {
    public static LoanSignLogger logger = LoanSignLogger.instance("DataSource");

    protected List<Loan> loans = new ArrayList<>();

    /**
     * Load the Data Source
     *
     * @return If loading was successful
     */
    public abstract boolean load();

    /**
     * Save the Data Source (if necessary)
     *
     * @return If the saving was successful
     */
    public abstract boolean save();

    public void createLoan(Loan loan) {
        loans.add(loan);
    }

    /**
     * Get all loans on the server
     *
     * @return An immutable list of loans
     */
    public List<Loan> getAllLoans() {
        return Collections.unmodifiableList(loans);
    }

    /**
     * Get all loans given by a specified player
     *
     * @param giver The player
     * @return An immutable list of loans
     */
    public List<Loan> getLoansFrom(UUID giver) {
        return loans.stream().filter(loan -> loan.lender == giver).toList();
    }

    /**
     * Get all loans given to a specified player
     *
     * @param borrower The player
     * @return An immutable list of loans
     */
    public List<Loan> getLoansFor(UUID borrower) {
        return loans.stream().filter(loan -> loan.borrower == borrower).toList();
    }

    /**
     * Get all loans that have expired
     *
     * @return An immutable list of loans
     */
    public List<Loan> getExpiredLoans() {
        return loans.stream().filter(loan -> Instant.now().isAfter(loan.getDueDate())).toList();
    }
}
