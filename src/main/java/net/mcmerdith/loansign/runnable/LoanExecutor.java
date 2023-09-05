package net.mcmerdith.loansign.runnable;

import net.mcmerdith.loansign.model.Loan;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * An executor task for {@link Loan}s
 * <p>This task should never be run asynchronously or on a timer</p>
 */
public class LoanExecutor extends BukkitRunnable {

    private final Loan loan;

    /**
     * Create a new executor
     * <p>Task will run automatically</p>
     *
     * @param plugin The plugin owning the executor
     */
    public LoanExecutor(Plugin plugin, Loan loan) {
        this.loan = loan;
        this.runTask(plugin);
    }

    @Override
    public void run() {

    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskAsynchronously(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanExecutor should not be run asynchronously");
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanExecutor should not be run asynchronously");
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanExecutor should not be run on a timer");
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanExecutor should not be run asynchronously");
    }
}
