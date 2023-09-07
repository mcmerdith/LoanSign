package net.mcmerdith.loansign.command;

import net.mcmerdith.mcmpluginlib.command.McmCommand;
import net.mcmerdith.mcmpluginlib.command.StaticTabComplete;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LoanCommand extends McmCommand {
    public LoanCommand() {
        setTabCompleter(0, new StaticTabComplete("accept", "decline"));
    }

    @Override
    protected boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }
}
