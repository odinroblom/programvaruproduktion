package frontend.events;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/** An event fired when cashier stage has been loaded **/
public class CashierStageEvent extends ApplicationEvent {

    public CashierStageEvent(Object source) {
        super(source);
    }

    public Stage getStage() {
        return (Stage) getSource();
    }
}
