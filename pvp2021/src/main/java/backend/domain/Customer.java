package backend.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.Date;
import java.util.List;

/**
 * Class representing the Customer.
 * Includes information such as their names, birthDate, address, bonusCards and sex.
 */
public class Customer {

    public static final String SEX_ANY = "ANY";
    public static final String SEX_MALE = "MALE";
    public static final String SEX_FEMALE = "FEMALE";

    private final String customerNo;
    private final String firstName;
    private final String lastName;
    private final Date birthDate;
    private final Address address;
    private final List<BonusCard> bonusCards;
    private final String sex;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private Customer( @JsonProperty( "@customerNo" ) String customerNo,
                      @JsonProperty( "firstName" ) String firstName,
                      @JsonProperty( "lastName" ) String lastName,
                      @JsonProperty( "birthDate" ) Date birthDate,
                      @JsonProperty( "address" ) Address address,
                      @JacksonXmlElementWrapper( useWrapping=false ) @JsonProperty( "bonusCard" ) List<BonusCard> bonusCards,
                      @JsonProperty( "sex" ) String sex ) {

        this.customerNo = customerNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.bonusCards = bonusCards;
        this.sex = sex;
    }

    public static Customer of( String customerNo, String firstName, String lastName, Date birthDate, Address address, List<BonusCard> bonusCards, String sex ) {
        return new Customer( customerNo, firstName, lastName, birthDate, address, bonusCards, sex );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equal(customerNo, customer.customerNo) && Objects.equal(firstName, customer.firstName) && Objects.equal(lastName, customer.lastName) && Objects.equal(birthDate, customer.birthDate) && Objects.equal(address, customer.address) && Objects.equal(bonusCards, customer.bonusCards) && Objects.equal(sex, customer.sex);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customerNo, firstName, lastName, birthDate, address, bonusCards, sex);
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public List<BonusCard> getBonusCards() {
        return ImmutableList.copyOf( bonusCards );
    }

    public BonusCard getBonusCard( String cardNo, String goodThruYear, String goodThruMonth ) {
        return bonusCards.stream()
                .filter( card ->
                    card.getNumber().equals( cardNo ) &&
                    card.getGoodThruMonth().equals(goodThruMonth) &&
                    card.getGoodThruYear().equals(goodThruYear)
                ).findAny().orElse(null);
    }

    public String getSex() {
        return sex;
    }
}
