package frontend.events;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/** An event fired when marketing stage has been loaded **/
public class MarketingStageEvent extends ApplicationEvent {

    public MarketingStageEvent(Object source) {
        super(source);
    }

    public Stage getStage() {
        return (Stage) getSource();
    }
}
