package ATM.view.windows;

import ATM.view.controllers.AtmSceneControllerBase;
import javafx.scene.Parent;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class ChangeWindow extends WindowType {


    public ChangeWindow(Stage primaryStage, AtmSceneControllerBase controllerBase, Windows windows) {
        super(primaryStage, controllerBase, windows);

    }

    @Override
    public void close() {
        //kinda doesnt do anything as the primaryStage should be closed form the AtmWindow
    }

    @Override
    public void open() {
        Parent root = getLoader().getRoot();
        primaryStage.getScene().setRoot(root);
    }
}
