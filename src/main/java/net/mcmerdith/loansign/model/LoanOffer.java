package net.mcmerdith.loansign.model;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public class LoanOffer {
    public Loan loan;
    public Instant expiry;

    public LoanOffer(@NotNull Loan loan, @NotNull Instant expiry) {
        this.loan = loan;
        this.expiry = expiry;
    }

    public LoanOffer(@NotNull Loan loan) {
        this(loan, Instant.now().plus(Duration.ofMinutes(5)));
    }
}
