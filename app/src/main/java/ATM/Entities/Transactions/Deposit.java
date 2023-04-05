package ATM.Entities.Transactions;

import ATM.Entities.Accounts.ATMAccount;
import ATM.Entities.Accounts.UserAccount;
import ATM.Database.TransactionDB;
import ATM.utils.ATMException;

public class Deposit extends Transaction {

    private static final String typeStr = "Deposit";

    public Deposit(UserAccount userAcc, ATMAccount atmAcc, float quantity) {
        super(userAcc, atmAcc, quantity, typeStr);
    }


    /**
     * Attempts to deposit transaction quantity into
     * the user account.
     * @return True if the deposit succeeded, false otherwise
     */
    public boolean attempt() throws ATMException {
        setTransactionResult(TransactionResult.FAIL);

        // can only deposit a positive amount
        if (getQuantity() <= 0) {
            throw new ATMException("Cannot deposit negative $");
        }

        //TODO: Haeata/Steve pls add test
        boolean res = false;
        //user account is atm account, so we only need to make change to the ATM Account (?)
        if (isUserTransactionATM()) {
            res = getAtmAccount().changeBalance(getQuantity());
        }
        else {
            // Money goes into both user account and ATM cash quantity when deposited
            res = (getUserAccount().changeBalance(getQuantity()) && getAtmAccount().changeBalance(getQuantity()));
        }
        if (res) {
            setTransactionResult(TransactionResult.SUCCESS);
            return recordTransaction();
        }

        return false;
    }
}
