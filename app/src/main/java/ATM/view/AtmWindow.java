package ATM.view;

import ATM.Entities.Accounts.ATMAccount;
import ATM.Entities.Accounts.Account;
import ATM.Entities.Accounts.AccountType;
import ATM.Entities.Accounts.UserAccount;
import ATM.Entities.Card;
import ATM.Entities.Transactions.Balance;
import ATM.Entities.Transactions.Deposit;
import ATM.Entities.Transactions.Transaction;
import ATM.Entities.Transactions.Withdrawal;
import ATM.utils.ATMException;
import ATM.view.callbackComms.*;
import ATM.view.controllers.*;
import ATM.view.controllers.TransactionView;
import ATM.view.events.*;
import ATM.view.events.SystemChange;
import ATM.view.windows.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static ATM.Entities.Accounts.Account.ATM_ACCOUNT_ID;

public class AtmWindow {

    EventCallbackMap eventCallbackMap;
    
    private final Stage primaryStage;
    private final Timeline timeline;
    private final ATMAccount atmAccount;
    private UserAccount userAccount;
    private Card card;
    private AccountType accountType;
    private Transaction recentTransaction;

    private final WindowsManager windowsManager;
    private static final Duration TICK_TIME = Duration.millis(20);
    
    public AtmWindow(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.atmAccount = Account.retrieveAtmAccount(ATM_ACCOUNT_ID);
        this.accountType = AccountType.NOT_SET;
        this.windowsManager = new WindowsManager(this.primaryStage);
        this.eventCallbackMap = new EventCallbackMap(this.windowsManager);
        this.setupControllers();


        //sets the root node to be the starting screen
        WindowType type = this.windowsManager.getWindow(Windows.WELCOME_SCREEN);
        windowsManager.setCurrentWindow(type);
        FXMLLoader loader = type.getLoader();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);


        timeline = new Timeline(new KeyFrame(TICK_TIME,
                t -> this.tick()));

        timeline.setCycleCount(Timeline.INDEFINITE);

        eventCallbackMap.registerCallback(new TransactionEvent(Transaction.TransactionType.DEPOSIT),
                BooleanReasonResponse.class,
                UserAmountRequest.class,
                (event, userRequest) -> {
            WindowType windowType = this.windowsManager.getWindow(Windows.TRANSACTION);
            TransactionView controller = (TransactionView) windowType.getController();

            UserAmountRequest userAmountRequest = (UserAmountRequest) userRequest;

            Deposit deposit = new Deposit(userAccount, atmAccount, userAmountRequest.getData());
            try {
                if (!deposit.attempt()) {
                    ///shouldn't ever be false as this should now only throw exception
                    return new BooleanReasonResponse(false, "Deposit attempt failed");
                } else {
                    controller.makeFromEntity(deposit);
                    this.windowsManager.changeWindow(windowType);
                    return new BooleanReasonResponse(true, "");
                }
            }
            catch (ATMException e) {
                return new BooleanReasonResponse(false, e.getMessage());
            }
        });

        eventCallbackMap.registerCallback(new SceneChangeEvent(Windows.TRANSACTION), ((event, request) -> {
            WindowType windowType = this.windowsManager.getWindow(Windows.TRANSACTION);
            TransactionView controller = (TransactionView) windowType.getController();
            //totally unchecked behaviour on this as recent transaction will now just be the lastest one
            controller.makeFromEntity(recentTransaction);
            this.windowsManager.changeWindow(windowType);
            return null;
        }));

        eventCallbackMap.registerCallback(new TransactionEvent(Transaction.TransactionType.WITHDRAWAl),
                BooleanReasonResponse.class,
                UserAmountRequest.class,
                (event, userRequest) -> {
//            WindowType windowType = this.windowsManager.getWindow(Windows.TRANSACTION);
//            TransactionView controller = (TransactionView) windowType.getController();
            WindowType windowType = this.windowsManager.getWindow(Windows.DISPENSING_CASH);
            DispensingMoneyProcessView controller = (DispensingMoneyProcessView) windowType.getController();


            UserAmountRequest userAmountRequest = (UserAmountRequest)userRequest;
            if(!userAmountRequest.isValid()) {
                return new BooleanReasonResponse(false,"");
            }

            System.out.println(userAmountRequest.getData());
            recentTransaction = new Withdrawal(userAccount, atmAccount, userAmountRequest.getData());
            try {
                boolean result = recentTransaction.attempt();
                if (!result) {
                    return new BooleanReasonResponse(false, "Withdrawal attempt failed");
                } else {
//                    controller.makeFromEntity(withdrawal);
                    controller.makeFromEntity(userAmountRequest.getData());
                    this.windowsManager.changeWindow(windowType);
                    return new BooleanReasonResponse(true,"");
                }
            }
            catch (ATMException e) {
                return new BooleanReasonResponse(false, e.getMessage());
            }
            //make fake Transaction for testing
//            Withdrawal withdrawl = new Withdrawal(new UserAccount("10", 0), new ATMAccount("11", 0), amount);
//            Deposit deposit = new Deposit(new UserAccount(10), new ATMAccount(11), amount, "key");
        });

        eventCallbackMap.registerCallback(new TransactionEvent(Transaction.TransactionType.BALANCE),
                BooleanResponse.class, (event, request) -> {

                WindowType windowType = this.windowsManager.getWindow(Windows.TRANSACTION);
                TransactionView controller = (TransactionView) windowType.getController();
                recentTransaction = new Balance(userAccount, atmAccount, 0);
                try {
                    if (!recentTransaction.attempt()) {
                        return new BooleanReasonResponse(false, "Check balance failed");
                    } else {
                        controller.makeFromEntity(recentTransaction);
                        this.windowsManager.changeWindow(windowType);
                        return new BooleanResponse(true);
                    }
                }
                catch (ATMException e) {
                    return new BooleanReasonResponse(false, e.getMessage());
                }

        });

        eventCallbackMap.registerCallback(new SystemChangeEvent(SystemChange.OPEN_ERROR), BooleanResponse.class,
                (event, userRequest) -> {
            WindowType windowType = this.windowsManager.getWindow(Windows.ERROR);
            ErrorSceneController controller = (ErrorSceneController) windowType.getController();

            SystemChangeEvent systemEvent = (SystemChangeEvent) event;
            if (systemEvent.hasReason()) {
                controller.makeFromEntity(systemEvent.getReason());
            }
            else {
                controller.makeFromEntity("An error");
            }

            this.windowsManager.changeWindow(windowType);

            return new BooleanResponse(true);
        });

        eventCallbackMap.registerCallback(new SystemChangeEvent(SystemChange.CLOSE_ERROR), BooleanResponse.class,
                (event, userRequest) -> {
            if (this.windowsManager.getCurrentWindow().getType() == Windows.ERROR) {
                this.windowsManager.getCurrentWindow().close();
                return new BooleanResponse(true);
            }
            return new BooleanResponse(false);
        });

//        eventCallbackMap.registerCallback(new SystemChangeEvent(SystemChange.LOADING_OPEN), BooleanResponse.class,
//                (event, userRequest) -> openLoadingScreen());
//
//        eventCallbackMap.registerCallback(new SystemChangeEvent(SystemChange.LOADING_CLOSE), BooleanResponse.class,
//                (event, userRequest) -> closeLoadingScreen());


        eventCallbackMap.registerCallback(new SystemChangeEvent(SystemChange.SHUTDOWN), ((event, request) -> {
            timeline.stop();;
            this.windowsManager.getCurrentWindow().close();
            this.primaryStage.close();
            return null;
        }));

        eventCallbackMap.registerCallback(new CardRequestEvent(CardRequests.VALIDATE_USER),
                BooleanReasonResponse.class,
                UserPairRequest.class, (event, userRequest) -> {

            //cannot set a card twice
            assert this.accountType == AccountType.NOT_SET;
            assert userRequest instanceof UserPairRequest;
            UserPairRequest request = (UserPairRequest)userRequest;

//
            String pinNumber = request.getData().getKey();
            String cardNumber = request.getData().getValue();

            return acquireCardOnRequest(pinNumber, cardNumber, AccountType.USER);


        });

        eventCallbackMap.registerCallback(new CardRequestEvent(CardRequests.VALIDATE_ADMIN),
                BooleanReasonResponse.class,
                UserPairRequest.class, (event, userRequest) -> {

                    //cannot set a card twice
                    assert this.accountType == AccountType.NOT_SET;
                    assert userRequest instanceof UserPairRequest;
                    UserPairRequest request = (UserPairRequest)userRequest;

//
                    String pinNumber = request.getData().getKey();
                    String cardNumber = request.getData().getValue();
                    assert cardNumber.equals(Account.ATM_ACCOUNT_CARDNUM);
                    BooleanReasonResponse response = acquireCardOnRequest(pinNumber, Account.ATM_ACCOUNT_CARDNUM,
                            AccountType.ADMIN);

                    WindowType windowType = this.windowsManager.getWindow(Windows.TRANSACTION_SCREEN);
                    TransactionChoiceController controller = (TransactionChoiceController) windowType.getController();
                    controller.setAccountType(AccountType.ADMIN);
                    return response;

                });

    }

    /**
     * Runs the primary stage. Should be called in main
     */
    public void run() {
        timeline.play();
        primaryStage.show();
    }


    /**
     * Callback function invoked when a SystemChangeEvent is called. Used to handle shutdowns and user-facing
     * graphical exceptions
     *
     * @param system SystemChange enum
     */
    private void systemChangeCallback(SystemChange system) {
        if (system == SystemChange.SHUTDOWN) {
            this.timeline.stop();
            this.primaryStage.close();
        }
    }

    private BooleanResponse openLoadingScreen() {
        System.out.println("Opening loading screen");
        WindowType windowType = this.windowsManager.getWindow(Windows.LOADING);
        this.windowsManager.changeWindow(windowType);

        return new BooleanResponse(true);
    }

    private BooleanResponse closeLoadingScreen() {
        if (this.windowsManager.getCurrentWindow().getType() == Windows.LOADING) {
            this.windowsManager.getCurrentWindow().close();
            return new BooleanResponse(true);
        }
        return new BooleanResponse(false);
    }

    private void setupControllers() throws FileNotFoundException{
        this.windowsManager.addWindow(Windows.WELCOME_SCREEN,
                getURLResourceFromFileName("/WelcomeScreen.fxml"),
                new WelcomeScreenController(),
                ChangeWindow.class);
        this.windowsManager.addWindow(Windows.TRANSACTION_SCREEN,
                getURLResourceFromFileName("/TransactionChoiceScreen.fxml"),
                new TransactionChoiceController(),
                ChangeWindow.class);
        this.windowsManager.addWindow(Windows.DEPOSIT_MONEY,
                getURLResourceFromFileName("/DepositMoneyScreen.fxml"),
                new DepositMoneyController(),
                ChangeWindow.class);
        this.windowsManager.addWindow(Windows.TRANSACTION,
                getURLResourceFromFileName("/Transaction.fxml"),
                new TransactionView(),
                PopupWindow.class);

        this.windowsManager.addWindow(Windows.WITHDRAW_MONEY,
                getURLResourceFromFileName("/WithdrawRequest.fxml"),
                new WithdrawalRequestController(),
                PopupWindow.class);

        this.windowsManager.addWindow(Windows.ERROR,
                getURLResourceFromFileName("/ErrorMessage.fxml"),
                new ErrorSceneController(),
                PopupWindow.class);

        this.windowsManager.addWindow(Windows.CARD_OUT,
                getURLResourceFromFileName("/CardOutMessage.fxml"),
                new CardEjectedMessageController(),
                PopupWindow.class);

        this.windowsManager.addWindow(Windows.DISPENSING_CASH,
                getURLResourceFromFileName("/DispensingMoneyProcess.fxml"),
                new DispensingMoneyProcessView(),
                PopupWindow.class);

//        this.windowsManager.addWindow(Windows.LOADING,
//                getURLResourceFromFileName("/LoadingScreen.fxml"),
//                new LoadingIndicatorController(),
//                PopupWindow.class);

        this.windowsManager.registerController(this.eventCallbackMap);
    }


    private void tick() {
        this.windowsManager.getCurrentWindow().getController().onTickCallback(TICK_TIME);
    }

    public BooleanReasonResponse acquireCardOnRequest(String pinNumber, String cardNumber, AccountType accountType) {
        try {
            if(card == null || !Objects.equals(card.getCardNumber(), cardNumber)) {
                card = Card.getCardInfo(cardNumber);
            }
        }
        catch (ATMException e) {
            return new BooleanReasonResponse(false, e.getMessage());
        }


//        if (card == null) {
//            return new BooleanReasonResponse(false, "Card Number not valid");
//        }

        if(!card.isValid()) {
            return new BooleanReasonResponse(false, card.getReason());
        }

        //
        if(!card.checkPin(pinNumber)) {
            return new BooleanReasonResponse(false, "Pin Number not valid");
        }

        List<String> accountNumberList;
        try {
            accountNumberList = Card.retrieveLinkedAccountNumbers(cardNumber);

        }
        catch (ATMException e) {
            return new BooleanReasonResponse(false, e.getMessage());
        }

        if(accountNumberList.size() != 1) {
            //big error?
            return new BooleanReasonResponse(false, "No linked accounts found for this card");
        }
        userAccount = Account.retrieveUserAccount(accountNumberList.get(0));
        if (userAccount == null) {
            return new BooleanReasonResponse(false, "Account is not valid");
        }
        this.accountType = accountType;



        return new BooleanReasonResponse(true, "");
    }

    /**
     * Gets the full resource path from a given file name. Fileanme must be preceded by "/" so that the
     * url is constructed correctly.
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    private URL getURLResourceFromFileName(String filename) throws FileNotFoundException {
        URL url = AtmWindow.class.getResource(filename);
        if(url == null) {
            throw new FileNotFoundException(filename + " cannot be found in the resource path");
        }
        return url;
    }
}
