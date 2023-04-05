package ATM.Entities.Transactions;

import ATM.Entities.Accounts.ATMAccount;
import ATM.Entities.Accounts.UserAccount;

import ATM.Database.TransactionDB;
import ATM.utils.ATMException;

public class Withdrawal extends Transaction {

    private static final String typeStr = "Withdrawal";

    public Withdrawal(UserAccount userAcc, ATMAccount atmAcc, float quantity) {
        super(userAcc, atmAcc, quantity, typeStr);
    }

    /**
     * Attempts the withdrawal transaction, failing if either user/ATM accounts
     * will be overdrawn
     * @return true if successful, false otherwise
     */
    public boolean attempt() throws ATMException {
        setTransactionResult(TransactionResult.FAIL);

        // Can't withdraw 0/negative $, or overdraw an account
        if (getQuantity() <= 0) {
            throw new ATMException("Cannot withdraw negative $");
        } else if (atmAccount.getBalance() - getQuantity() < 0) {
            throw new ATMException("ATM Machine is overdrawn. Please refill the machine to withdraw money.");
        } else if (userAccount.getBalance() - getQuantity() < 0) {
            throw new ATMException("Not enough money in your account to withdrawn this amount.");
        }

        //TODO: Haeata/Steve pls add test
        //we should never get here becuase the ATM account cannot make withdrawals
        if (isUserTransactionATM()) {
            throw new ATMException("ATM Admin account cannot withdraw money!");
        }

        boolean res = (atmAccount.changeBalance(-getQuantity()) && userAccount.changeBalance(-getQuantity()));
        if (res) {
            setTransactionResult(TransactionResult.SUCCESS);
            return recordTransaction();
        }

        return false;
    }
}
