package backend.domain.states;

public enum PaymentCardType {

    CREDIT( "CREDIT" ),
    DEBIT( "DEBIT" ),
    UNKNOWN( "" );

    private final String value;

    PaymentCardType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentCardType of(String value ) {
        for (PaymentCardType paymentCardType : values()) {
            if ( paymentCardType.value.equals(value) )
                return paymentCardType;
        }
        return null;
    }
}
