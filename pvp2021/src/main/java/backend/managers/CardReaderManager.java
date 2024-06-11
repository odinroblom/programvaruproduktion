package backend.managers;

import backend.domain.Payment;
import backend.domain.states.CardReaderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static backend.utils.StringUtils.isValidResponse;

/**
 * Manages the inputs and outputs of the CardReader
 * **/
@Service
public class CardReaderManager {

    public static final String root = "http://localhost:9002/cardreader/";

    @Autowired
    private RestTemplate restTemplate;

    public String getStatus() {
        String url = root + "status/";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if ( isValidResponse( response ) )
                return response.getBody();
        } catch ( Exception ignored ) {}

        return CardReaderState.UNKNOWN.getValue();
    }

    public Payment getResult() {
        String url = root + "result/";
        try {
            ResponseEntity<Payment> response = restTemplate.getForEntity(url, Payment.class);
            if ( isValidResponse( response ) )
                return response.getBody();
        } catch ( Exception ignored ) {}

       return null;
    }

    public boolean reset() {
        String url = root + "reset/";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, HttpEntity.EMPTY, String.class );
            return ( response.getStatusCode() == HttpStatus.OK );
        } catch ( Exception ignored ) {}

        return false;
    }

    public boolean requestPayment( double amount ) {
        String url = root + "waitForPayment/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        MultiValueMap<String, Double> map = new LinkedMultiValueMap<>();
        map.add("amount", amount);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>( map, headers ), String.class );
            return ( response.getStatusCode() == HttpStatus.OK );
        } catch (Exception ignored ) {}

        return false;
    }
}
