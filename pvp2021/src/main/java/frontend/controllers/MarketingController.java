package frontend.controllers;

import backend.managers.PoS;
import backend.services.SaleService;
import backend.utils.DateHandler;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * Controller for the Marketing view
 * Displays information about registered sales and customer data for purchases made with a BonusCard.
 */
@Component
public class MarketingController {

    @Autowired
    private PoS pos;

    @Autowired
    SaleService saleService;

    @FXML
    TextField timeFrom;

    @FXML
    TextField timeTo;

    @FXML
    TextField buyerBirthdayFrom;

    @FXML
    TextField buyerBirthdayTo;

    @FXML
    ComboBox customerSex;

    @FXML
    TableView<Map.Entry<String, Integer>> saleRegister;

    @FXML
    TableColumn<Map.Entry<String, Integer>, String> barCode;

    @FXML
    TableColumn<Map.Entry<String, Integer>, Integer>  amount;

    @FXML
    Button loadButton;

    @FXML
    Label errorMessage;

    @FXML
    CategoryAxis categoryAxis;

    @FXML
    NumberAxis numberAxis;

    @FXML
    BarChart<String,Number> barChart;

    ObservableList<Map.Entry<String, Integer>> sales;

    private int chartCount;

    @FXML
    public void initialize() {
        Instant instant = Instant.now();
        timeTo.setText( DateHandler.format( instant ) );
        timeFrom.setText( DateHandler.format( instant.minus( 1, ChronoUnit.DAYS ) ) );
        errorMessage.setTextFill( Color.RED );
        saleRegister.setEditable( true );
        sales = FXCollections.observableArrayList();
        saleRegister.setItems( sales );
        barCode.setCellValueFactory( cellData -> new SimpleObjectProperty<>( cellData.getValue().getKey() ) );

        barChart.setTitle("Shopping Summary");
        barChart.setCategoryGap(50);
        barChart.setBarGap(1);

        amount.setCellValueFactory( cellData -> new SimpleObjectProperty<>( cellData.getValue().getValue() ) );
        categoryAxis.setLabel("Item Barcode");
        categoryAxis.setAnimated( false );
        numberAxis.setLabel("Quantity");
        amount.setSortType(TableColumn.SortType.DESCENDING);
        saleRegister.getSortOrder().add(amount);
        chartCount = 1;
    }

    //Loads wanted data on the table and xy-axis
    @FXML
    public void loadButton(ActionEvent event) {
        try {
            errorMessage.setText( "" );
            Date birthdayFrom = buyerBirthdayFrom.getText().length() == 0 ? null : DateHandler.parse( buyerBirthdayFrom.getText() );
            Date birthdayTo = buyerBirthdayTo.getText().length() == 0 ? null : DateHandler.parse( buyerBirthdayTo.getText() );
            pos.displaySales(
                    DateHandler.parse( timeFrom.getText() ),
                    DateHandler.parse( timeTo.getText() ),
                    birthdayFrom,
                    birthdayTo,
                    customerSex.getValue().toString()
            );
        } catch ( ResourceAccessException request ) {
            errorMessage.setTextFill( Color.RED );
            errorMessage.setText( "Please launch CustomerRegistry or limit search terms" );
        } catch ( Exception other ) {
            errorMessage.setTextFill( Color.RED );
            errorMessage.setText( "Bad input!" );
        }
    }

    @FXML
    private void clearChart(ActionEvent event){
        barChart.getData().clear();
        chartCount = 1;
    }

    public void onDisplaySales( Map<String, Integer> sumOfSales ){
        Platform.runLater( () -> {
            sales.setAll(sumOfSales.entrySet());
            saleRegister.sort();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Selection " + chartCount);
            chartCount++;
            for (Map.Entry<String, Integer> entry : sumOfSales.entrySet()) {
                String tmpString = entry.getKey();
                Number tmpValue = entry.getValue();
                XYChart.Data<String, Number> newData = new XYChart.Data<>(tmpString, tmpValue);
                series.getData().add(newData);
            }
            barChart.getData().add(series);
        } );
    }
}
