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
     * Returns a description of the payment method.
     *
     * @return payment method description (e.g. "Cash Payment")
     */
    String getPaymentMethod();
}
