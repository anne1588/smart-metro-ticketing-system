package service;

import enums.TicketStatus;
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
     * <p>On success, the passenger's e-wallet is charged the ticket fare
     * and the ticket status is updated to {@code USED}.</p>
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
        double amount = ticket.getFare();
        System.out.println("\n--- Payment via " + payment.getPaymentMethod() + " ---");
        System.out.println("Ticket ID : " + ticket.getTicketId());
        System.out.println("Amount    : RM " + String.format("%.2f", amount));

        // 1. Check whether the passenger has enough balance.
        if (passenger.getBalance() < amount) {
            System.out.println("[Payment] FAILED: Insufficient balance. "
                    + "Current balance: RM " + String.format("%.2f", passenger.getBalance()));
            return false;
        }

        // 2. Delegate the actual transaction to the payment method (polymorphism).
        if (!payment.pay(amount)) {
            System.out.println("[Payment] FAILED: Transaction was declined.");
            return false;
        }

        // 3. Deduct the amount from the passenger's e-wallet.
        passenger.deduct(amount);

        // 4. Mark the ticket as USED.
        ticket.setStatus(TicketStatus.USED);

        System.out.println("[Payment] SUCCESS: RM " + String.format("%.2f", amount)
                + " paid via " + payment.getPaymentMethod() + ".");
        System.out.println("New balance: RM " + String.format("%.2f", passenger.getBalance()));
        return true;
    }
}
