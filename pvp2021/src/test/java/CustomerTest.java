import backend.SpringBootApp;
import backend.domain.Address;
import backend.domain.BonusCard;
import backend.domain.Customer;
import backend.managers.CustomerRegisterManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringBootApp.class)
public class CustomerTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CustomerRegisterManager customerRegisterManager;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper;

    private List<BonusCard> cards;
    private Address address;
    private Customer customer;
    private Date birthday;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mapper = new XmlMapper();
        cards = ImmutableList.of(
                    BonusCard.of( "123", "12", "2014", false, false, "js" ),
                    BonusCard.of( "321", "11", "2021", false, false, "jsten" )
                );
        address = Address.of( "kl", "25700", "Kimito", "fi" );
        birthday = Date.from( Instant.now().minusSeconds( 36000 ) );
        customer = Customer.of( "1", "Jo", "Ste", birthday, address, cards, Customer.SEX_MALE );
    }

    @Test
    public void testEquals() {
        assertEquals( customer, Customer.of( "1", "Jo", "Ste", birthday, address, cards, Customer.SEX_MALE) );

        assertNotEquals( customer, Customer.of( "2", "Jo", "Ste", birthday, address, cards, Customer.SEX_MALE) );
        assertNotEquals( customer, Customer.of( "1", "Jo", "Ste", birthday, address, ImmutableList.of(), Customer.SEX_MALE) );
        assertNotEquals( customer, Customer.of( "1", "Jo", "Ste", Date.from( Instant.now() ), address, cards, Customer.SEX_MALE) );
    }

    @Test
    public void testCustomerFromBonusCard() throws JsonProcessingException {
        BonusCard card = cards.get(0);

        mockServer.expect( requestTo( CustomerRegisterManager.root + "findByBonusCard/" + card.getNumber() + "/" + card.getGoodThruYear() + "/" + card.getGoodThruMonth() ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( mapper.writeValueAsString( customer ) )
                );

        Customer mocked = customerRegisterManager.findByCard( card.getNumber(), card.getGoodThruYear(), card.getGoodThruMonth() );
        mockServer.verify();

        assertEquals( mocked, customer );

        // Test that the cards are parsed correctly too
        BonusCard parsedCard = mocked.getBonusCard( card.getNumber(), card.getGoodThruYear(), card.getGoodThruMonth() );
        assertEquals( parsedCard, card );
        assertNotEquals( parsedCard, cards.get(1) );
    }

    @Test
    public void testCustomerFromCustomerNo() throws Exception {
        mockServer.expect( requestTo( CustomerRegisterManager.root + "findByCustomerNo/" + customer.getCustomerNo() ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( mapper.writeValueAsString( customer ) )
                );

        Customer mocked = customerRegisterManager.findByCustomerNo( customer.getCustomerNo() );
        mockServer.verify();

        assertEquals( mocked, customer );
    }
}
