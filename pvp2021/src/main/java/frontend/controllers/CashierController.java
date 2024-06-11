package frontend.controllers;

import backend.domain.ProductPrice;
import backend.domain.ScannedItem;
import backend.managers.PoS;
import backend.utils.DecimalHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;


/**
 * Controller for the Cashier view.
 * The class works as a "shopping cart manager" by using stashSale(), unStashSale() etc.
 * Loads, saves and removes products from tablecolumns which are used to have a visual shopping cart.
 * Communicates with the PoS class
 */

@Component
public class CashierController {

    @Autowired
    private PoS pos;

    // JavaFX buttons, tables, labels and etc.
    @FXML
    private TableView<ScannedItem> shoppingCart;

    @FXML
    private TableColumn<ScannedItem,String> itemName;

    @FXML
    private TableColumn<ScannedItem,Integer> itemQuantity;

    @FXML
    private TableColumn<ScannedItem,Double> itemPrice;

    @FXML
    private ComboBox<ProductPrice> itemSelection;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private Label totalsum;

    @FXML
    private ComboBox<String> searchBySelection;

    @FXML
    private TextField itemSearchTerm;

    @FXML
    private TextField stashName;

    @FXML
    private ComboBox<String> chooseStash;

    @FXML
    private TextField payedWithCash;

    @FXML
    private Label transactionMessage;

    @FXML
    private Label ReturnCashMessage;

    @FXML
    private TextField discountAmount;

    @FXML
    private Button receipt;

    private int stashDefaultNameCounter;

    // Initializing shopping cart columns and rows, some user limitation to avoid possible crashing.
    @FXML
    public void initialize(){
        stashDefaultNameCounter = 0;
        itemSelection.setItems( FXCollections.observableArrayList() );
        itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemQuantity.setCellValueFactory(new PropertyValueFactory<>("itemQuantity"));
        itemPrice.setCellValueFactory(new PropertyValueFactory<>("formattedPrice"));
        itemName.setReorderable(false); shoppingCart.getColumns().get(0).setResizable(false);
        itemQuantity.setReorderable(false); shoppingCart.getColumns().get(1).setResizable(false);
        itemPrice.setReorderable(false); shoppingCart.getColumns().get(2).setResizable(false);
        shoppingCart.setEditable(true);
        priceField.setTextFormatter(new TextFormatter<>((change) -> {
            String text = change.getControlNewText();
            return ( text.matches("\\d*\\.?\\d*") ) ? change : null;
        }));
        payedWithCash.setTextFormatter(new TextFormatter<>((change) -> {
            String text = change.getControlNewText();
            return  ( text.matches("\\d*\\.?\\d*") ) ? change : null;
        }));
        discountAmount.setTextFormatter(new TextFormatter<>((change) -> {
            String text = change.getControlNewText();
            return ( text.matches("\\d*\\.?\\d*") ) ? change : null;
        }));
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ( !newValue.matches("\\d*") ) quantityField.setText(newValue.replaceAll("[^\\d]", ""));
        });
    }

    // Search products by communicating with POS (src/main/java/backend/managers/PoS)
    @FXML
    private void searchForItems(ActionEvent event) {
        if ( searchBySelection != null ) {
            String searchTerm = itemSearchTerm.getText();
            switch (searchBySelection.getValue()) {
                case "Name":
                    pos.getProductPricingByName(searchTerm, this::handleFindProducts);
                    break;
                case "Barcode":
                    pos.getProductPriceByBarCode(searchTerm, this::handleFindProduct);
                    break;
                case "Keyword":
                    pos.getProductPricingByKeyword(searchTerm, this::handleFindProducts);
                    break;
            }
        }
    }
//listens on clicked item on the table
    @FXML
    public void onItemSelected(ActionEvent event) {
        ProductPrice productPrice = itemSelection.getValue();
        if ( productPrice != null && productPrice.getPrice() != null ) {
            priceField.setEditable( false );
            priceField.setText( DecimalHandler.format( productPrice.getPrice() ) );
        } else {
            priceField.setEditable( true );
            priceField.setText( "" );
        }
    }
// adds stuff to table
    @FXML
    private void addToCart(ActionEvent event) {
        double discountPercentage;
        try {
            discountPercentage = Double.parseDouble( discountAmount.getText() );
        } catch (Exception e) {
            discountPercentage = 0;
        }
        if ( discountPercentage > 99 ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText( "Percentage should not overexceed 99%" );
            alert.showAndWait();
            return;
        }

        ProductPrice selectedItem = itemSelection.getValue();
        try {
            Integer quantity = Integer.parseInt(quantityField.getText());

            Double price = selectedItem.getPrice();
            if (price == null) {
                price = Double.parseDouble(priceField.getText());
            }
            price *= quantity;
            price *= (1 + (selectedItem.getProduct().getVat() / 100));
            price *= (1 - (discountPercentage / 100));

            ScannedItem item = ScannedItem.of(selectedItem.getProduct(), quantity, price);

            pos.addItemsToCart(item);
        } catch ( NumberFormatException ignored) {
        }
    }
    // removs stuff from the table
    @FXML
    private void removeItemFromCart(ActionEvent event) {
        ScannedItem item = shoppingCart.getSelectionModel().getSelectedItem();
        pos.removeItemFromCart(item);
    }

    //Send table of products forward for payment
    @FXML
    private void payForItems(ActionEvent event) {
        if( shoppingCart.getItems().size() > 0 ){
            double cashAmount = Double.parseDouble( payedWithCash.getText() );
            pos.payForItems(cashAmount);
        }
    }

    //Saves the current table for later
    @FXML
    private void stashSale(ActionEvent event) {
        if( shoppingCart.getItems().size() > 0 ){
            pos.stashSale();
            String stash = this.stashName.getText();
            if ( !StringUtils.hasText( stash ) ) {
                stashDefaultNameCounter += 1;
                stash = String.valueOf( stashDefaultNameCounter );
            }
            chooseStash.getItems().add( stash );
            this.stashName.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No shopping items to stash");
            alert.showAndWait();
        }
    }

    @FXML
    private void unstashSale(ActionEvent event) {
        if (shoppingCart.getItems().isEmpty()) {
            int itemIndex = chooseStash.getSelectionModel().getSelectedIndex();
            if ( itemIndex >= 0 ) {
                pos.unStashSale(itemIndex);
                chooseStash.getItems().remove(itemIndex);
            }
        }
        else{
            //User limitation
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("The shopping basket has to be empty, save or sell the current basket.");
            alert.showAndWait();
        }
    }

    //Alert window asking user if they want a receipt
    public Boolean printReceipt(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Receipt");
        alert.setContentText( "Print receipt?" );
        Optional<ButtonType> buttonType = alert.showAndWait();
        return (buttonType.isPresent() && buttonType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE));
    }

    public void updateBasket(ObservableList<ScannedItem> currentSale, String totalSum){
        transactionMessage.setText("");
        ReturnCashMessage.setText("Return to customer: ");
        shoppingCart.setItems( currentSale );
        totalsum.setText( totalSum + " €");
    }

    //Error message for user
    public void handleFailedPayment(String state) {
        Platform.runLater(() -> {
            transactionMessage.setTextFill(Color.RED);
            transactionMessage.setText( "Payment Failed " + state );
        });
    }

    public void handleSuccessfulPayment(/* List<ScannedItem> scannedItems */String message){
        Platform.runLater(() -> {
            transactionMessage.setTextFill(Color.GREEN);
            transactionMessage.setText("Payment Successful");
            ReturnCashMessage.setText(message);
        });
    }

    public void handleDisconnectedCardReader(){
        Platform.runLater(() -> {
            transactionMessage.setTextFill(Color.RED);
            transactionMessage.setText("Card reader disconnected");
        });
    }

    private void handleFindProduct( ProductPrice productPrice ){
        Platform.runLater(() -> {
            if ( productPrice != null ) {
                itemSelection.getItems().setAll( productPrice );
                itemSelection.setValue( productPrice );
            }
        });
    }

    private void handleFindProducts( List<ProductPrice> productPrices ){
        Platform.runLater(() -> {
            if ( productPrices.size() > 0 ) {
                itemSelection.getItems().setAll( productPrices );
                itemSelection.setValue( productPrices.get( 0 ) );
            }
        });
    }



}