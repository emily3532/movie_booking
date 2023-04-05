package ATM.view.controllers;

import ATM.view.AtmWindow;
import ATM.view.events.SystemChange;
import ATM.view.events.SystemChangeEvent;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class CardEjectedMessageController extends AtmSceneControllerBase implements Initializable {

    private static final double TIME_TO_WAIT_IN_SECONDS = 2;

    private double timeWaited;

    @FXML
    private Button ok_button;

    @FXML
    private ProgressBar progress_bar;

    @FXML
    private Text text_message;

    @FXML
    private void onOkClick() {
        emit(new SystemChangeEvent(SystemChange.SHUTDOWN));
    }

    public CardEjectedMessageController() {

    }

    @Override
    public void onTickCallback(Duration interval) {
        timeWaited += interval.toSeconds();
        timeWaited = Math.min(TIME_TO_WAIT_IN_SECONDS, timeWaited);
        if (timeWaited >= TIME_TO_WAIT_IN_SECONDS) {
            ok_button.setDisable(false);
            text_message.setText("Please take your card...");
            progress_bar.setStyle("-fx-accent: green;");
        }
        progress_bar.setProgress(timeWaited/TIME_TO_WAIT_IN_SECONDS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ok_button.setDisable(true);

    }
}
