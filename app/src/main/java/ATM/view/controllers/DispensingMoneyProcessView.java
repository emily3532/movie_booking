package ATM.view.controllers;

import ATM.utils.CoinChanger;
import ATM.view.entity.ViewInterface;
import ATM.view.events.SceneChangeEvent;
import ATM.view.windows.Windows;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class DispensingMoneyProcessView extends AtmSceneControllerBase implements ViewInterface<Float> {

    private static final double TIME_TO_WAIT_IN_SECONDS = 10;

    @FXML
    private Button ok_button;

    @FXML
    private ProgressBar progress_bar;

    @FXML
    private Text text_message;

    @FXML
    private Text money_out_text;

    @FXML
    private void onOkClick() {
//        emit(new SystemChangeEvent(SystemChange.SHUTDOWN));
        emit(new SceneChangeEvent(Windows.TRANSACTION));
    }

    private double amountDeposited;
    ArrayList<Double> coinMapKeys;
    ArrayList<Double> coinMapValues;
    private double intervalToWait;
    private double timeWaited;
    private String output;
    private int index;
    private boolean isWritten;


    public DispensingMoneyProcessView() {
        coinMapKeys = new ArrayList<>();
        coinMapValues = new ArrayList<>();
        isWritten = false;
        output = "";
        amountDeposited = 0;
        timeWaited = 0;
        index = 0;

    }

    @Override
    public void onTickCallback(Duration interval) {
//        assert coinMapValues != null;
//        assert coinMapKeys != null;
//        System.out.println("size " + coinMapKeys.size());
//        System.out.println("Index " + index);
//        int size
        timeWaited += interval.toSeconds();
//        timeWaited += interval.toSeconds();
//        timeWaited = Math.min(TIME_TO_WAIT_IN_SECONDS, timeWaited);
//        if (timeWaited >= TIME_TO_WAIT_IN_SECONDS) {
//            ok_button.setDisable(false);
////            text_message.setText("Please take your card...");
//        }
//        progress_bar.setProgress(timeWaited/TIME_TO_WAIT_IN_SECONDS);
//        System.out.println(timeWaited);

        if(timeWaited >= intervalToWait && index < coinMapKeys.size()) {
            System.out.println(interval.toSeconds());
            double moneyValue = coinMapKeys.get(index);
            double number = coinMapValues.get(index);
            this.amountDeposited += moneyValue * number;
            index++;
            double progess = index * 1.0/coinMapKeys.size();
            output += String.format("%s x %d = %.2f\n", formatMoney(moneyValue), (int)number, this.amountDeposited);
            money_out_text.setText(output);
            System.out.println(progess);
            System.out.println(moneyValue);
            System.out.println(number);

            progress_bar.setProgress(progess);
            timeWaited = 0;
        }
        if(index >= coinMapKeys.size() && !isWritten) {
            output += String.format("Dispensed total of $ %.2f\n", this.amountDeposited);
            progress_bar.setStyle("-fx-accent: green;");
            money_out_text.setText(output);
            progress_bar.setProgress(1);
            ok_button.setDisable(false);
            isWritten = true;
        }
//
    }

    @Override
    public void makeFromEntity(Float amountDeposited) {
//        this.amountDeposited = amountDeposited;
        sortByKey(CoinChanger.findCoinAndNotes(amountDeposited));
        intervalToWait = TIME_TO_WAIT_IN_SECONDS/coinMapValues.size();
        System.out.println("Interval to wait " + intervalToWait);

    }

    @Override
    public void onClose() {
        coinMapKeys = new ArrayList<>();
        coinMapValues = new ArrayList<>();
        isWritten = false;
        output = "";
        amountDeposited = 0;
        timeWaited = 0;
        index = 0;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ok_button.setDisable(true);
        text_message.setText("Dispensing money...");
        timeWaited = 0;
    }

    private void sortByKey(HashMap<Double, Double> coinMap) {
        assert coinMap != null;
        Map<Double, Double> reverseSortedMap = new TreeMap<Double, Double>(Collections.reverseOrder());
        reverseSortedMap.putAll(coinMap);

        for(Map.Entry<Double, Double> entry : reverseSortedMap.entrySet()) {
            coinMapKeys.add(entry.getKey());
            coinMapValues.add(entry.getValue());
        }

    }

    private String formatMoney(double amount) {
        if (amount >= 1) {
            return String.format("$ %d", (int)amount);
        }
        else {
            return String.format("%.2fc", amount);
        }
    }
}
