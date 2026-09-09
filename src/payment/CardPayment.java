package payment;

/**
 * Represents a card payment method (debit / credit card).
 * <p>This class demonstrates polymorphism by implementing the
 * {@link Payment} interface. The card is considered "declined"
 * when the amount is invalid, otherwise the payment is approved.</p>
 */
public class CardPayment implements Payment {

    private final String cardNumber;
    private final String cardHolderName;

    /**
     * Creates a card payment with the given card details.
     *
     * @param cardNumber     the card number (e.g. 4111 1111 1111 1111)
     * @param cardHolderName the name printed on the card
     */
    public CardPayment(String cardNumber, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }

    /**
     * Simulates processing a card transaction.
     * A positive amount is always approved in this simulation.
     *
     * @param amount the amount to pay in RM
     * @return true if the card payment was approved, false otherwise
     */
    @Override
    public boolean pay(double amount) {
        if (amount <= 0) {
            System.out.println("[Card Payment] Invalid amount. Transaction declined.");
            return false;
        }
        // In a real system the card would be validated against a bank gateway.
        System.out.println("[Card Payment] Card ending with "
                + getLastFourDigits() + " charged RM " + String.format("%.2f", amount));
        return true;
    }

    /**
     * @return false because a card payment is charged to the card and does
     *         not deduct the e-wallet balance
     */
    @Override
    public boolean deductsWalletBalance() {
        return false;
    }

    /**
     * @return "Card Payment" as the method description
     */
    @Override
    public String getPaymentMethod() {
        return "Card Payment";
    }

    /**
     * Returns the last four digits of the card number, used for display.
     *
     * @return the last 4 digits, or "0000" if the number is malformed
     */
    private String getLastFourDigits() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "0000";
        }
        String digits = cardNumber.replaceAll("\\s+", "");
        return digits.substring(Math.max(0, digits.length() - 4));
    }

    /**
     * @return the card holder name
     */
    public String getCardHolderName() {
        return cardHolderName;
    }
}
