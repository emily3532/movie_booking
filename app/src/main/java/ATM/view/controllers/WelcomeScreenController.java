package ATM.view.controllers;

import ATM.Entities.Accounts.Account;
import ATM.Entities.Accounts.AccountType;
import ATM.view.callbackComms.BooleanReasonResponse;
import ATM.view.callbackComms.UserPairRequest;
import ATM.view.events.*;
import ATM.utils.CardFormatValidator;
import ATM.utils.PinNumberFormatValidator;
import ATM.view.windows.Windows;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import org.checkerframework.checker.units.qual.A;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class WelcomeScreenController extends AtmSceneControllerBase implements Initializable {


    @FXML
    private Button keypad_1;

    @FXML
    private Button keypad_2;

    @FXML
    private Button keypad_3;

    @FXML
    private Button keypad_4;

    @FXML
    private Button keypad_5;

    @FXML
    private Button keypad_6;

    @FXML
    private Button keypad_7;

    @FXML
    private Button keypad_8;

    @FXML
    private Button keypad_9;

    @FXML
    private Button keypad_0;

    @FXML
    private Button password_ok_button;

    @FXML
    private Button password_cancel_button;

    @FXML
    private TextField card_text_field;


    @FXML
    private TextField password_text_display;

    @FXML
    private Button validate_card_button;

    @FXML
    private Button admin_login_button;

    @FXML
    private void onKeypad1Click() {
        addTextToPassword("1");
    }

    @FXML
    private void onKeypad2Click() {
        addTextToPassword("2");
    }

    @FXML
    private void onKeypad3Click() {
        addTextToPassword("3");
    }

    @FXML
    private void onKeypad4Click() {
        addTextToPassword("4");
    }

    @FXML
    private void onKeypad5Click() {
        addTextToPassword("5");
    }

    @FXML
    private void onKeypad6Click() {
        addTextToPassword("6");
    }

    @FXML
    private void onKeypad7Click() {
        addTextToPassword("7");
    }

    @FXML
    private void onKeypad8Click() {
        addTextToPassword("8");
    }

    @FXML
    private void onKeypad9Click() {
        addTextToPassword("9");
    }

    @FXML
    private void onKeypad0Click() {
        addTextToPassword("0");}


    @FXML
    private void onPasswordOkClick() {

        PinNumberFormatValidator pinNumberFormatValidator = new PinNumberFormatValidator(enteredPassword);
//        System.out.println(pinNumberFormatValidator.getReason());
        if (pinNumberFormatValidator.isValid() && cardRequestType != null) {
            BooleanReasonResponse response = (BooleanReasonResponse) emit(new CardRequestEvent(cardRequestType),
                    new UserPairRequest(enteredPassword, enteredCardNumber, true));
            System.out.println(response);
            //card/pin number was invalid for some reason
            if(!response.isValid()) {
                emit(new SystemChangeEvent(SystemChange.OPEN_ERROR, response.getReason()));
                setButtonsDisable(true);
            }
            else {
                //everythign was good so we so we go to transution choice screen
                emit(new SceneChangeEvent(Windows.TRANSACTION_SCREEN));
            }

        }
        else {
            emit(new SystemChangeEvent(SystemChange.OPEN_ERROR, pinNumberFormatValidator.getReason()));
            setButtonsDisable(true);

        }

        card_text_field.setDisable(false);
        validate_card_button.setDisable(false);
        admin_login_button.setDisable(false);
        clearPassword();
        cardRequestType = null;
    }

    @FXML
    private void onPasswordCancelClick() {
        clearPassword();
        clearCardNumber();
        setButtonsDisable(true);
        card_text_field.setDisable(false);
        validate_card_button.setDisable(false);
        admin_login_button.setDisable(false);
        cardRequestType = null;
    }


    @FXML
    void onValidateCardClick() {
        CardFormatValidator cardFormatValidator = new CardFormatValidator(card_text_field.getText());
        if (cardFormatValidator.isValid()) {
            setButtonsDisable(false);
            admin_login_button.setDisable(true);
            cardRequestType = CardRequests.VALIDATE_USER;
        }
        else {
            setButtonsDisable(true);
            emit(new SystemChangeEvent(SystemChange.OPEN_ERROR, cardFormatValidator.getReason()));
            clearCardNumber();
        }
    }

    @FXML
    private void onAdminLoginClick() {
        setButtonsDisable(false);
        //also disable cardtextfield
        card_text_field.setDisable(true);
        validate_card_button.setDisable(true);

        enteredCardNumber = Account.ATM_ACCOUNT_CARDNUM;
        cardRequestType = CardRequests.VALIDATE_ADMIN;
    }

    private String enteredPassword;
    private String enteredCardNumber;
    private CardRequests cardRequestType;

    public WelcomeScreenController() {
        cardRequestType = null;
        password_text_display = new TextField();
        password_text_display.setEditable(false);

        card_text_field = new TextField();
        card_text_field.setEditable(false);

        enteredPassword = "";
        enteredCardNumber = "";

        setupButtons();

    }

    @Override
    public void onTickCallback(Duration interval) {

    }

    /**
     * Appends the users entered text to the interfaces password key.
     * @param s The character entered by the user on the keypad
     */
    private void addTextToPassword(String s) {
        enteredPassword += s;
        displayEnteredPassword();
    }

    /**
     * Appends the input keystroke to the entered number and displays it on the screen.
     * @param key KeyEvent
     */
    private void addTextToCardNumber(KeyEvent key) {
        if (key.getCode() == KeyCode.BACK_SPACE) {
            enteredCardNumber = removeLastCharacter(enteredCardNumber);
        }
        else {
            enteredCardNumber += key.getText();
        }
        displayEnteredCardNumber();
    }

    /**
     * Displays the user's password as it is entered as a series of "*"
     * TODO: doesnt do that * yet
     */
    private void displayEnteredPassword() {
        password_text_display.setText(Objects.requireNonNullElse(enteredPassword, ""));

    }

    /**
     * Displays the card number as it is entered
     */
    private void displayEnteredCardNumber() {
        if(enteredCardNumber == null) {
            card_text_field.setText("");
        }
        else {
            card_text_field.setText(enteredCardNumber);
        }
    }

    /**
     * Clears the user input for their password pdates the display
     */
    private void clearPassword() {
        enteredPassword = "";
        displayEnteredPassword();
    }

    /**
     * Clears the users input for their card name and updates the display
     */
    private void clearCardNumber() {
        enteredCardNumber = "";
        displayEnteredCardNumber();
    }


    /**
     * Removes the last character of a string and returns the new String. Returns empty string if thr string is
     * already empty.
     * @param s String
     * @return String
     */
    private String removeLastCharacter(String s) {
        if(s.length() > 0) {
            return s.substring(0,
                    s.length() - 1);
        }
        return s;

    }

    /**
     * Sets up all the buttons of the keypad
     */
    private void setupButtons() {
        this.keypad_0 = new Button();
        this.keypad_1 = new Button();
        this.keypad_2 = new Button();
        this.keypad_3 = new Button();
        this.keypad_4 = new Button();
        this.keypad_5 = new Button();
        this.keypad_6 = new Button();
        this.keypad_7 = new Button();
        this.keypad_8 = new Button();
        this.keypad_9 = new Button();
        this.admin_login_button = new Button();
        this.validate_card_button = new Button();
    }

    /**
     * Emables/disables all the buttons of the keypad and the text display
     * @param disable boolean
     */
    private void setButtonsDisable(boolean disable) {
        this.keypad_0.setDisable(disable);
        this.keypad_1.setDisable(disable);
        this.keypad_2.setDisable(disable);
        this.keypad_3.setDisable(disable);
        this.keypad_4.setDisable(disable);
        this.keypad_5.setDisable(disable);
        this.keypad_6.setDisable(disable);
        this.keypad_7.setDisable(disable);
        this.keypad_8.setDisable(disable);
        this.keypad_9.setDisable(disable);
        this.password_text_display.setDisable(disable);
    }


    /**
     * Initalizer function for this controller (overwritten from javafx.Initalizer.
     *
     * Sets the buttons of disabled and sets up a KeyEvent to trigger on key releases so we can track
     * backspaces.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonsDisable(true);

        this.card_text_field.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                addTextToCardNumber(event);
            }
        });
    }
}
