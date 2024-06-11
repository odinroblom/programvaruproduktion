import backend.SpringBootApp;
import backend.domain.Product;
import backend.managers.ProductCatalogManager;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringBootApp.class)
public class ProductsTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductCatalogManager productCatalogManager;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper;

    private Product productBarCodeOne;
    private Product productDuplicateKeyword;
    private Product productDuplicateName;

    private List<Product> keywordList;
    private List<Product> nameList;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mapper = new XmlMapper();
        productBarCodeOne = new Product( 1, "1", 24D, "Test product", "test" );
        productDuplicateName = new Product( 2, "2", 10D, "Test product", "other keyword" );
        productDuplicateKeyword= new Product( 3, "55", 24D, "other name", "test" );
        keywordList = ImmutableList.of( productBarCodeOne, productDuplicateKeyword );
        nameList = ImmutableList.of( productBarCodeOne, productDuplicateName );
    }

    @Test
    public void testEquals() {
        Product second = new Product( 1, "1",24D, "Test product", "test" );
        assertEquals( productBarCodeOne, second );
    }

    @Test
    public void testFindByBarcode() throws JsonProcessingException {
        mockServer.expect( requestTo( ProductCatalogManager.root + "findByBarCode/" + productBarCodeOne.getBarCode() ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( mapper.writeValueAsString( productBarCodeOne ) )
                );

        Product mocked = productCatalogManager.findByBarCode( productBarCodeOne.getBarCode() );
        mockServer.verify();
        assertEquals( productBarCodeOne, mocked );
    }

    @Test
    public void testFindByKeyword() throws JsonProcessingException {
        mockServer.expect( requestTo( ProductCatalogManager.root + "findByKeyword/" + productBarCodeOne.getKeyword() ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( mapper.writeValueAsString( keywordList ) )
                );

        List<Product> mocked = productCatalogManager.findByKeyword( productDuplicateKeyword.getKeyword() );
        mockServer.verify();
        assertEquals( keywordList, mocked );
        assertNotEquals( mocked.get( 0 ), mocked.get( 1 ) );
    }

    @Test
    public void testFindByName() throws JsonProcessingException {
        mockServer.expect( requestTo( ProductCatalogManager.root + "findByName/" + URLEncoder.encode(  productBarCodeOne.getName(), StandardCharsets.UTF_8 ) ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( mapper.writeValueAsString( nameList ) )
                );

        List<Product> mocked = productCatalogManager.findByName( URLEncoder.encode( productDuplicateName.getName(), StandardCharsets.UTF_8 ) );
        mockServer.verify();
        assertEquals( nameList, mocked );
        assertNotEquals( mocked.get( 0 ), mocked.get( 1 ) );
    }

    @Test
    public void testBadResponse() throws JsonProcessingException {
        mockServer.expect( requestTo( ProductCatalogManager.root + "findByBarCode/" + productBarCodeOne.getBarCode()  ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                );

        Product mocked = productCatalogManager.findByBarCode( productBarCodeOne.getBarCode() );
        mockServer.verify();
        assertNull(mocked);
    }

    @Test
    public void testBadListResponse() throws JsonProcessingException {
        mockServer.expect( requestTo( ProductCatalogManager.root + "findByName/" + URLEncoder.encode( productBarCodeOne.getName(), StandardCharsets.UTF_8 ) ) )
                .andExpect( method( HttpMethod.GET ) )
                .andRespond( withStatus( HttpStatus.OK )
                        .contentType(MediaType.APPLICATION_XML)
                        .body( mapper.writeValueAsString( ImmutableList.of() ) )
                );

        List<Product> mocked = productCatalogManager.findByName( URLEncoder.encode( productBarCodeOne.getName(), StandardCharsets.UTF_8) );
        mockServer.verify();
        assertEquals( mocked.size(), 0 );
    }
}
