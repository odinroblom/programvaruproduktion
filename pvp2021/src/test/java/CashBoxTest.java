import backend.SpringBootApp;
import backend.managers.CashBoxManager;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringBootApp.class)
public class CashBoxTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CashBoxManager cashBoxManager;

    private MockRestServiceServer mockServer;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testStatus() {
        mockServer.expect( requestTo( CashBoxManager.root + "status/" ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( "OPEN" )
                );

        assertEquals( cashBoxManager.getStatus(), "OPEN" );
        mockServer.verify();
    }

    @Test
    public void testOpen() {
        mockServer.expect( requestTo( CashBoxManager.root + "open/" ) )
                .andExpect( method( HttpMethod.POST ) )
                .andRespond( withStatus( HttpStatus.OK )
                );

        assertTrue( cashBoxManager.open() );
        mockServer.verify();
    }
}
