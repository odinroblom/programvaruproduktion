package backend.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Utility class for formatting dates to yyyy-MM-dd pattern.
 * Used as a link between generated local time and database timestamps.
 */
public class DateHandler {
    public static SimpleDateFormat timeFormat = new SimpleDateFormat( "yyyy-MM-dd" );

    public static String format( Date date ) {
        return timeFormat.format( date );
    }

    public static String format( Instant instant ) {
        return format( Date.from( instant ) );
    }

    public static String now() {
        return format( Instant.now() );
    }

    public static Date parse( String date ) throws ParseException {
        return timeFormat.parse( date );
    }
}
