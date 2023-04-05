package ATM.view.controllers;

import ATM.Entities.Accounts.AccountType;
import ATM.Entities.Transactions.Transaction;
import ATM.view.events.*;
import ATM.view.windows.Windows;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionChoiceController extends AtmSceneControllerBase implements Initializable {

    @FXML
    private Button withdraw_money_button;

    @FXML
    private Button deposit_money_button;

    @FXML
    private Button check_balance_button;

    @FXML
    private Button cancel_button_t_choice;


    @FXML
    private void onWithdrawMoneyClick() {
        emit(new SceneChangeEvent(Windows.WITHDRAW_MONEY));
    }

    @FXML
    private void onDepositMoneyClick() {
        emit(new SceneChangeEvent(Windows.DEPOSIT_MONEY));
    }

    @FXML
    private void onCheckBalanceClick() {
        emit(new TransactionEvent(Transaction.TransactionType.BALANCE));
    }

    @FXML
    private void onTChoiceCancelClick() {
        emit(new SceneChangeEvent(Windows.CARD_OUT));
    }

    private AccountType accountType;

    public TransactionChoiceController() {

    }

    public void setAccountType(AccountType accountType) {
        assert accountType != AccountType.NOT_SET;
        this.accountType = accountType;

        if (this.accountType == AccountType.ADMIN) {
            this.withdraw_money_button.setDisable(true);
        }
    }

    @Override
    public void onTickCallback(Duration interval) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.withdraw_money_button.setDisable(false);
    }
}
