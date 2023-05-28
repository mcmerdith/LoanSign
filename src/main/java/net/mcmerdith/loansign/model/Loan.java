package net.mcmerdith.loansign.model;

import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.Period;
import java.util.UUID;

public class Loan {
    public UUID giver;
    public UUID receiver;
    public double amount;
    public int rate;
    public int divPerYear;
    public Instant initiation;
    public Instant expiry;

    /**
     * Gson Constructor: Do not use
     */
    public Loan() {
    }

    /**
     * Create a new loan
     *
     * @param giver      The UUID of the player giving the loan
     * @param receiver   The UUID of the player receiving the loan
     * @param amount     The amount of the loan
     * @param initiation When the loan was created
     * @param expiry     When the loan will expire
     */
    public Loan(UUID giver, UUID receiver, double amount, int rate, int divPerYear, Instant initiation, Instant expiry) {
        this.giver = giver;
        this.receiver = receiver;
        this.amount = amount;
        this.rate = rate;
        this.divPerYear = divPerYear;
        this.initiation = initiation;
        this.expiry = expiry;

    }

    /**
     * Create a new loan initiating immediately with a specified duration
     *
     * @param giver    The UUID of the player giving the loan
     * @param receiver The UUID of the player receiving the loan
     * @param amount   The amount of the loan
     * @param duration The duration of the loan
     */
    public Loan(UUID giver, UUID receiver, double amount, int rate, int divPerYear, Period duration) {
        this(giver, receiver, amount, rate, divPerYear, Instant.now(), (Instant) duration.addTo(Instant.now()));
    }
}
