package frontend.stageInitializer;

import frontend.events.CashierStageEvent;
import frontend.events.CustomerStageEvent;
import frontend.events.MarketingStageEvent;
import frontend.events.SalesStageEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Class for initializing all the 4 different stages that appear at startup. **/
@Component
public class StageInitializer {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    public StageInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener
    public void handleCashierStageEvent( CashierStageEvent event ) {
        Stage stage = event.getStage();
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/CashierView.fxml") );
            loader.setControllerFactory( clazz -> applicationContext.getBean( clazz ) );
            Parent root = loader.load();
            stage.setTitle("Cashier System");
            stage.setScene(new Scene(root, 933, 606));
            stage.setX(20);
            stage.setY(20);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventListener
    public void handleCustomerStageEvent( CustomerStageEvent event ) {
        Stage stage = event.getStage();
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/CustomerWindow.fxml") );
            loader.setControllerFactory( clazz -> applicationContext.getBean( clazz ) );
            Parent root = loader.load();
            stage.setTitle("Customer Screen");
            stage.setScene(new Scene(root, 439, 305));
            stage.setX(950);
            stage.setY(20);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventListener
    public void handleMarketingStageEvent( MarketingStageEvent event ) {
        Stage stage = event.getStage();
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/MarketingWindow.fxml") );
            loader.setControllerFactory( clazz -> applicationContext.getBean( clazz ) );
            Parent root = loader.load();
            stage.setTitle("Marketing panel");
            stage.setScene(new Scene(root, 1368, 505));
            stage.setX(1200);
            stage.setY(600);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventListener
    public void handleSalesStageEvent( SalesStageEvent event ) {
        Stage stage = event.getStage();
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/SalesWindow.fxml") );
            loader.setControllerFactory( clazz -> applicationContext.getBean( clazz ) );
            Parent root = loader.load();
            stage.setTitle("Sales panel");
            stage.setScene(new Scene(root, 622, 503));
            stage.setX(200);
            stage.setY(600);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
