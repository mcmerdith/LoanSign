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
    public String reason;

    /**
     * A description of the fee
     */
    @Nullable
    public String explanation;

    /**
     * Create a new fee
     *
     * @param date   The date of the fee
     * @param amount The amount of the fee
     * @param reason A description of the fee
     */
    public Fee(@NotNull Instant date, @NotNull BigDecimal amount, @Nullable String reason, @Nullable String explanation) {
        this.date = date;
        this.amount = amount;
        this.reason = reason;
        this.explanation = explanation;
    }

    /**
     * Create a new fee with a date of {@link Instant#now()}
     *
     * @param amount The amount of the fee
     * @param reason A description of the fee
     */
    public Fee(@NotNull BigDecimal amount, @Nullable String reason, @Nullable String explanation) {
        this(Instant.now(), amount, reason, explanation);
    }

    /**
     * Create a new fee with a date of {@link Instant#now()}
     *
     * @param amount The amount of the fee
     * @param reason A description of the fee
     */
    public Fee(double amount, @Nullable String reason, @Nullable String explanation) {
        this(BigDecimal.valueOf(amount), reason, explanation);
    }

    /**
     * @return A double of the amount
     * @see Fee#amount
     */
    public double getAmount() {
        return this.amount.doubleValue();
    }
}
