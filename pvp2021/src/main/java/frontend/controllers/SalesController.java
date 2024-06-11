package frontend.controllers;

import backend.domain.ProductPrice;
import backend.managers.PoS;
import backend.domain.ProductPriceColumn;
import backend.services.ProductPriceService;
import backend.services.SaleService;
import backend.utils.DateHandler;
import backend.utils.DecimalHandler;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Sales View
 * Enables defining prices for products in the ProductCatalog.
 * Displays sales data for any given day. More limited than Marketing view.
 **/

@Component
public class SalesController {

    @Autowired
    private PoS pos;

    @Autowired
    private ProductPriceService productPriceService;

    @Autowired
    private SaleService saleService;

    @FXML
    private TextField date;

    @FXML
    private ComboBox<String> searchBySelection;

    @FXML
    private TextField itemSearchTerm;

    @FXML
    TableView<ProductPrice> priceTable;

    @FXML
    TableColumn<ProductPrice, String> productName;

    @FXML
    TableColumn<ProductPrice, String> productBarCode;

    @FXML
    TableColumn<ProductPrice, String> productPrice;

    @FXML
    TableView<Map.Entry<String, Integer>> itemsSoldTable;

    @FXML
    TableColumn<Map.Entry<String, Integer>, String> soldProductBarCode;

    @FXML
    TableColumn<Map.Entry<String, Integer>, Integer> soldProductQty;

    ObservableList<Map.Entry<String, Integer>> productsSold;

    @FXML
    public void initialize() {
        date.setText( DateHandler.format( Instant.now() ) );
        priceTable.setEditable( true );
        priceTable.setItems( FXCollections.observableArrayList() );

        productName.setCellValueFactory( productPrice -> new SimpleObjectProperty<>( productPrice.getValue().getProduct().getName() ) );
        productBarCode.setCellValueFactory( productPrice -> new SimpleObjectProperty<>( productPrice.getValue().getProduct().getBarCode() ) );
        productPrice.setCellFactory(TextFieldTableCell.forTableColumn());
        productPrice.setCellValueFactory( productPrice -> new SimpleObjectProperty<>( DecimalHandler.format( productPrice.getValue().getPrice() ) ) );
        productPrice.setOnEditCommit( this::handleEditPriceCell );

        productsSold = FXCollections.observableArrayList();
        itemsSoldTable.setItems( productsSold );
        soldProductBarCode.setCellValueFactory( cellData -> new SimpleObjectProperty<>( cellData.getValue().getKey() ) );
        soldProductQty.setCellValueFactory( cellData -> new SimpleObjectProperty<>( cellData.getValue().getValue() ) );
    }


    @FXML
    public void searchForItems(ActionEvent event) {
        if ( searchBySelection != null ) {
            String searchTerm = itemSearchTerm.getText();
            switch ( searchBySelection.getValue())  {
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

    @FXML
    public void searchForSoldItems(ActionEvent event) {
        if ( date != null ) {
            try {
                pos.displayItemsSold(date.getText());
            } catch ( Exception ignored ) {}
        }
    }

    public void handleFindProductForSales(Map<String, Integer> sales ){
        Platform.runLater( () -> {
            productsSold.setAll(sales.entrySet());
        } );
    }

    private void handleFindProduct( ProductPrice productPrice ){
        Platform.runLater(() -> {
            if ( productPrice != null ) {
                priceTable.getItems().setAll( productPrice );
            }
        });
    }


    private void handleFindProducts( List<ProductPrice> products ){
        Platform.runLater(() -> {
            if ( products.size() > 0 ) {
                priceTable.getItems().setAll( products );
            }
        });
    }

    //Updates the price the user sets for a product
    private void handleEditPriceCell( TableColumn.CellEditEvent<ProductPrice, String> cellEditEvent ) {
        String newValue = cellEditEvent.getNewValue();
        if ( StringUtils.hasText( newValue ) && newValue.matches("\\d*\\.?\\d*") ) {
            double price = Double.parseDouble(newValue);
            if ( price >= 0 ) {
                ProductPriceColumn productPrice = ProductPriceColumn.of( cellEditEvent.getRowValue().getProduct().getBarCode(), price );
                productPriceService.save( productPrice );
                return;
            }
        }
        //default to writing the original Product back to the column
        priceTable.getItems().set( cellEditEvent.getTablePosition().getColumn(), cellEditEvent.getRowValue() );
    }
}
