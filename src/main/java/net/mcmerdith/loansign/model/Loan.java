package net.mcmerdith.loansign.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Loan {

    /**
     * The {@link UUID} identifying this loan
     */
    public UUID loanID;

    /**
     * The {@link UUID} of the lender
     */
    public UUID lender;

    /**
     * The {@link UUID} of the borrower
     */
    public UUID borrower;

    /**
     * The initial amount of the loan
     */
    public BigDecimal loanAmount;

    /**
     * The initiation date of the loan
     */
    public Instant initiation;

    /**
     * The current period the loan is on
     */
    public int currentPeriod;

    /**
     * The number of periods the loan lasts
     */
    public int totalPeriods;

    /**
     * The time unit represented by each period
     */
    public ChronoUnit periodUnit;

    /**
     * The list of payments made on this loan
     */
    public List<Payment> payments;

    /**
     * The list of fees on this loan
     */
    public List<Fee> fees;

    /**
     * Gson Constructor: Do not use
     */
    public Loan() {
    }

    /**
     * Create a new loan
     * <p>
     * Data constructor
     *
     * @param loanID        A {@link UUID} for this loan
     * @param lender        The {@link UUID} of the player giving the loan
     * @param borrower      The {@link UUID} of the player receiving the loan
     * @param initialAmount The initial amount of the loan
     * @param initiation    When the loan was created
     * @param currentPeriod The current period of this loan
     * @param totalPeriods  The total number of periods for this loan
     * @param periodUnit    The time unit of each period
     * @param payments      The list of payments made
     */
    public Loan(
            UUID loanID,
            UUID lender,
            UUID borrower,
            BigDecimal initialAmount,
            BigDecimal rate,
            Instant initiation,
            int currentPeriod,
            int totalPeriods,
            ChronoUnit periodUnit,
            List<Payment> payments,
            List<Fee> fees
    ) {

        this.loanID = loanID;
        this.lender = lender;
        this.borrower = borrower;
        this.loanAmount = initialAmount.multiply(rate.add(BigDecimal.ONE).pow(totalPeriods));
        this.initiation = initiation;
        this.currentPeriod = currentPeriod;
        this.totalPeriods = totalPeriods;
        this.periodUnit = periodUnit;
        this.payments = payments;
        this.fees = fees;
    }

    /**
     * Create a new loan initiating immediately with a specified duration
     *
     * @param lender        The UUID of the player giving the loan
     * @param borrower      The UUID of the player receiving the loan
     * @param initialAmount The initial amount of the loan
     * @param rate          The interest rate per day
     * @param durationDays  The duration of the loan in days
     */
    public Loan(UUID lender, UUID borrower, double initialAmount, double rate, int durationDays) {
        this(
                UUID.randomUUID(),
                lender,
                borrower,
                BigDecimal.valueOf(initialAmount),
                BigDecimal.valueOf(rate),
                Instant.now(),
                0,
                durationDays,
                ChronoUnit.DAYS,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    /**
     * @return The {@link Instant} when this loan is due
     */
    @NotNull
    @Contract("-> !null")
    public Instant getDueDate() {
        // Extra day is added because payment is not due on day 1
        return this.initiation.plus(Duration.of(this.totalPeriods + 1, this.periodUnit));
    }

    /**
     * @return The number of payments before the due date
     */
    public long getRemainingPayments() {
        return this.totalPeriods - this.currentPeriod;
    }

    /**
     * @return The total amount with interest and fees
     */
    @NotNull
    @Contract("-> !null")
    public BigDecimal getTotalAmount() {
        return this.loanAmount.add(getFeeTotal());
    }

    /**
     * @return The amount remaining to be paid
     */
    @NotNull
    @Contract("-> !null")
    public BigDecimal getRemainingAmount() {
        return getTotalAmount().subtract(getPaymentTotal());
    }

    /**
     * @return The total amount of all payments
     */
    @NotNull
    @Contract("-> !null")
    public BigDecimal getPaymentTotal() {
        return this.payments.stream().map(p -> p.amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @return The total amount of all fees
     */
    @NotNull
    @Contract("-> !null")
    public BigDecimal getFeeTotal() {
        return this.fees.stream().map(f -> f.amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @return The current amount per installment to pay the loan off before the due date
     */
    @NotNull
    @Contract("-> !null")
    public BigDecimal getInstallmentAmount() {
        if (getRemainingPayments() <= 0) {
            return getRemainingAmount();
        } else {
            return getRemainingAmount().divide(BigDecimal.valueOf(getRemainingPayments()), RoundingMode.DOWN);
        }
    }

    /**
     * Make a payment on this loan
     *
     * @param maximum The largest payment that could be made
     * @return The payment data, or null if no payment was made
     */
    @Nullable
    @Contract("_ -> _")
    public Payment makePayment(double maximum) {
        // the amount that still needs to be paid
        BigDecimal requiredAmount = getInstallmentAmount();
        // no payment required is there is no balance
        if (requiredAmount.compareTo(BigDecimal.ZERO) <= 0) return null;
        // calculate the withdrawal
        BigDecimal actualAmount = requiredAmount.min(BigDecimal.valueOf(maximum));
        // only positive amounts can be made as payments
        if (actualAmount.compareTo(BigDecimal.ZERO) > 0) {
            Payment payment = new Payment(actualAmount, requiredAmount.subtract(actualAmount));
            this.payments.add(payment);
            return payment;
        } else {
            return null;
        }
    }
}
