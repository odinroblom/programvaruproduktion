package backend.managers;

import backend.domain.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static backend.utils.StringUtils.isValidResponse;

/**
 * Gets Customer data from the CustomerRegister.
 **/
@Service
public class CustomerRegisterManager {

    public static final String root = "http://localhost:9004/rest/";
    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    RestTemplate restTemplate;

    //Here we actually want to throw an exception in case we can't reach the customer registry.
    public Customer findByCustomerNo( String customerNo ) throws Exception {
        String url = root + "findByCustomerNo/" + customerNo;
        ResponseEntity<Customer> response = restTemplate.getForEntity( url, Customer.class );
        if ( isValidResponse( response ) )
            return response.getBody();

        return null;
    }

    public Customer findByCard(String cardNo, String goodThruYear, String goodThruMonth ) {
        String url = root + "findByBonusCard/" + cardNo + "/" + goodThruYear + '/' + goodThruMonth;
        try {
            ResponseEntity<Customer> response = restTemplate.getForEntity( url, Customer.class );
            if ( isValidResponse( response ) )
                return response.getBody();

        } catch ( Exception ignored ) {}
        return null;
    }
}
