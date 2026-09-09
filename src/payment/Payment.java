package payment;

/**
 * Interface that defines a common contract for every payment method.
 * <p>Both {@link CashPayment} and {@link CardPayment} implement this
 * interface, which allows polymorphic code like:
 * {@code Payment payment = new CashPayment();}</p>
 */
public interface Payment {

    /**
     * Processes a payment for the given amount.
     *
     * @param amount the amount to pay in RM
     * @return true if the payment was successful, false otherwise
     */
    boolean pay(double amount);

    /**
     * Indicates whether this payment method is settled from the passenger's
     * e-wallet balance. Cash payments deduct the balance, while card
     * payments charge the card directly and leave the balance untouched.
     *
     * @return true if the passenger's e-wallet balance must be deducted
     */
    boolean deductsWalletBalance();

    /**
     * Returns a description of the payment method.
     *
     * @return payment method description (e.g. "Cash Payment")
     */
    String getPaymentMethod();
}
