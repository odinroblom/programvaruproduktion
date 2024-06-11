import backend.SpringBootApp;
import backend.domain.Payment;
import backend.domain.states.BonusState;
import backend.domain.states.CardReaderState;
import backend.domain.states.PaymentCardType;
import backend.domain.states.PaymentState;
import backend.managers.CardReaderManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringBootApp.class)
public class CardReaderTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CardReaderManager cardReaderManager;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper;

    private Payment normalPayment;
    private Payment bonusPayment;
    private Payment combined;

    private Double requestAmount;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mapper = new XmlMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        normalPayment = Payment.of( null, null, "123", null, null, PaymentState.ACCEPTED.getValue(), PaymentCardType.CREDIT.getValue() );
        bonusPayment = Payment.of( "321", BonusState.ACCEPTED.getValue(), null, "12", "2014", null, null );
        combined = Payment.of( "123", BonusState.ACCEPTED.getValue(), "123", "12", "2014", PaymentState.ACCEPTED.getValue(), PaymentCardType.CREDIT.getValue() );
        requestAmount = 10D;
    }

    private void reset() {
        mockServer.expect( requestTo( CardReaderManager.root + "reset/" ) )
                .andExpect( method( HttpMethod.POST ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                );
    }

    private void status( String status ) {
        mockServer.expect( requestTo( CardReaderManager.root + "status/" ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( status )
                );
    }

    private void initiate() {
        mockServer.expect( requestTo( CardReaderManager.root + "waitForPayment/" ) )
                .andExpect( method( HttpMethod.POST ) )
                .andExpect( content().string( "amount=" + requestAmount.toString() ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                );
    }

    private void result( Payment payment ) throws JsonProcessingException {
        mockServer.expect( requestTo( CardReaderManager.root + "result/" ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( mapper.writeValueAsString( payment ) )
                );
    }

    @Test
    public void testEquals() {
        assertEquals( normalPayment, Payment.of( null, null, "123", null, null, PaymentState.ACCEPTED.getValue(), PaymentCardType.CREDIT.getValue() ) );
        assertNotEquals( normalPayment, bonusPayment );
    }

    @Test
    public void testSequence() throws JsonProcessingException {
        initiate();
        status( CardReaderState.DONE.getValue() );
        result( normalPayment );
        reset();

        assertTrue( cardReaderManager.requestPayment( requestAmount ) );
        assertEquals( CardReaderState.DONE.getValue(), cardReaderManager.getStatus() );
        assertNotNull( cardReaderManager.getResult() );
        assertTrue( cardReaderManager.reset() );

        mockServer.verify();
    }

    @Test
    public void testCardPayment() throws JsonProcessingException {
        result( normalPayment );

        Payment mocked = cardReaderManager.getResult();
        assertNotNull( mocked );
        assertEquals( mocked, normalPayment );

        mockServer.verify();
    }

    @Test
    public void testBonusPayment() throws JsonProcessingException {
        result( bonusPayment );

        Payment mocked = cardReaderManager.getResult();
        assertNotNull( mocked );
        assertEquals( mocked, bonusPayment );

        mockServer.verify();
    }

    @Test
    public void testCombinedPayment() throws JsonProcessingException {
        result( combined );

        Payment mocked = cardReaderManager.getResult();
        assertNotNull( mocked );
        assertEquals( mocked, combined );
        assertEquals( mocked.getBonusCardNumber(), mocked.getPaymentCardNumber() );

        mockServer.verify();
    }
}
