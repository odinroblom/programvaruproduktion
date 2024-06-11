package backend.utils;

import java.util.function.BooleanSupplier;

/**
 * Utility class for polling the state of anything via a boolean supplier.
 * Includes time interval as well as a 'max' timeout failsafe.
 */
public class Poll {
    public static boolean until(BooleanSupplier supplier, int interval, long max) {
        long maxMS = System.currentTimeMillis() + max;
        do {
            if (supplier.getAsBoolean())
                return true;

            try {
                Thread.sleep( interval );
            } catch (InterruptedException ignored) {
                //ignore
            }
        } while (System.currentTimeMillis() < maxMS);

        return false;
    }
}
