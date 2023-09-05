package net.mcmerdith.loansign.model;

import net.mcmerdith.loansign.LoanSignMain;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {
    public static void assertBigDecimalEquals(BigDecimal a, BigDecimal b, int scale, String message) {
        BigDecimal scaledA = a.setScale(scale, RoundingMode.HALF_UP);
        BigDecimal scaledB = b.setScale(scale, RoundingMode.HALF_UP);
        assertEquals(scaledA, scaledB, message);
    }

    public static void assertBigDecimalEquals(BigDecimal a, BigDecimal b, int scale) {
        assertBigDecimalEquals(a, b, scale, null);
    }

    static final int TEST_PRECISION = 4;
    static final double TEST_RATE = 0.05;
    static final double TEST_FEE = 25.0;
    static final int TEST_DURATION_DAYS = 20;
    static final double[] TEST_INITIAL_AMOUNTS = {15.0, 150.0, 1500.0, 15000.0, 150000.0};
    static final double[] TEST_TOTAL_AMOUNTS = {39.7994655771663, 397.994655771663, 3979.94655771663, 39799.4655771663, 397994.655771663};
    static final double[] TEST_INSTALLMENT_AMOUNTS = {1.98997327885832, 19.8997327885832, 198.997327885832, 1989.97327885832, 19899.7327885832};

    static Loan testLoan(int dataIdx) {
        return new Loan(UUID.randomUUID(), UUID.randomUUID(), TEST_INITIAL_AMOUNTS[dataIdx], TEST_RATE, TEST_DURATION_DAYS);
    }

    static final BigDecimal TWO = BigDecimal.valueOf(2.0);

    static {
        LoanSignMain.economy = new FakeEconomy();
    }

    @Test
    public void testLoan() {
        for (int i = 0; i < TEST_INITIAL_AMOUNTS.length; ++i) {
            Loan loan = testLoan(i);

            // test time calculations
            assertEquals(loan.initiation.plus(Duration.ofDays(TEST_DURATION_DAYS + 1)), loan.getDueDate(), "Incorrect due date calculated");
            assertEquals(TEST_DURATION_DAYS, loan.getRemainingPeriods(), "Incorrect remaining payments calculated");

            // test period calculations
            assertEquals(0, loan.getExpectedCurrentPeriod(), "Loan should have an expected period of 0 on initiation");
            assertFalse(loan.isPaymentDue(), "Loan should not have a payment due on initiation");

            // test calculation of total amount
            assertBigDecimalEquals(BigDecimal.valueOf(TEST_TOTAL_AMOUNTS[i]), loan.getTotalAmount(), TEST_PRECISION, "Incorrect total amount calculated");

            // test the installment amount calculations
            BigDecimal installmentAmount = loan.getInstallmentAmount();
            assertBigDecimalEquals(BigDecimal.valueOf(TEST_INSTALLMENT_AMOUNTS[i]), installmentAmount, TEST_PRECISION, "Incorrect installment amount calculated");

            // make a set of payments on the loan
            for (int paymentId = 1; !loan.isPaidOff(); ++paymentId) {
                // pretend like time is passing
                loan.initiation = loan.initiation.minus(1, loan.periodUnit);
                // payment should be the same as the expected period
                assertEquals(paymentId, loan.getExpectedCurrentPeriod(), "Payment # should match the expected period");
                // 1 payment should be required
                assertEquals(1, loan.getRequiredPayments(), "One payment should be required per time unit");
                // make a payment with more than enough funds
                Payment payment = loan.attemptPayment(loan.getTotalAmount().doubleValue(), TEST_FEE);
                // payment should have gone through
                assertNotNull(payment, "Payment was rejected when it should not be");
                // payment should only be the installment amount
                assertBigDecimalEquals(installmentAmount, payment.amount, TEST_PRECISION, "Payment was for an incorrect amount");
                // there should be no deficit
                assertBigDecimalEquals(BigDecimal.ZERO, payment.deficit, TEST_PRECISION, "Payment should not have a deficit");
                // the number of payments should equal the period after this loop
                assertEquals(loan.currentPeriod, loan.payments.size(), "Payment count is incorrect");
            }

            // there should be no more payments
            assertEquals(0, loan.getRemainingPeriods(), "Loan should not have any payments remaining");
            // there should be no more balance
            assertTrue(loan.isPaidOff(), "Loan should not have a balance remaining");
            // making another payment should fail
            assertNull(loan.attemptPayment(loan.getTotalAmount().doubleValue(), TEST_FEE), "Payment was accepted when it should not be");
            // making another payment shouldn't add another record (even a 0.0)
            assertEquals(loan.totalPeriods, loan.payments.size(), "Rejected payments should not add a payment");

            // reset
            loan = testLoan(i);

            // make a short payment for each period
            for (int paymentId = 1; paymentId <= loan.totalPeriods; ++paymentId) {
                // make a payment that's half the required
                BigDecimal currentHalfPayment = loan.getInstallmentAmount().divide(TWO, RoundingMode.DOWN);
                // pretend like time is passing
                loan.initiation = loan.initiation.minus(1, loan.periodUnit);
                // payment should be the same as the expected period
                assertEquals(paymentId, loan.getExpectedCurrentPeriod(), "Payment # should match the expected period");
                // 1 payment should be required
                assertEquals(1, loan.getRequiredPayments(), "One payment should be required per time unit");
                // make a payment with more than enough funds
                Payment payment = loan.attemptPayment(currentHalfPayment.doubleValue(), TEST_FEE);
                // payment should have gone through
                assertNotNull(payment, "Payment was rejected when it should not be");
                // payment should only be the half payment
                assertBigDecimalEquals(currentHalfPayment, payment.amount, TEST_PRECISION, "Payment was for an incorrect amount");
                // deficit should be the remaining half payment
                assertBigDecimalEquals(currentHalfPayment, payment.deficit, TEST_PRECISION, "Deficit was an incorrect amount");
                // the number of payments should equal the period after this loop
                assertEquals(loan.currentPeriod, loan.payments.size(), "Payment count is incorrect");
            }

            // there should be no more payments
            assertEquals(0, loan.getRemainingPeriods(), "Loan should have no periods remaining");
            assertEquals(0, loan.getRequiredPayments(), "Loan should have no payments remaining");

            // pretend like time is passing
            loan.initiation = loan.initiation.minus(2, loan.periodUnit);

            assertEquals(1, loan.getRequiredPayments(), "Loan should have 1 payment remaining");
            // get the remaining balance
            BigDecimal remainingAmount = loan.getRemainingAmount();
            // any further installments should be for the full amount
            assertBigDecimalEquals(remainingAmount, loan.getInstallmentAmount(), TEST_PRECISION, "Installments after no more payments should be for the full amount");

            // make a payment for the remainder of the loan
            Payment payment = loan.attemptPayment(remainingAmount.add(BigDecimal.ONE).doubleValue(), TEST_FEE);
            // payment should be successful
            assertNotNull(payment, "Payment failed when it should not have");
            // payment should be for the remaining amount
            assertBigDecimalEquals(remainingAmount, payment.amount, TEST_PRECISION, "Payment was for an incorrect amount");
            // there should be no deficit
            assertBigDecimalEquals(BigDecimal.ZERO, payment.deficit, TEST_PRECISION, "Payment should not have a deficit");

            // there should be no more balance
            assertTrue(loan.isPaidOff(), "Loan should not have a balance remaining");
        }
    }
}
