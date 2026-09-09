package service;

import java.math.BigDecimal;

import model.Passenger;
import model.Ticket;
import payment.Payment;

/**
 * Processes payments for booked tickets.
 * <p>
 * The payment method is injected as a {@link Payment} reference, which
 * demonstrates polymorphism: the same service works for both
 * {@link payment.CashPayment} and {@link payment.CardPayment}.
 * </p>
 */
public class PaymentService {

    /**
     * Processes the payment for a ticket using the given payment method.
     * <p>Cash payments are settled from the passenger's e-wallet (the fare is
     * deducted and sufficient balance is required). Card payments charge the
     * card directly and leave the e-wallet balance untouched.</p>
     *
     * @param payment   the payment method (Cash or Card)
     * @param passenger the passenger paying for the ticket
     * @param ticket    the ticket being paid for
     * @return true if the payment was successful, false otherwise
     */
    public boolean processPayment(Payment payment, Passenger passenger, Ticket ticket) {
        if (payment == null || passenger == null || ticket == null) {
            System.out.println("[Payment] Invalid payment request.");
            return false;
        }
        BigDecimal amount = scaleMoney(ticket.getFare());
        System.out.println("\n--- Payment via " + payment.getPaymentMethod() + " ---");
        System.out.println("Ticket ID : " + ticket.getTicketId());
        System.out.println("Amount    : RM " + formatMoney(amount));

        // 1. Cash is paid from the e-wallet, so check the balance first.
        //    Card payments are charged to the card and do not need a balance.
        if (payment.deductsWalletBalance()
                && passenger.getBalance().compareTo(amount) < 0) {
            System.out.println("[Payment] FAILED: Insufficient balance. "
                    + "Current balance: RM " + formatMoney(passenger.getBalance()));
            return false;
        }

        // 2. Delegate the actual transaction to the payment method (polymorphism).
        if (!payment.pay(amount)) {
            System.out.println("[Payment] FAILED: Transaction was declined.");
            return false;
        }

        // 3. Deduct the amount from the passenger's e-wallet only for cash.
        if (payment.deductsWalletBalance()) {
            passenger.deduct(amount);
        }

        System.out.println("[Payment] SUCCESS: RM " + formatMoney(amount)
                + " paid via " + payment.getPaymentMethod() + ".");
        if (payment.deductsWalletBalance()) {
            System.out.println("New balance: RM " + formatMoney(passenger.getBalance()));
        }
        return true;
    }

    /**
     * Rounds a money value to 2 decimal places (half-up).
     *
     * @param value the raw money value
     * @return the scaled value
     */
    private static BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Formats a money value as a plain string with 2 decimal places.
     *
     * @param value the money value
     * @return e.g. "12.50"
     */
    private static String formatMoney(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
