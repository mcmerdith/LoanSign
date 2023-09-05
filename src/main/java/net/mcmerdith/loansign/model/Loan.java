package net.mcmerdith.loansign.model;

import net.mcmerdith.loansign.LoanSignMain;
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
import java.util.Objects;
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
    protected BigDecimal loanAmount;

    /**
     * The initiation date of the loan
     */
    protected Instant initiation;

    /**
     * The current period the loan is on
     */
    protected int currentPeriod;

    /**
     * The number of periods the loan lasts
     */
    protected int totalPeriods;

    /**
     * The time unit represented by each period
     */
    protected ChronoUnit periodUnit;

    /**
     * The list of payments made on this loan
     */
    protected List<Payment> payments;

    /**
     * The list of fees on this loan
     * <p>Additional fees may be stored separate from this list. Use {@link Loan#getFees()}
     * to get a list of all fees</p>
     *
     * @see Loan#getFees()
     */
    protected List<Fee> fees;

    /**
     * Gson Constructor: Do not use
     */
    protected Loan() {
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
     * @return The period that this loan should be on based on the current date
     * @apiNote May be greater than {@link Loan#totalPeriods}
     */
    public int getExpectedCurrentPeriod() {
        return (int) this.initiation.until(Instant.now(), this.periodUnit);
    }

    /**
     * @return The number of payments before the due date
     * @apiNote This value is never negative (0 < n <= {@link Loan#totalPeriods})
     */
    public int getRemainingPeriods() {
        return Math.max(this.totalPeriods - this.currentPeriod, 0);
    }

    /**
     * @return If a payment is due on this loan
     */
    public boolean isPaymentDue() {
        return this.currentPeriod < getExpectedCurrentPeriod();
    }

    /**
     * @return If there is no remaining balance on this loan
     */
    public boolean isPaidOff() {
        return this.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * @return The number of payments required to make this loan current
     * @apiNote This value is designed to be used as a multiplier for {@link Loan#getInstallmentAmount()}
     */
    public int getRequiredPayments() {
        // No balance, no payment
        if (this.isPaidOff()) return 0;
        // required payments to be current
        int requiredPayments = getExpectedCurrentPeriod() - currentPeriod;
        // if current or ahead no payments required
        if (requiredPayments <= 0) return 0;
        // return the number of payments required
        // max: remaining payments, min: 1
        return Math.max(1, Math.min(requiredPayments, getRemainingPeriods()));
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
     * @apiNote This value is never negative
     */
    @NotNull
    @Contract("-> !null")
    public BigDecimal getRemainingAmount() {
        return getTotalAmount().subtract(getPaymentTotal()).max(BigDecimal.ZERO);
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
        return getFees().stream().map(f -> f.amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @return All fees on this loan and associated payments
     */
    public List<Fee> getFees() {
        List<Fee> allFees = new ArrayList<>(fees);
        allFees.addAll(payments.stream().map(p -> p.fee).filter(Objects::nonNull).toList());
        return allFees;
    }

    /**
     * @return The current amount per installment to pay the loan off before the due date
     * @apiNote This value is never negative
     */
    @NotNull
    @Contract("-> !null")
    public BigDecimal getInstallmentAmount() {
        if (this.isPaidOff()) return BigDecimal.ZERO;

        BigDecimal remainingAmount = getRemainingAmount();
        int remainingPayments = getRemainingPeriods();

        if (remainingPayments <= 1) {
            return remainingAmount;
        } else {
            return remainingAmount.divide(BigDecimal.valueOf(remainingPayments), RoundingMode.DOWN);
        }
    }

    /**
     * Make a payment on this loan
     * <p>This method does not apply fees for insufficient payments.
     * Use {@link Loan#attemptPayment(double, double)} to auto-apply fees</p>
     *
     * @param maximum The largest payment that could be made
     * @return The payment data, or null if no payment was made
     * @see Loan#attemptPayment(double, double)
     */
    @Nullable
    @Contract("_ -> _")
    public Payment makePayment(double maximum) {
        // No balance, no payment
        if (this.isPaidOff()) return null;
        // the amount that still needs to be paid
        BigDecimal installmentAmount = getInstallmentAmount();
        // get the number of payments that must be made to become current
        int requiredPayments = getRequiredPayments();
        // calculate the required amount
        BigDecimal requiredAmount = installmentAmount.multiply(BigDecimal.valueOf(requiredPayments));
        // no payment required is there is no balance
        if (requiredAmount.compareTo(BigDecimal.ZERO) <= 0 || maximum < 0) return null;
        // calculate the withdrawal
        BigDecimal actualAmount = requiredAmount.min(BigDecimal.valueOf(maximum));
        // create and return the payment
        Payment payment = new Payment(actualAmount, requiredAmount.subtract(actualAmount));
        this.payments.add(payment);
        this.currentPeriod = Math.min(this.currentPeriod + requiredPayments, this.totalPeriods);
        return payment;
    }

    /**
     * Make a payment on this loan
     *
     * @param maximum The largest payment that could be made
     * @return The payment data, or null if no payment was made
     */
    @Nullable
    @Contract("_, _ -> _")
    public Payment attemptPayment(double maximum, double maxFee) {
        Payment payment = makePayment(maximum);
        if (payment == null) return null;
        if (payment.deficit.compareTo(BigDecimal.ZERO) > 0) {
            payment.setFee(new Fee(
                    payment.deficit
                            .divide(payment.getTotal(), RoundingMode.DOWN)
                            .multiply(BigDecimal.valueOf(maxFee)),
                    "Insufficient Payment",
                    String.format(
                            "%s / %s (%s short)",
                            LoanSignMain.economy.format(payment.getAmount()),
                            LoanSignMain.economy.format(payment.getTotal_d()),
                            LoanSignMain.economy.format(payment.getDeficit())
                    )));
        }
        return payment;
    }
}

