package backend.domain;

import backend.managers.CustomerRegisterManager;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import static backend.utils.StringUtils.valid;

/**
 * Class representing a Payment.
 * This includes information about the type of card used in a transaction.
 */
public class Payment {

    private final String bonusCardNumber;
    private final String bonusState;
    private final String paymentCardNumber;
    private final String goodThruMonth;
    private final String goodThruYear;
    private final String paymentState;
    private final String paymentCardType;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private Payment( @JsonProperty( "bonusCardNumber" ) String bonusCardNumber,
                     @JsonProperty( "bonusState" ) String bonusState,
                     @JsonProperty( "paymentCardNumber" ) String paymentCardNumber,
                     @JsonProperty( "goodThruMonth" ) String goodThruMonth,
                     @JsonProperty( "goodThruYear" ) String goodThruYear,
                     @JsonProperty( "paymentState" ) String paymentState,
                     @JsonProperty( "paymentCardType" ) String paymentCardType ) {

        this.bonusCardNumber = bonusCardNumber;
        this.bonusState = bonusState;
        this.paymentCardNumber = paymentCardNumber;
        this.goodThruMonth = goodThruMonth;
        this.goodThruYear = goodThruYear;
        this.paymentState = paymentState;
        this.paymentCardType = paymentCardType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equal(bonusCardNumber, payment.bonusCardNumber) &&
                Objects.equal(bonusState, payment.bonusState) &&
                Objects.equal(paymentCardNumber, payment.paymentCardNumber) &&
                Objects.equal(goodThruMonth, payment.goodThruMonth) &&
                Objects.equal(goodThruYear, payment.goodThruYear) &&
                Objects.equal(paymentState, payment.paymentState) &&
                Objects.equal(paymentCardType, payment.paymentCardType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bonusCardNumber, bonusState, paymentCardNumber, goodThruMonth, goodThruYear, paymentState, paymentCardType);
    }

    public static Payment of(String bonusCardNumber, String bonusState, String paymentCardNumber, String goodThruMonth, String goodThruYear, String paymentState, String paymentCardType ) {
        return new Payment( bonusCardNumber, bonusState, paymentCardNumber, goodThruMonth, goodThruYear, paymentState, paymentCardType );
    }

    public Customer getCustomer( CustomerRegisterManager customerRegisterManager ) {
        if ( valid( getBonusCardNumber() ) && valid( getGoodThruMonth() ) && valid( getGoodThruYear() ) ) {
            try {
                return customerRegisterManager.findByCard( getBonusCardNumber(), getGoodThruYear(), getGoodThruMonth() );
            } catch (Exception ignored) {}
        }
        return null;
    }

    public String getBonusCardNumber() {
        return bonusCardNumber;
    }

    public String getBonusState() {
        return bonusState;
    }

    public String getPaymentCardNumber() {
        return paymentCardNumber;
    }

    public String getGoodThruMonth() {
        return goodThruMonth;
    }

    public String getGoodThruYear() {
        return goodThruYear;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public String getPaymentCardType() {
        return paymentCardType;
    }
}
