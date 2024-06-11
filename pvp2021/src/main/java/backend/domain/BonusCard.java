package backend.domain;

import backend.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.springframework.core.style.ToStringCreator;

//Class representing BonusCard objects
public class BonusCard {
    private final String number;
    private final String goodThruMonth;
    private final String goodThruYear;
    private final boolean blocked;
    private final boolean expired;
    private final String holderName;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private BonusCard(
                       @JsonProperty( "number" ) String number,
                       @JsonProperty( "goodThruMonth" ) String goodThruMonth,
                       @JsonProperty( "goodThruYear" ) String goodThruYear,
                       @JsonProperty( "blocked" ) boolean blocked,
                       @JsonProperty( "expired" ) boolean expired,
                       @JsonProperty( "holderName" ) String holderName ) {

        this.number = number;
        this.goodThruMonth = goodThruMonth;
        this.goodThruYear = goodThruYear;
        this.blocked = blocked;
        this.expired = expired;
        this.holderName = holderName;
    }

    public static BonusCard of( String number, String goodThruMonth, String goodThruYear, boolean blocked, boolean expired, String holderName ) {
        return new BonusCard( number, goodThruMonth, goodThruYear, blocked, expired, holderName );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BonusCard bonusCard = (BonusCard) o;
        return blocked == bonusCard.blocked && expired == bonusCard.expired && Objects.equal(number, bonusCard.number) && Objects.equal(goodThruMonth, bonusCard.goodThruMonth) && Objects.equal(goodThruYear, bonusCard.goodThruYear) && Objects.equal(holderName, bonusCard.holderName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( number, goodThruMonth, goodThruYear, blocked, expired, holderName);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append( "number", number )
                .append( "goodThruMonth", goodThruMonth )
                .append( "goodThruYear", goodThruMonth )
                .append( "blocked", blocked )
                .append( "expired", expired )
                .append( "holderName", holderName )
                .toString();
    }

    public boolean isValid() {
        return StringUtils.valid( number ) && ( !expired ) && ( !blocked );
    }

    public String getNumber() {
        return number;
    }

    public String getGoodThruMonth() {
        return goodThruMonth;
    }

    public String getGoodThruYear() {
        return goodThruYear;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isExpired() {
        return expired;
    }

    public String getHolderName() {
        return holderName;
    }
}
