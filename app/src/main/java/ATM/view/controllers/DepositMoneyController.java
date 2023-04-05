package ATM.view.controllers;


import ATM.Entities.Transactions.Transaction;
import ATM.view.callbackComms.BooleanReasonResponse;
import ATM.view.callbackComms.UserAmountRequest;
import ATM.view.events.*;
import ATM.view.windows.Windows;
import ATM.utils.MoneyDepositValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Duration;


public class DepositMoneyController extends AtmSceneControllerBase {

    @FXML
    private Button ok_button;

    @FXML
    private Button cancel_button;

    @FXML
    private TextField deposit_amount_field;


    @FXML
    private void onOkClick() {
        String amount = deposit_amount_field.getText();
        System.out.println("Read deposit amount as " + amount);

        MoneyDepositValidator moneyValidator = new MoneyDepositValidator(amount);

        if (moneyValidator.isValid()) {
            float value = moneyValidator.getAmount();
            BooleanReasonResponse response = (BooleanReasonResponse)
                    emit(new TransactionEvent(Transaction.TransactionType.DEPOSIT), new UserAmountRequest(value));

            if(!response.isValid()) {
                emit(new SystemChangeEvent(SystemChange.OPEN_ERROR, response.getReason()));
            }
        }
        else {
            emit(new SystemChangeEvent(SystemChange.OPEN_ERROR, moneyValidator.getReason()));
        }

    }

    @FXML
    private void onCancelClick() {
        deposit_amount_field.setText("0.00");
        emit(new SceneChangeEvent(Windows.TRANSACTION_SCREEN));
    }


    public DepositMoneyController() {
        deposit_amount_field = new TextField();
        deposit_amount_field.setEditable(true);

    }

    @Override
    public void onTickCallback(Duration interval) {

    }

}
