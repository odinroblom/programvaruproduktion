package frontend.events;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/** An event fired when sales stage has been loaded **/
public class SalesStageEvent extends ApplicationEvent {

    public SalesStageEvent(Object source) {
        super(source);
    }

    public Stage getStage() {
        return (Stage) getSource();
    }
}
