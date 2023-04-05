package ATM.view.controllers;


import ATM.Entities.Transactions.Transaction;
import ATM.utils.MoneyWithdrawValidator;
import ATM.view.callbackComms.BooleanReasonResponse;
import ATM.view.callbackComms.UserAmountRequest;
import ATM.view.events.*;
import ATM.utils.MoneyDepositValidator;
import ATM.view.windows.Windows;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

public class WithdrawalRequestController extends AtmSceneControllerBase{


    @FXML
    private TextArea amount_field;

    @FXML
    private Button deposit_button;

    @FXML
    private Button cancel_button;

    @FXML
    private void onDepositClick() {
        String amount = amount_field.getText();
        System.out.println("Read deposit amount as " + amount);

        MoneyWithdrawValidator moneyValidator = new MoneyWithdrawValidator(amount);

        if (moneyValidator.isValid()) {
            float value = moneyValidator.getAmount();
            BooleanReasonResponse response = (BooleanReasonResponse)
                    emit(new TransactionEvent(Transaction.TransactionType.WITHDRAWAl), new UserAmountRequest(value));

            if (!response.isValid()) {
                emit(new SystemChangeEvent(SystemChange.OPEN_ERROR, response.getReason()));
            }
        }
        else {
            emit(new SystemChangeEvent(SystemChange.OPEN_ERROR, moneyValidator.getReason()));
        }

        amount_field.setText("");
    }

    @FXML
    private void onCancelClick() {
        emit(new SceneChangeEvent(Windows.TRANSACTION_SCREEN));
    }

    @Override
    public void onTickCallback(Duration interval) {

    }
}
