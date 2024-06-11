package frontend.controllers;

import backend.domain.ScannedItem;
import backend.managers.PoS;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Controller for the Customer window.
 * Displays scanned items and total sum
 * Similar but limited functionality compared with the Cashier view.
 */
@Component
public class CustomerController {

    @Autowired
    private PoS pos;

    @FXML
    private TableView<ScannedItem> shoppingCartCustomer;

    @FXML
    private TableColumn<ScannedItem,String> itemNameCustomer;

    @FXML
    private TableColumn<ScannedItem,Integer> itemQuantityCustomer;

    @FXML
    private TableColumn<ScannedItem,Double> itemPriceCustomer;

    @FXML
    private Label totalSumCustomer;

    @FXML
    private Label transactionMessageCustomer;

    @FXML
    private Label returnCashMessageCustomer;

    //initializes the tableview for the customer
    @FXML
    public void initialize(){
        itemNameCustomer.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemQuantityCustomer.setCellValueFactory(new PropertyValueFactory<>("itemQuantity"));
        itemPriceCustomer.setCellValueFactory(new PropertyValueFactory<>("formattedPrice"));
        itemNameCustomer.setReorderable(false); shoppingCartCustomer.getColumns().get(0).setResizable(false);
        itemQuantityCustomer.setReorderable(false); shoppingCartCustomer.getColumns().get(1).setResizable(false);
        itemPriceCustomer.setReorderable(false); shoppingCartCustomer.getColumns().get(2).setResizable(false);
    }

    //Gets table values from Cashiercontroller
    public void updateBasket(ObservableList<ScannedItem> currentSale, String totalSum){
        transactionMessageCustomer.setText("");
        returnCashMessageCustomer.setText("Return to customer: ");
        shoppingCartCustomer.setItems( currentSale );
        totalSumCustomer.setText( totalSum +" €" );
    }

    //feedback message of successful payment to customer
    public void handleSuccessfulPayment( String message ){
        Platform.runLater(() -> {
            transactionMessageCustomer.setTextFill(Color.GREEN);
            transactionMessageCustomer.setText("Payment Successful");
            returnCashMessageCustomer.setText(message);
        });
    }

    //feedback message of unsuccessful payment to customer
    public void handleFailedPayment( String reason ) {
        Platform.runLater(() -> {
            transactionMessageCustomer.setTextFill(Color.RED);
            transactionMessageCustomer.setText( "Payment Failed, " + reason );
        });
    }

}