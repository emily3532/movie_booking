package ATM.view.controllers;

import ATM.view.entity.ViewInterface;
import ATM.view.events.SystemChange;
import ATM.view.events.SystemChangeEvent;
import com.gluonhq.charm.glisten.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class ErrorSceneController extends AtmSceneControllerBase implements ViewInterface<String> {


    @FXML
    private Label error_message_field;

    @FXML
    private Button ok_button;


    @FXML
    private void onOkClick() {
        this.emit(new SystemChangeEvent(SystemChange.CLOSE_ERROR));
    }


    public ErrorSceneController() {
        error_message_field = new Label();

    }

    @Override
    public void onTickCallback(Duration interval) {

    }

    @Override
    public void makeFromEntity(String entity) {
        error_message_field.setText(entity);
        error_message_field.setWrapText(true);
        error_message_field.setTextAlignment(TextAlignment.CENTER);
//        error_message_field.setWrapText(true);
//        error_message_field.
    }

    @Override
    public void onClose() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        error_message_field.setDisable(true);
    }
}
