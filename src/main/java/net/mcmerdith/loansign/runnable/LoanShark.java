package net.mcmerdith.loansign.runnable;

import net.mcmerdith.loansign.LoanSignMain;
import net.mcmerdith.loansign.model.Loan;
import net.mcmerdith.loansign.storage.LoanData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * A watchdog task for {@link Loan}s
 */
public class LoanShark extends BukkitRunnable {

    /**
     * The {@link BukkitTask} associated with the watchdog
     */
    private final BukkitTask task;

    /**
     * Create a new watchdog
     * <p>Task will run automatically</p>
     */
    public LoanShark() {
        this.task = runTaskTimerAsynchronously(LoanSignMain.instance, 0L, 1200L);
    }

    /**
     * @return The {@link BukkitTask} associated with the watchdog
     */
    public BukkitTask getTask() {
        return this.task;
    }

    @Override
    public void run() {
        // Run an executor on all due loans
        LoanData.instance().getDueLoans().forEach(l -> new LoanExecutor(LoanSignMain.instance, l));
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTask(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanShark should not be run synchronously");
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskLater(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanShark should not be run synchronously");
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanShark should not be run on synchronously");
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskAsynchronously(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanShark should not be run without a timer");
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanShark should not be run without a timer");
    }
}
