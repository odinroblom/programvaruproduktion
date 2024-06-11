import backend.SpringBootApp;
import backend.domain.*;
import backend.managers.CardReaderManager;
import backend.managers.CustomerRegisterManager;
import backend.managers.PoS;
import backend.services.SaleService;
import backend.utils.DateHandler;
import com.google.common.collect.ImmutableList;
import frontend.controllers.CashierController;
import frontend.controllers.CustomerController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringBootApp.class)
public class PoSIntegrationTest {

    // I could probably mock SaleService but this is closer to the real deal.
    @Autowired
    private SaleService saleService;

    @Autowired
    @InjectMocks
    PoS pos;

    @Mock
    private CustomerRegisterManager customerRegisterManager;

    @Mock
    private CardReaderManager cardReaderManager;

    @Mock
    private CashierController cashierController;

    @Mock
    private CustomerController customerController;

    private ScannedItem scannedItem;
    private Customer customer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        scannedItem = ScannedItem.of(Product.of(1, "55", 24D, "Apple", "food"), 3, 0.85D);

        Date yesterday = Date.from(Instant.now().minusSeconds(60 * 60 * 24));
        Payment payment = Payment.of("123", "ACCEPTED", null, "12", "2014", null, null);

        List<BonusCard> bonusCards = ImmutableList.of(
                BonusCard.of("321", "11", "2013", false, false, "testare"),
                BonusCard.of(payment.getBonusCardNumber(), payment.getGoodThruMonth(), payment.getGoodThruYear(), false, false, "testsson")
        );
        customer = Customer.of("1", "Test", "Testsson", yesterday, Address.of("test st", "123400", "te", "Testland"), bonusCards, Customer.SEX_MALE);

        Mockito.when(cardReaderManager.getStatus()).thenReturn("IDLE", "DONE");
        Mockito.when(cardReaderManager.requestPayment(anyDouble())).thenReturn(true);
        Mockito.when(cardReaderManager.getResult()).thenReturn(payment);
        Mockito.when(cardReaderManager.reset()).thenReturn(true);

        Mockito.when(customerRegisterManager.findByCard(payment.getBonusCardNumber(), payment.getGoodThruYear(), payment.getGoodThruMonth())).thenReturn(customer);

        Mockito.doNothing().when(cashierController).updateBasket(anyObject(), anyString());
        Mockito.doNothing().when(customerController).updateBasket(anyObject(), anyString());
    }

    @Test
    public void testBonusCardSale() {
        assertEquals(0, saleService.getAll().size());

        pos.addItemsToCart(scannedItem);
        pos.processTransaction(0D, 24D);

        List<Sale> sales = saleService.getAll();
        assertEquals(1, sales.size()); //wohoo!

        Sale sale = sales.get(0);

        assertEquals(sale.getBarcode(), scannedItem.getBarcode());
        assertEquals(sale.getAmount(), scannedItem.getItemQuantity());
        assertEquals(sale.getCustomerNo(), customer.getCustomerNo());
        String a = DateHandler.format(sale.getDate());
        String b =DateHandler.format(Date.from(Instant.now()));
        assertEquals(a, b);

        saleService.remove(sale);
    }
}
