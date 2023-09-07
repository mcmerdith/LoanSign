package net.mcmerdith.loansign.storage;

import net.mcmerdith.loansign.LoanSignMain;
import net.mcmerdith.loansign.model.Loan;
import net.mcmerdith.loansign.model.LoanOffer;
import net.mcmerdith.loansign.runnable.LoanShark;
import net.mcmerdith.mcmpluginlib.McmPluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoanData {
    private static final McmPluginLogger logger = McmPluginLogger.classInstance(LoanData.class);

    private static LoanData instance;

    public static LoanData instance() {
        if (instance == null) instance = new LoanData();
        return instance;
    }

    private DataStore dataStore;

    /**
     * The watchdog for loans
     */
    private LoanShark loanShark;

    /**
     * Thread-safe storage
     */
    private final ConcurrentLinkedQueue<Loan> loans = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<LoanOffer> loanOffers = new ConcurrentLinkedQueue<>();

    public void enable(DataStore dataStore) {
        // set storage handler
        this.dataStore = dataStore;
        // load data from handler
        this.dataStore.load(this);
        // start the watchdog
        this.loanShark = new LoanShark();
        // start an auto-save task (5 minute interval)
        Bukkit.getScheduler().runTaskTimerAsynchronously(LoanSignMain.instance, () -> {
            logger.info("Auto-saving data...");
            this.dataStore.save(this);
            logger.info("Done!");
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
            } catch (InterruptedException ignored) {
            }
        }

        // Save the data
        logger.info("Saving data before shutdown...");
        this.dataStore.save(this);
        logger.info("Done!");
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
    public List<Loan> getAllLoans() {
        return loans.stream().toList();
    }

    /**
     * Get all loans that have a payment due
     *
     * @return An immutable list of loans
     */
    public List<Loan> getDueLoans() {
        return loans.stream().filter(Loan::isPaymentDue).toList();
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

    /**
     * Set a loan offer
     * <p>Only one offer can exist per borrower</p>
     * <p>{@link LoanOffer#loan} -> {@link Loan#borrower}</p>
     *
     * @param offer The new offer
     */
    public void setLoanOffer(@NotNull LoanOffer offer) {
        loanOffers.removeIf(o -> o.loan.borrower == offer.loan.borrower);
        loanOffers.add(offer);
    }

    /**
     * Get a players loan offer
     *
     * @param player The players UUID
     * @return The {@link LoanOffer}, or null if no offer exists
     */
    @Nullable
    public LoanOffer getLoanOffer(UUID player) {
        return loanOffers.stream().filter(o -> o.loan.borrower.equals(player)).findFirst().orElse(null);
    }
}
