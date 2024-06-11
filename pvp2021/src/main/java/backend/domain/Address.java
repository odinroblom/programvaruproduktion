package backend.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

//Class representing the Address objects for Bonuscard holders.
public class Address {
    private final String streetAddress;
    private final String postalCode;
    private final String postOffice;
    private final String country;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private Address( @JsonProperty( "streetAddress" ) String streetAddress,
                     @JsonProperty( "postalCode" ) String postalCode,
                     @JsonProperty( "postOffice" ) String postOffice,
                     @JsonProperty( "country" ) String country ) {

        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.postOffice = postOffice;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equal(streetAddress, address.streetAddress) && Objects.equal(postalCode, address.postalCode) && Objects.equal(postOffice, address.postOffice) && Objects.equal(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(streetAddress, postalCode, postOffice, country);
    }

    public static Address of(String streetAddress, String postalCode, String postalOffice, String country ) {
        return new Address( streetAddress, postalCode, postalOffice, country );
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public String getCountry() {
        return country;
    }
}
