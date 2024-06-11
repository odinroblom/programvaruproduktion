import backend.SpringBootApp;
import backend.domain.Sale;
import backend.services.SaleService;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringBootApp.class)
public class SalesTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SaleService saleService;

    private Sale sale;
    private Sale saleDuplicateBarCode;
    private Sale saleUniqueBarcode;

    private List<Sale> allSales;

    private Date twoDaysAgo;
    private Date today;

    @Before
    public void init() {
        twoDaysAgo = Date.from( Instant.now().minusSeconds( 60 * 60 * 24 * 2 ) );
        today = Date.from( Instant.now() );

        sale = Sale.of( "1", 5, "1", twoDaysAgo);
        saleDuplicateBarCode = Sale.of( "1", 5, "2", today );
        saleUniqueBarcode = Sale.of( "2", 3, "1", twoDaysAgo);
        allSales = ImmutableList.of( sale, saleDuplicateBarCode, saleUniqueBarcode );
        System.out.println(saleService.getAll().size());
        assertEquals(0, saleService.getAll().size() );

        saleService.saveAll(allSales);

        assertEquals( allSales.size(), saleService.getAll().size() );
    }

    @Test
    public void testEquals() {
        assertEquals( Sale.of( "1", 5, "1", twoDaysAgo), sale );
        assertNotEquals( sale, saleDuplicateBarCode );
    }

    @Test
    public void getAll() {
        List<Sale> sales = saleService.getAll();
        assertEquals( allSales.get( 0 ), sales.get( 0 ) );
        assertEquals( allSales.get( 1 ), sales.get( 1 ) );
        assertEquals( allSales.get( 2 ), sales.get( 2 ) );
    }

    @Test
    public void testByBarcode() {
        List<Sale> sales = saleService.getAll( sale.getBarcode() );
        assertEquals( 2, sales.size() );
        assertTrue( sales.contains( sale ) );
        assertTrue( sales.contains( saleDuplicateBarCode ) );

        List<Sale> barcodeTwo = saleService.getAll( saleUniqueBarcode.getBarcode() );
        assertEquals( 1, barcodeTwo.size() );
        assertEquals( barcodeTwo.get( 0 ), saleUniqueBarcode );
    }

    @Test
    public void testByBarcodeAndDate() {
        List<Sale> sales = saleService.getByDate( sale.getDate() );
        assertEquals( 2, sales.size() );
        assertEquals( sales.get( 0 ), sale );
        assertEquals( sales.get( 1 ), saleUniqueBarcode );
    }

    @Test
    public void testAddSingular() {
        Sale newSale = Sale.of( "44", 12, "55", today );
        assertEquals( 0, saleService.getAll( newSale.getBarcode() ).size() );
        saleService.save( newSale );
        List<Sale> sales = saleService.getAll( newSale.getBarcode() );
        assertEquals( 1, sales.size() );
        assertEquals( newSale, sales.get( 0 ) );
        saleService.remove( newSale );
        assertEquals( 0, saleService.getAll( newSale.getBarcode() ).size() );
    }

    @After
    public void removeAll() {
        List<Sale> sales = saleService.getAll();
        assertTrue( sales.size() > 0 );
        saleService.removeAll( sales );
        assertEquals( 0, saleService.getAll().size() );
    }
}
