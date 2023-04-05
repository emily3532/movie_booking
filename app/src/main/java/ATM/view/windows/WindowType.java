package ATM.view.windows;

import ATM.view.controllers.AtmSceneControllerBase;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public abstract class WindowType implements WindowTypeInterface {

    protected Windows type;
    protected Stage primaryStage;
    protected AtmSceneControllerBase controllerBase;
    protected WindowState windowState;

    public WindowType(Stage primaryStage, AtmSceneControllerBase controllerBase, Windows type) {
        this.primaryStage = primaryStage;
        this.controllerBase = controllerBase;
        this.windowState = WindowState.CLOSE;
        this.type = type;
    }

    public AtmSceneControllerBase getController() {
        return controllerBase;
    }

    public FXMLLoader getLoader() {
        return controllerBase.getLoader();
    }

    public Windows getType() {
        return type;
    }
}
