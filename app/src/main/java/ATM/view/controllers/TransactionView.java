package ATM.view.controllers;


import ATM.Entities.Transactions.Balance;
import ATM.Entities.Transactions.Transaction;
import ATM.Entities.Transactions.Withdrawal;
import ATM.view.entity.ViewInterface;
import ATM.view.events.SceneChangeEvent;
import ATM.view.windows.Windows;
import com.gluonhq.charm.glisten.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionView extends AtmSceneControllerBase implements ViewInterface<Transaction> {

    private static final String FONT_NAME = "Courier";
    private static final double FONT_SIZE = 13;

    @FXML
    private TextField transaction_label;

    @FXML
    private Text summary_text;

    @FXML
    private Button close_button;

    @FXML
    private void onCancelClick() {
        emit(new SceneChangeEvent(Windows.TRANSACTION_SCREEN));
    }

    public TransactionView() {
        transaction_label = new TextField();
        summary_text = new Text();
    }

    @Override
    public void onTickCallback(Duration interval) {

    }


    @Override
    public void makeFromEntity(Transaction entity) {
        String type = entity.getTypeStr();
        String summary = getSummary(entity);
        this.summary_text.setText(summary);

        transaction_label.setText(type);



//        this.textArea.setText(entity.text);
    }

    @Override
    public void onClose() {

    }


    private String getSummary(Transaction transaction) {
        double balance;
        if (transaction.isUserTransactionATM()) {
            balance = transaction.getAtmAccount().getBalance();
        }
        else {
            balance = transaction.getUserAccount().getBalance();
        }
        return String.format("" +
                " *\ttransaction ID: %s\n" +
                " *\tTransaction Amount: %.2f\n" +
                " *\tTransaction Type: %s\n" +
                " *\tAccount Balance: %.2f\n", transaction.getTransactionId(), transaction.getQuantity(),
                                             transaction.getTypeStr(), balance);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO: this doesn't seem to work with custom fonts?
        summary_text.setFont(Font.font(FONT_NAME, FontPosture.REGULAR, FONT_SIZE));
    }
}
