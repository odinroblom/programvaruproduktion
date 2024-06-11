import backend.SpringBootApp;
import backend.utils.DateHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringBootApp.class)
public class DateTest {

    Date yesterday;
    Date today;

    @Before
    public void init() {
        yesterday = Date.from( Instant.now().minusSeconds( 60 * 60 * 24 ) );
        today = Date.from( Instant.now() );
    }

    @Test
    public void testEquals() {
        assertEquals(0, today.compareTo(Date.from(Instant.now())));
        assertNotEquals( 0, today.compareTo( yesterday ) );

        assertNotEquals( DateHandler.format(yesterday), DateHandler.format(today) );
    }
}
