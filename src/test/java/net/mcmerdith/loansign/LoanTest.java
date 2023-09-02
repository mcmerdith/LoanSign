package net.mcmerdith.loansign;

import net.mcmerdith.loansign.model.Loan;
import net.mcmerdith.loansign.model.Payment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
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

    static final double TEST_RATE = 0.05;
    static final int TEST_DURATION_DAYS = 20;
    static final double[] TEST_INITIAL_AMOUNTS = {15.0, 150.0, 1500.0, 15000.0, 150000.0};
    static final double[] TEST_TOTAL_AMOUNTS = {39.7994655771663, 397.994655771663, 3979.94655771663, 39799.4655771663, 397994.655771663};
    static final double[] TEST_INSTALLMENT_AMOUNTS = {1.98997327885832, 19.8997327885832, 198.997327885832, 1989.97327885832, 19899.7327885832};

    static final BigDecimal TWO = BigDecimal.valueOf(2.0);

    @Test
    public void testLoan() {
        for (int i = 0; i < TEST_INITIAL_AMOUNTS.length; ++i) {
            Loan loan = new Loan(UUID.randomUUID(), UUID.randomUUID(), TEST_INITIAL_AMOUNTS[i], TEST_RATE, TEST_DURATION_DAYS);

            // test time calculations
            assertEquals(loan.initiation.plus(Duration.ofDays(TEST_DURATION_DAYS + 1)), loan.getDueDate(), "Incorrect due date calculated");
            assertEquals(TEST_DURATION_DAYS, loan.getRemainingPayments(), "Incorrect remaining payments calculated");

            // test calculation of total amount
            BigDecimal totalAmount = loan.getTotalAmount();
            assertBigDecimalEquals(BigDecimal.valueOf(TEST_TOTAL_AMOUNTS[i]), totalAmount, 4, "Incorrect total amount calculated");

            // test the installment amount calculations
            BigDecimal installmentAmount = loan.getInstallmentAmount();
            assertBigDecimalEquals(BigDecimal.valueOf(TEST_INSTALLMENT_AMOUNTS[i]), installmentAmount, 4, "Incorrect installment amount calculated");

            // make a set of payments on the loan
            for (; loan.currentPeriod < loan.totalPeriods; loan.currentPeriod++) {
                // make a payment with more than enough funds
                Payment payment = loan.makePayment(loan.getTotalAmount().doubleValue());
                // payment should have gone through
                assertNotNull(payment, "Payment was rejected when it should not be");
                // payment should only be the installment amount
                assertBigDecimalEquals(installmentAmount, payment.amount, 4, "Payment was for an incorrect amount");
                // there should be no deficit
                assertBigDecimalEquals(BigDecimal.ZERO, payment.deficit, 4, "Payment should not have a deficit");
                // the number of payments should equal the period after this loop
                assertEquals(loan.currentPeriod + 1, loan.payments.size(), "Payment count is incorrect");
            }

            // there should be no more payments
            assertEquals(0, loan.getRemainingPayments(), "Loan should not have any payments remaining");
            // there should be no more balance
            assertBigDecimalEquals(BigDecimal.ZERO, loan.getRemainingAmount(), 4, "Loan should not have a balance remaining");
            // making another payment should fail
            assertNull(loan.makePayment(loan.getTotalAmount().doubleValue()), "Payment was accepted when it should not be");
            // making another payment shouldn't add another record (even a 0.0)
            assertEquals(loan.totalPeriods, loan.payments.size(), "Rejected payments should not add a payment");

            // reset
            loan = new Loan(UUID.randomUUID(), UUID.randomUUID(), TEST_INITIAL_AMOUNTS[i], TEST_RATE, TEST_DURATION_DAYS);

            // make a short payment every time
            for (; loan.currentPeriod < loan.totalPeriods; loan.currentPeriod++) {
                // make a payment that's half the required
                BigDecimal currentHalfPayment = loan.getInstallmentAmount().divide(TWO, RoundingMode.DOWN);
                Payment payment = loan.makePayment(currentHalfPayment.doubleValue());
                // payment should have gone through
                assertNotNull(payment, "Payment was rejected when it should not be");
                // payment should only be the half payment
                assertBigDecimalEquals(currentHalfPayment, payment.amount, 4, "Payment was for an incorrect amount");
                // deficit should be the remaining half payment
                assertBigDecimalEquals(currentHalfPayment, payment.deficit, 4, "Deficit was an incorrect amount");
                // the number of payments should equal the period after this loop
                assertEquals(loan.currentPeriod + 1, loan.payments.size(), "Payment count is incorrect");
            }

            // there should be no more payments
            assertEquals(0, loan.getRemainingPayments(), "Loan should not have any more payments remaining");
            // get the remaining balance
            BigDecimal remainingAmount = loan.getRemainingAmount();
            // any further installments should be for the full amount
            assertBigDecimalEquals(remainingAmount, loan.getInstallmentAmount(), 4, "Installments after no more payments should be for the full amount");

            // make a payment for the remainder of the loan
            Payment payment = loan.makePayment(totalAmount.doubleValue());
            // payment should be successful
            assertNotNull(payment, "Payment failed when it should not have");
            // payment should be for the remaining amount
            assertBigDecimalEquals(remainingAmount, payment.amount, 4, "Payment was for an incorrect amount");
            // there should be no deficit
            assertBigDecimalEquals(BigDecimal.ZERO, payment.deficit, 4, "Payment should not have a deficit");

            // there should be no more balance
            assertBigDecimalEquals(BigDecimal.ZERO, loan.getRemainingAmount(), 4, "Loan should not have a balance remaining");
        }
    }
}
