package ATM.view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoadingIndicatorController extends AtmSceneControllerBase implements Initializable {


    @FXML
    ProgressIndicator progress_indicator;


    public LoadingIndicatorController() {

    }

    @Override
    public void onTickCallback(Duration interval) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
