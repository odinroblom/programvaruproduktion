package backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for checking response validity and null/empty checking strings
 */
public class StringUtils {
    public static boolean isValidResponse( ResponseEntity<?> response ) {
        return ( response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null );
    }

    //wrapper
    public static boolean valid( String text ) {
        return org.springframework.util.StringUtils.hasText( text );
    }
}
