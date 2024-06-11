import backend.SpringBootApp;
import backend.domain.Product;
import backend.domain.ProductPrice;
import backend.managers.PoS;
import backend.managers.ProductCatalogManager;
import backend.domain.ProductPriceColumn;
import backend.services.ProductPriceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableList;
import org.junit.After;
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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * Integration test for mapping between product and its price (nullable)
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringBootApp.class)
public class ProductPriceIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PoS pos;

    @Autowired
    private ProductPriceService productPriceService;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper;

    private String keyword;
    List<Product> keywordList;
    Double expectedPrice;
    private volatile List<ProductPrice> productPrices;
    private CountDownLatch lock;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mapper = new XmlMapper();
        keyword = "test";
        keywordList = ImmutableList.of(
                new Product( 1, "5", 24D, "product with price", keyword ),
                new Product( 2, "6", 10D, "no price", keyword )
        );
        expectedPrice = 123D;
        lock = new CountDownLatch(1);
        productPrices = null;
    }

    /**
     * Tests mapping between a product and its price through the Point of Sale system
     * the first product should have "expectedPrice" and the other one should have no price set.
     *
     * Only tests for keywords as we are not really testing for GET/parsing. That is covered in other tests.
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testProductPrice() throws JsonProcessingException {
        mockServer.expect( requestTo( ProductCatalogManager.root + "findByKeyword/" + keyword ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( mapper.writeValueAsString( keywordList ) )
                );

        productPriceService.save( ProductPriceColumn.of( keywordList.get( 0 ).getBarCode(), expectedPrice ) );

        pos.getProductPricingByKeyword( keyword, this::consumer );
    }

    /**
     * Consumer for the product price mapping since PoS is multi-threaded
     * Uses a locking mechanism to sync threads.
     */
    private void consumer( List<ProductPrice> productPrices ) {
        this.productPrices = productPrices;
        lock.countDown();
    }

    @After
    public void finish() throws InterruptedException {
        lock.await( 20, TimeUnit.SECONDS );
        mockServer.verify();
        assertNotNull( productPrices );

        assertEquals( productPrices.get(0).getProduct(), keywordList.get(0) );
        assertEquals( productPrices.get(1).getProduct(), keywordList.get(1) );

        assertEquals( expectedPrice, productPrices.get(0).getPrice() );
        assertNull( productPrices.get(1).getPrice() );
    }
}
