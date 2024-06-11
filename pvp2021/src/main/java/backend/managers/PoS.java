package backend.managers;

import backend.domain.*;
import backend.domain.states.BonusState;
import backend.domain.states.CardReaderState;
import backend.domain.states.PaymentState;
import backend.services.ProductPriceService;
import backend.services.SaleService;
import backend.utils.DecimalHandler;
import backend.utils.Poll;
import backend.utils.ReceieptHandler;
import com.google.common.collect.ImmutableList;
import frontend.controllers.CashierController;
import frontend.controllers.CustomerController;
import frontend.controllers.MarketingController;
import frontend.controllers.SalesController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Manages the communication between frontend controllers and backend managers and services
 * Utilizes polling to make the experience more seamless.
 */
@Service
public class PoS {

    private static final Logger log = LoggerFactory.getLogger(PoS.class);

    @Autowired
    private ProductCatalogManager productCatalogManager;

    @Autowired
    private CashBoxManager cashBoxManager;

    @Autowired
    private CardReaderManager cardReaderManager;

    @Autowired
    private CustomerRegisterManager customerRegisterManager;

    @Autowired
    CashierController cashierController;

    @Autowired
    CustomerController customerController;

    @Autowired
    MarketingController marketingController;

    @Autowired
    SalesController salesController;

    @Autowired
    SaleService saleService;

    @Autowired
    ProductPriceService productPriceService;

    private final ExecutorService executorService;

    private ObservableList<ScannedItem> currentSale;

    private LinkedList<ObservableList<ScannedItem>> stashedSales = new LinkedList<>();;

    private final BooleanSupplier setCardReaderToIdle = () -> {
        if ( CardReaderState.IDLE.matches( cardReaderManager.getStatus() ) )
            return true;

        cardReaderManager.reset();
        return false;
    };

    //
    public PoS() {
        executorService = Executors.newFixedThreadPool( 2 );
        currentSale = FXCollections.observableArrayList();
    }

    public void terminateExecutorService() {
        executorService.shutdown();
    }

    public void getProductPriceByBarCode(String barcode, Consumer<ProductPrice> eventHandler ) {
        executorService.execute( () -> eventHandler.accept( ProductPrice.of( productCatalogManager.findByBarCode(barcode), productPriceService.getPriceFor(barcode) ) ) );
    }

    public void getProductPricingByName(String name, Consumer<List<ProductPrice>> eventHandler ) {
        executorService.execute( () -> {
            Map<String, Double> prices = productPriceService.getAllPrices();

            ImmutableList.Builder<ProductPrice> builder = ImmutableList.builder();
            for (Product product : productCatalogManager.findByName(name)) {
                builder.add( ProductPrice.of( product, prices.get( product.getBarCode() ) ) );
            }
            eventHandler.accept( builder.build() );
        } );
    }

    public void getProductPricingByKeyword(String keyword, Consumer<List<ProductPrice>> eventHandler  ) {
        executorService.execute( () -> {
            Map<String, Double> prices = productPriceService.getAllPrices();

            ImmutableList.Builder<ProductPrice> builder = ImmutableList.builder();
            for (Product product : productCatalogManager.findByKeyword( keyword ) ) {
                builder.add( ProductPrice.of( product, prices.get( product.getBarCode() ) ) );
            }
            eventHandler.accept( builder.build() );
        } );
    }

    private boolean payByCash( double amount ) {
        return cashBoxManager.open();
    }

    private boolean bonusCardPaymentAccepted( Payment payment, BonusCard bonusCard ) {
        return ( BonusState.ACCEPTED.matches( payment.getBonusState() ) && ( bonusCard != null ) && bonusCard.isValid() );
    }

    /**
     * The most important method of the PoS system.
     * Handles transactions by comining different elements of our system.
     *
     * Polls the state of the cardreader
     * Registers different data depending on the cardreader's result, such as bonuscard holder information.
     *
     * @param cashAmount amount paid in cash
     * @param cardAmount amount paid by card ( credit/debit, bonus, combined )
     */
    public void processTransaction( double cashAmount , double cardAmount ) {
        if ( !Poll.until( setCardReaderToIdle, 1000, 10000 ) ) {
            cashierController.handleDisconnectedCardReader();
            return;
        }

        if ( cashAmount > 0 ) {
            payByCash( cashAmount );
            if ( cardAmount <= 0){
                String change = DecimalHandler.format( cashAmount - totalSum() );
                printReceipt();
                nextSale( change );
            }
        }

        if ( cardAmount > 0 ) {
            cardReaderManager.requestPayment(cardAmount);

            if ( !Poll.until( () -> CardReaderState.DONE.matches( cardReaderManager.getStatus() ), 1000, 15000 ) ) {
                cashierController.handleFailedPayment( PaymentState.TIME_OUT.getValue() );
                cardReaderManager.reset();
                return;
            }

            Payment payment = cardReaderManager.getResult();
            String paymentState = payment.getPaymentState();

            boolean bonusAccepted = false;
            BonusCard bonusCard = null;
            Customer customer = payment.getCustomer( customerRegisterManager );
            if (customer != null) {
                bonusCard = customer.getBonusCard(payment.getBonusCardNumber(), payment.getGoodThruYear(), payment.getGoodThruMonth());
                bonusAccepted = bonusCardPaymentAccepted(payment, bonusCard);
            }

            if ( PaymentState.ACCEPTED.matches( paymentState ) || bonusAccepted ) {

                ImmutableList.Builder<Sale> builder = ImmutableList.builder();
                for ( ScannedItem scannedItem : currentSale ) {
                    builder.add( Sale.of( scannedItem.getBarcode(), scannedItem.getItemQuantity(), customer != null ? customer.getCustomerNo() : null, Date.from( Instant.now() ) ) );
                }
                saleService.saveAll( builder.build() );

                printReceipt();

                nextSale( "0" );

            } else {
                String reason = paymentState;
                if ( bonusCard != null ) {
                    if ( bonusCard.isBlocked() ) {
                        reason = BonusState.BLOCKED_CARD.getValue();
                    } else if ( bonusCard.isExpired() ) {
                        reason = BonusState.EXPIRED_CARD.getValue();
                    } else if ( !BonusState.ACCEPTED.matches( payment.getBonusState() ) ) {
                        reason = payment.getBonusState();
                    }
                } else if ( payment.getBonusCardNumber() != null ) {
                    reason = "Bad BonusCard";
                }
                cashierController.handleFailedPayment( reason );
                customerController.handleFailedPayment( "Declined" );
            }

            cardReaderManager.reset();
        }
    }
    public void printReceipt(){
        //Sends the shoppingcart data to ReceiptHandler
        if( cashierController.printReceipt() ) {
            double totalsum = totalSum();
            try {
                ReceieptHandler.printReceipt(currentSale, totalsum);
            } catch (IOException ignored) {}
        }
    }

    private void nextSale( String customerReturn ) {
        cashierController.handleSuccessfulPayment( "Return to customer: " + customerReturn + " €" );
        customerController.handleSuccessfulPayment( customerReturn + " €" );
        currentSale = FXCollections.observableArrayList();
        String rounded = DecimalHandler.format( totalSum() );
        cashierController.updateBasket( currentSale, rounded );
        customerController.updateBasket( currentSale, rounded );
    }

    public void addItemsToCart(ScannedItem item){
        currentSale.add(item);
        String rounded = DecimalHandler.format( totalSum() );
        cashierController.updateBasket( currentSale, rounded );
        customerController.updateBasket( currentSale, rounded );
    }

    public void removeItemFromCart(ScannedItem item){
        currentSale.remove(item);
        String rounded = DecimalHandler.format( totalSum() );
        cashierController.updateBasket( currentSale, rounded );
        customerController.updateBasket( currentSale, rounded );
    }

    public void payForItems(double cashAmount) {
        if ( totalSum() > 0 ) {
            processTransaction(cashAmount, totalSum()-cashAmount);
        }
    }
    //Returns the totalsum of the products in the shopping cart
    public double totalSum(){
        double sum = 0;
        for (ScannedItem item : currentSale) {
            sum += item.getItemPrice();
        }
        return sum;
    }

    //Saves shopping cart for later.
    public void stashSale(){
        stashedSales.add(currentSale);
        currentSale = FXCollections.observableArrayList();
        String rounded = DecimalHandler.format( totalSum() );
        cashierController.updateBasket(currentSale, rounded );
        customerController.updateBasket(currentSale, rounded );
    }

    //replaces current shopping cart (has to be empty) with a chosen saved shopping cart,
    public void unStashSale(int itemIndex){
        currentSale = stashedSales.remove(itemIndex);
        String rounded = DecimalHandler.format( totalSum() );
        cashierController.updateBasket(currentSale, rounded );
        customerController.updateBasket(currentSale, rounded );
    }

    private boolean saleDateBetween( Sale sale, Date saleFrom, Date saleTo ) {
        Date saleDate = sale.getDate();
        return ( saleFrom.compareTo( saleDate ) <= 0 && saleTo.compareTo( saleDate ) >= 0 );
    }

    private boolean checkCustomerData( Date birthDayFrom, Date birthdayTo, String buyerSex ) {
        return ( ( birthDayFrom != null ) && ( birthdayTo != null ) ) || !buyerSex.equals( Customer.SEX_ANY );
    }

    private boolean birthDateBetween( Customer customer, Date birthDayFrom, Date birthdayTo ) {
        if ( birthDayFrom != null && birthdayTo != null ) {
            if ( customer == null )
                return false;

            Date birthday = customer.getBirthDate();
            return birthDayFrom.compareTo(birthday) <= 0 && birthdayTo.compareTo(birthday) >= 0;
        }
        return true;
    }

    private boolean genderMatch( Customer customer, String targetSex ) {
        if ( targetSex.equals( Customer.SEX_ANY ) )
            return true;

        return ( customer != null ) && ( targetSex.equals( customer.getSex() ) );
    }

    public void displaySales( Date saleFrom, Date saleTo, Date birthDayFrom, Date birthdayTo, String buyerSex ) throws Exception {
        if ( saleFrom == null || saleTo == null ) {
            return;
        }

        Map<String, Integer> map = new HashMap<>();
        for ( Sale sale : saleService.getAll() ) {
            if ( !saleDateBetween( sale, saleFrom, saleTo ) )
                continue;

            if ( checkCustomerData( birthDayFrom, birthdayTo, buyerSex ) ) {
                Customer customer = null;
                if (sale.getCustomerNo() != null) {
                    customer = customerRegisterManager.findByCustomerNo(sale.getCustomerNo());
                }
                if ( ( customer == null ) || !birthDateBetween( customer, birthDayFrom, birthdayTo ) || !genderMatch( customer, buyerSex ) )
                    continue;
            }

            map.put( sale.getBarcode(), map.getOrDefault( sale.getBarcode(), 0 ) + sale.getAmount() );
        }

        marketingController.onDisplaySales( map );
    }

    public void displayItemsSold( String date) throws Exception {
        if ( date == null) {
            return;
        }
        Map<String, Integer> sales = new HashMap<>();
        for ( Sale sale : saleService.getByDate(date) ) {
            sales.put( sale.getBarcode(), sales.getOrDefault( sale.getBarcode(), 0 ) + sale.getAmount() );
        }
        salesController.handleFindProductForSales( sales );
    }


}
