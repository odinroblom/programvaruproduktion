package backend.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Utility class for handling decimal formatting to two decimals.
 */
public class DecimalHandler {

    public static DecimalFormat formatter;
    static {
        formatter = new DecimalFormat();
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator( '.' );
        formatter.setDecimalFormatSymbols( formatSymbols );
        formatter.setMinimumIntegerDigits(1);
        formatter.setMaximumFractionDigits(2);
        formatter.setGroupingUsed( false );
    }


    public static String format(Double value ) {
        if ( value == null )
            return "";

        return formatter.format( value );
    }
}
