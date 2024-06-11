package frontend.events;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/** An event fired when customer stage has been loaded **/
public class CustomerStageEvent extends ApplicationEvent {

    public CustomerStageEvent(Object source) {
        super(source);
    }

    public Stage getStage() {
        return (Stage) getSource();
    }
}
