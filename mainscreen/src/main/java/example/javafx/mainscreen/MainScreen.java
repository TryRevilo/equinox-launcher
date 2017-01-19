package example.javafx.mainscreen;

import example.javafx.ui.AppScreen;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.apache.felix.dm.annotation.api.Component;

@Component
public class MainScreen implements AppScreen {

    @Override
    public String getName() {
        return "Main";
    }

    @Override
    public Node getContent() {
        return new Label("Main screen");
    }

    @Override
    public int getPosition() {
        return 0;
    }

}
