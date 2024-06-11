package backend.domain.states;

public enum BonusState {

    ACCEPTED( "ACCEPTED" ),
    UNSUPPORTED_CARD( "UNSUPPORTED_CARD" ),
    BLOCKED_CARD( "BLOCKED_BONUS_CARD" ),
    EXPIRED_CARD( "EXPIRED_BONUS_CARD" ),
    UNKNOWN( "" );

    private final String value;

    BonusState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean matches( String text ) {
        return this.value.equals( text );
    }
}
