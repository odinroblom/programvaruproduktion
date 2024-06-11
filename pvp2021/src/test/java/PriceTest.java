import backend.SpringBootApp;
import backend.domain.ProductPriceColumn;
import backend.services.ProductPriceService;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringBootApp.class)
public class PriceTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductPriceService productPriceService;

    private ProductPriceColumn productPrice;
    private ProductPriceColumn productPriceTwo;
    private ProductPriceColumn productPriceTwoDuplicateBarcode;

    @Before
    public void init() {
        productPrice = ProductPriceColumn.of( "1", 15D );
        productPriceTwo = ProductPriceColumn.of( "2", 5D );
        productPriceTwoDuplicateBarcode = ProductPriceColumn.of( "2", 123D );
        assertEquals(0, productPriceService.getAllPrices().size() );
    }

    @Test
    public void testEquals() {
        assertEquals( ProductPriceColumn.of( "1", 15D ), productPrice );
        assertNotEquals( productPriceTwo, productPriceTwoDuplicateBarcode );
    }

    @Test
    public void testByBarcode() {
        productPriceService.save( productPrice );
        Double price = productPriceService.getPriceFor( productPrice.getBarcode() );
        assertNotNull( price );
        assertEquals( (Double) productPrice.getPrice(), price );

        //test remove
        productPriceService.remove( productPrice );
        assertNull( productPriceService.getPriceFor( productPrice.getBarcode() ) );
    }

    @Test
    public void testGetAll() {
        productPriceService.saveAll( ImmutableList.of( productPrice, productPriceTwo ) );
        Map<String, Double> map = productPriceService.getAllPrices();
        assertEquals( 2, map.size() );
        assertEquals( (Double) productPrice.getPrice(), map.get( productPrice.getBarcode() ) );
        assertEquals( (Double) productPriceTwo.getPrice(), map.get( productPriceTwo.getBarcode() ) );

        productPriceService.removeAll( List.of( productPrice, productPriceTwo ) );
    }

    @Test
    public void testReplace() {
        productPriceService.save( productPriceTwo );
        assertEquals( (Double) productPriceTwo.getPrice(), productPriceService.getPriceFor( productPriceTwo.getBarcode() ) );
        productPriceService.save( productPriceTwoDuplicateBarcode );
        assertNotEquals( (Double) productPriceTwo.getPrice(), productPriceService.getPriceFor( productPriceTwo.getBarcode() ) );
        // Price should now be replaced by duplicate
        assertEquals( (Double) productPriceTwoDuplicateBarcode.getPrice(), productPriceService.getPriceFor( productPriceTwo.getBarcode() ) );

        productPriceService.remove( productPriceTwoDuplicateBarcode );
    }

    @After
    public void empty() {
        assertEquals( 0, productPriceService.getAllPrices().size() );
    }
}
