package payment;

import java.math.BigDecimal;

/**
 * Represents a cash payment method.
 * <p>This class demonstrates polymorphism by implementing the
 * {@link Payment} interface. A cash payment is always accepted
 * (simulated in this console application).</p>
 */
public class CashPayment implements Payment {

    /**
     * Processes a cash payment. In this simulation the payment is
     * always successful once the cash tender is provided.
     *
     * @param amount the amount to pay in RM
     * @return true (cash payment always accepted in simulation)
     */
    @Override
    public boolean pay(BigDecimal amount) {
        return amount != null && amount.signum() > 0;
    }

    /**
     * @return true because a cash purchase is paid from the e-wallet balance
     */
    @Override
    public boolean deductsWalletBalance() {
        return true;
    }

    /**
     * @return "Cash Payment" as the method description
     */
    @Override
    public String getPaymentMethod() {
        return "Cash Payment";
    }
}
