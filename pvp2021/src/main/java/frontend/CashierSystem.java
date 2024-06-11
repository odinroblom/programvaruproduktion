package frontend;

import backend.SpringBootApp;
import backend.managers.PoS;
import frontend.events.CashierStageEvent;
import frontend.events.CustomerStageEvent;
import frontend.events.MarketingStageEvent;
import frontend.events.SalesStageEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Class for creating and connecting the various stages to the SpringBoot application instance.
 * It enables JavaFX controllers to autowire spring beans
 * It also enables other spring components to autowire JavaFX controllers as beans.
 **/
public class CashierSystem extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder( SpringBootApp.class ).run();
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent( new CashierStageEvent( stage ) );
        Stage customerStage = new Stage();
        applicationContext.publishEvent( new CustomerStageEvent( customerStage ) );
        Stage marketingStage = new Stage();
        applicationContext.publishEvent( new MarketingStageEvent( marketingStage ) );
        Stage salesStage = new Stage();
        applicationContext.publishEvent(new SalesStageEvent( salesStage ) );
    }

    @Override
    public void stop() {
        applicationContext.getBean( PoS.class ).terminateExecutorService();
        applicationContext.close();
        Platform.exit();
    }
}
