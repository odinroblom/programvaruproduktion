package backend.managers;

import backend.domain.Product;
import backend.services.ProductPriceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static backend.utils.StringUtils.isValidResponse;

/**
 * Gets product data from the ProductCatalog
 **/
@Service
public class ProductCatalogManager {

    public static final String root = "http://localhost:9003/rest/";

    @Autowired
    private ProductPriceService productPriceService;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private RestTemplate restTemplate;

    public Product findByBarCode( String barcode ) {
        String url = root + "findByBarCode/" + barcode;
        try {
            ResponseEntity<Product> response = restTemplate.getForEntity( url, Product.class );
            if ( isValidResponse( response ) )
                return response.getBody();

        } catch (Exception ignored) {}

        return null;
    }

    public ImmutableList<Product> findByKeyword( String keyword ) {
        String url = root + "findByKeyword/" + keyword;
        try {
            ResponseEntity<Product[]> response = restTemplate.getForEntity( url, Product[].class );
            if ( isValidResponse( response ) )
                return ImmutableList.of( response.getBody() );
        } catch (Exception ignored) {}

        return ImmutableList.of();
    }

    public List<Product> findByName( String name ) {
        String url = root + "findByName/" + name;
        try {
            ResponseEntity<Product[]> response = restTemplate.getForEntity( url, Product[].class );
            if ( isValidResponse( response ) )
                return ImmutableList.of( response.getBody() );

        } catch (Exception ignored) {}

        return ImmutableList.of();
    }
}