package net.mcmerdith.loansign.model;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class FakeEconomy implements Economy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "FakeEcon";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return "$" + amount;
    }

    @Override
    public String currencyNamePlural() {
        return "dollars";
    }

    @Override
    public String currencyNameSingular() {
        return "dollar";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return true;
    }

    @Override
    public double getBalance(String playerName) {
        return 10000;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return 10000;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return 10000;
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return 10000;
    }

    @Override
    public boolean has(String playerName, double amount) {
        return true;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return true;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return true;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return true;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return false;
    }
}
