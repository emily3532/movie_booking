package ATM.utils;

import java.util.*;

/**
 * Helper class to determine the most efficient set of notes/coins to dispense to the user when they
 * request to withdraw money.
 */
public class CoinChanger {

    private static final double[] currency = {100, 50, 20, 10, 5, 2, 1, 0.50, 0.20, 0.10, 0.05};

    /**
     * Given an input amount to withdraw the algorithm determined how to dispense cash/coins so that the
     * user receives the smallest amount of each currency.
     *
     * Eg. if the user attempts to withdraw
     * $75 would become $50 x 1, $25 x 1
     * @param amount double Amount to withdraw.
     * @return HashMap of key = double, value = value representing an unordered map of return notes/coins (key)
     * and the # of each currency value that needs to be dispensed (value).
     */
    public static HashMap<Double, Double> findCoinAndNotes(double amount){
        if(amount == 0){
            return null;
        }

        HashMap<Double, Double> required = new HashMap<Double, Double>();

        for (int i = 0; i < currency.length; i++){
            while(currency[i] <= amount){
                double amt = Math.floor(amount/currency[i]);
                required.put(currency[i], amt);
                amount = Math.round((amount - currency[i]*(int)Math.round(amt))*100.0)/100.0;

            }
        }
//        for (Double i : required.keySet()) {
//            System.out.println("key: " + i + " value: " + required.get(i));
//        }
        return required;
    }

}
