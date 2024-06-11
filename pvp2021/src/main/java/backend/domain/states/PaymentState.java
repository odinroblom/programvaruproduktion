package backend.domain.states;

public enum PaymentState {

    ACCEPTED( "ACCEPTED" ),
    INSUFFICIENT_FUNDS( "INSUFFICIENT_FUNDS" ),
    UNSUPPORTED_CARDS( "UNSUPPORTED_CARD" ),
    INVALID_PIN( "INVALID_PIN" ),
    NETWORK_ERROR( "NETWORK_ERROR" ),
    TIME_OUT( "TIME_OUT" ),
    UNKNOWN( "" );

    private final String value;

    PaymentState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean matches( String text ) {
        return this.value.equals( text );
    }
}
