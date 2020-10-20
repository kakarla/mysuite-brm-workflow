import org.junit.After;
import org.junit.Before;

import java.time.Duration;
import java.time.Instant;

public abstract class AbstractTest {
    Instant start;
    @Before
    public void init(){
        start = Instant.now();
    }
    @After
    public void printTimeTaken(){
        long timeElapsed = Duration.between(start, Instant.now()).toMillis();  //in millis
        System.out.println("Test time taken: " + timeElapsed);
    }
}
