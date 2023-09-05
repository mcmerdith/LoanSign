package net.mcmerdith.loansign.model;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.Instant;

public class Payment {
    /**
     * The date of the payment
     */
    @NotNull
    public Instant date;

    /**
     * The amount of the payment
     */
    @NotNull
    public BigDecimal amount;

    /**
     * The deficit from the expected payment
     */
    @NotNull
    public BigDecimal deficit;

    @Nullable
    public Fee fee = null;

    /**
     * Create a new payment
     *
     * @param date    The date of the payment
     * @param amount  The amount of the payment
     * @param deficit The deficit from the expected payment
     */
    public Payment(@NotNull Instant date, @NotNull BigDecimal amount, @NotNull BigDecimal deficit) {
        this.date = date;
        this.amount = amount;
        this.deficit = deficit;
    }

    /**
     * Create a new payment with a date of {@link Instant#now()}
     *
     * @param amount  The amount of the payment
     * @param deficit The deficit from the payment expected
     */
    public Payment(@NotNull BigDecimal amount, @NotNull BigDecimal deficit) {
        this(Instant.now(), amount, deficit);
    }

    /**
     * Create a new payment with a date of {@link Instant#now()}
     *
     * @param amount  The amount of the payment
     * @param deficit The deficit from the payment expected
     */
    public Payment(double amount, double deficit) {
        this(BigDecimal.valueOf(amount), BigDecimal.valueOf(deficit));
    }

    public void setFee(@Nullable Fee fee) {
        this.fee = fee;
    }

    /**
     * Also see {@link Payment#amount}
     *
     * @return A double of the amount
     */
    public double getAmount() {
        return this.amount.doubleValue();
    }

    /**
     * Also see {@link Payment#deficit}
     *
     * @return A double of the deficit
     */
    public double getDeficit() {
        return this.deficit.doubleValue();
    }

    public BigDecimal getTotal() {
        return amount.add(deficit);
    }

    public double getTotal_d() {
        return getAmount() + getDeficit();
    }
}
