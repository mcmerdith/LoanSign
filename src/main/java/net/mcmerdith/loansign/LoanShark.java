package net.mcmerdith.loansign;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class LoanShark extends BukkitRunnable {
    private final Plugin plugin;

    public LoanShark(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        runTaskLaterAsynchronously(plugin, 100L);
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
        throw new IllegalStateException("LoanShark should not be run on a timer");
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        throw new IllegalStateException("LoanShark should not be run on a timer");
    }
}
