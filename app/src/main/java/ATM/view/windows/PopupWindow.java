package ATM.view.windows;

import ATM.view.controllers.AtmSceneControllerBase;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class PopupWindow extends WindowType {

    private final Stage popup;
    private final Scene scene;

    public PopupWindow(Stage primaryStage, AtmSceneControllerBase controllerBase, Windows windows) {
        super(primaryStage, controllerBase, windows);

        this.popup = new Stage();
        this.popup.initModality(Modality.APPLICATION_MODAL);

        Parent root = getLoader().getRoot();
        scene= new Scene(root);
    }


    @Override
    public void close() {
        getController().onClose();
        this.popup.close();
    }

    @Override
    public void open() {

        this.popup.setScene(scene);
        getController().onOpen();
        this.popup.show();

    }
}
