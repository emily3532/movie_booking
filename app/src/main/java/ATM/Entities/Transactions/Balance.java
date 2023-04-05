package ATM.Entities.Transactions;

import ATM.Entities.Accounts.ATMAccount;
import ATM.Entities.Accounts.UserAccount;
import ATM.Database.TransactionDB;

public class Balance extends Transaction {

    private static final String typeStr = "Balance";

    public Balance(UserAccount userAcc, ATMAccount atmAcc, float quantity) {
        super(userAcc, atmAcc, quantity, typeStr);
    }

    /**
     * Attempts to retrieve the balance of an account, and sets
     * the transaction quantity to the balance if successful
     * @return whether or not the transaction was successful
     */
    public boolean attempt() {
        //TODO: H/S add test to make sure that the appropriate function is called
        if(isUserTransactionATM()) {
            setQuantity(atmAccount.getBalance());
        }
        else {
            setQuantity(userAccount.getBalance());
        }

        setTransactionResult(TransactionResult.SUCCESS);
        return recordTransaction();
    }
}
