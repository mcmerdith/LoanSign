package net.mcmerdith.loansign.storage;

import net.mcmerdith.loansign.LoanSignLogger;
import net.mcmerdith.loansign.LoanSignMain;
import net.mcmerdith.loansign.model.Loan;
import net.mcmerdith.loansign.runnable.LoanShark;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoanData {

    private static LoanData instance;

    public static LoanData instance() {
        if (instance == null) instance = new LoanData();
        return instance;
    }

    private DataStorage dataStorage;

    /**
     * The watchdog for loans
     */
    private LoanShark loanShark;

    /**
     * Thread-safe storage
     */
    private final ConcurrentLinkedQueue<Loan> loans = new ConcurrentLinkedQueue<>();

    public void enable(DataStorage dataStorage) {
        // set storage handler
        this.dataStorage = dataStorage;
        // load data from handler
        this.dataStorage.load(this);
        // start the watchdog
        this.loanShark = new LoanShark();
        // start an auto-save task (5 minute interval)
        Bukkit.getScheduler().runTaskTimerAsynchronously(LoanSignMain.instance, () -> {
            LoanSignLogger.DATASOURCE.info("Auto-saving data...");
            this.dataStorage.save(this);
            LoanSignLogger.DATASOURCE.info("Done!");
        }, 6000L, 6000L);
    }

    public void disable() {
        // Stop the watchdog and wait for completion
        BukkitTask watchdog = loanShark.getTask();
        Bukkit.getScheduler().cancelTask(watchdog.getTaskId());
        while (Bukkit.getScheduler().isCurrentlyRunning(watchdog.getTaskId())) {
            try {
                // The watchdog has to complete before the data can be saved
                //noinspection BusyWait
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }

        // Save the data
        LoanSignLogger.DATASOURCE.info("Saving data before shutdown...");
        this.dataStorage.save(this);
        LoanSignLogger.DATASOURCE.info("Done!");
    }

    /**
     * Set the loans tracked in the Data Source
     *
     * @param loans The new loans
     */
    public void setLoans(Collection<Loan> loans) {
        this.loans.clear();
        this.loans.addAll(loans);
    }

    /**
     * Add a loan to be tracked in the Data Source
     *
     * @param loan The new loan
     */
    public void addLoan(Loan loan) {
        loans.add(loan);
    }

    /**
     * Get all loans on the server
     *
     * @return An immutable collection of loans
     */
    public Collection<Loan> getAllLoans() {
        return loans.stream().toList();
    }

    /**
     * Get all loans that have a payment due
     *
     * @return
     */
    public Collection<Loan> getDueLoans() {
        return loans.stream().filter(Loan::isPaymentDue).toList();
    }

    /**
     * Get all loans given by a specified player
     *
     * @param giver The player
     * @return An immutable list of loans
     */
    public Collection<Loan> getLoansFrom(UUID giver) {
        return loans.stream().filter(loan -> loan.lender == giver).toList();
    }

    /**
     * Get all loans given to a specified player
     *
     * @param borrower The player
     * @return An immutable list of loans
     */
    public Collection<Loan> getLoansFor(UUID borrower) {
        return loans.stream().filter(loan -> loan.borrower == borrower).toList();
    }

    /**
     * Get all loans that have expired
     *
     * @return An immutable list of loans
     */
    public Collection<Loan> getExpiredLoans() {
        return loans.stream().filter(loan -> Instant.now().isAfter(loan.getDueDate())).toList();
    }
}
