package backend.domain.states;

public enum CardReaderState {

    IDLE( "IDLE" ),
    WAITING_FOR_PAYMENT( "WAITING_FOR_PAYMENT" ),
    DONE( "DONE" ),
    UNKNOWN( "" );

    private final String value;

    CardReaderState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CardReaderState of(String value ) {
        for (CardReaderState cardReaderState : values()) {
            if ( cardReaderState.value.equals(value) )
                return cardReaderState;
        }
        return null;
    }

    public boolean matches( String text ) {
        return this.value.equals( text );
    }
}
