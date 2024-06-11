package backend.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static backend.utils.StringUtils.isValidResponse;

/**
 * Manages the state of the CashBox
 * **/
@Service
public class CashBoxManager {

    public static final String root = "http://localhost:9001/cashbox/";
    @Autowired
    RestTemplate restTemplate;

    public String getStatus() {
        String url = root + "status/";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if ( isValidResponse(response) )
                return response.getBody();

        } catch (Exception ignored) {}

        return null;
    }

    public boolean open() {
        String url = root + "open/";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, HttpEntity.EMPTY, String.class );
            if ( response != null )
                return ( response.getStatusCode() == HttpStatus.OK );

        } catch (Exception ignored) {}

        return false;
    }
}
