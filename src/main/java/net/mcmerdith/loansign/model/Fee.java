package net.mcmerdith.loansign.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;

public class Fee {
    /**
     * The date of the fee
     */
    @NotNull
    public Instant date;
    /**
     * The amount of the fee
     */
    @NotNull
    public BigDecimal amount;
    /**
     * A description of the fee
     */
    @Nullable
    public String description;

    /**
     * Create a new fee
     *
     * @param date        The date of the fee
     * @param amount      The amount of the fee
     * @param description A description of the fee
     */
    public Fee(@NotNull Instant date, @NotNull BigDecimal amount, @Nullable String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    /**
     * Create a new fee with a date of {@link Instant#now()}
     *
     * @param amount      The amount of the fee
     * @param description A description of the fee
     */
    public Fee(@NotNull BigDecimal amount, @Nullable String description) {
        this(Instant.now(), amount, description);
    }

    /**
     * Create a new fee with a date of {@link Instant#now()}
     *
     * @param amount      The amount of the fee
     * @param description A description of the fee
     */
    public Fee(double amount, @Nullable String description) {
        this(BigDecimal.valueOf(amount), description);
    }

    /**
     * Also see {@link Fee#amount}
     *
     * @return A double of the amount
     */
    public double getAmount() {
        return this.amount.doubleValue();
    }
}
