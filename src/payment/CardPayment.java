package payment;

import java.math.BigDecimal;

/**
 * Represents a card payment method (debit / credit card).
 * <p>This class demonstrates polymorphism by implementing the
 * {@link Payment} interface. A card payment is approved only when the
 * card details are valid (numeric number of an acceptable length and a
 * non-empty cardholder name) and the amount is positive.</p>
 */
public class CardPayment implements Payment {

    private final String cardNumber;
    private final String cardHolderName;

    /** Minimum number of digits a card number must contain. */
    private static final int MIN_DIGITS = 13;
    /** Maximum number of digits a card number may contain. */
    private static final int MAX_DIGITS = 19;

    /**
     * Creates a card payment with the given card details.
     *
     * @param cardNumber     the card number (e.g. 4111 1111 1111 1111)
     * @param cardHolderName the name printed on the card
     */
    public CardPayment(String cardNumber, String cardHolderName) {
        this.cardNumber = cardNumber == null ? "" : cardNumber.trim();
        this.cardHolderName = cardHolderName == null ? "" : cardHolderName.trim();
    }

    /**
     * Simulates processing a card transaction.
     * The transaction is declined if the amount is invalid or the card
     * details do not pass validation.
     *
     * @param amount the amount to pay in RM
     * @return true if the card payment was approved, false otherwise
     */
    @Override
    public boolean pay(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            System.out.println("[Card Payment] Invalid amount. Transaction declined.");
            return false;
        }
        if (!hasValidCardDetails()) {
            System.out.println("[Card Payment] Invalid card details. Transaction declined.");
            return false;
        }
        // In a real system the card would be validated against a bank gateway.
        System.out.println("[Card Payment] Card ending with "
                + getLastFourDigits() + " charged RM " + amount.setScale(2).toPlainString());
        return true;
    }

    /**
     * Validates the card number (numeric, acceptable length) and holder name.
     *
     * @return true if the card details are valid
     */
    private boolean hasValidCardDetails() {
        if (cardHolderName.isEmpty()) {
            return false;
        }
        String digits = cardNumber.replaceAll("\\s+", "");
        if (digits.isEmpty() || !digits.matches("\\d+")) {
            return false;
        }
        return digits.length() >= MIN_DIGITS && digits.length() <= MAX_DIGITS;
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
        String digits = cardNumber.replaceAll("\\s+", "");
        if (digits.isEmpty()) {
            return "0000";
        }
        return digits.substring(Math.max(0, digits.length() - 4));
    }

    /**
     * @return the card holder name
     */
    public String getCardHolderName() {
        return cardHolderName;
    }
}
