package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FutureTest {

    private static Future<String> tFuture;
    private static String result;
    AtomicBoolean check;
    TimeUnit seconds;

    @Before
    public void setUp() throws Exception {
        tFuture = new Future<>();
        result = "result";
        check = new AtomicBoolean(false);
        seconds = TimeUnit.SECONDS;

    }

    @Test
    public void testGetWait() {
        // test if not resolved, wait until resolved
        tFuture.resolve(null);
        Thread thread = new Thread(()-> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tFuture.resolve("resolved");
        });
        thread.start();
        assertEquals("not freed when resolved" ,tFuture.get(),"resolved");

    }

    @Test
    public void testResolve() {
        tFuture.resolve(result);
        assertEquals("resolve didnt change value", result, tFuture.get(1,seconds));
    }

    @Test
    public void testIsDone() {
        assertFalse("is done returned wrong value", tFuture.isDone());
        tFuture.resolve(result);
        assertTrue("is done returned wrong value", tFuture.isDone());
    }

    @Test
    public void testGetWaitUntil() {
        // test if not resolved, wait until resolved
        tFuture.resolve(null);
        Thread thread = new Thread(()-> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tFuture.resolve("resolved");
        });
        thread.start();
        assertEquals("not freed when resolved" ,tFuture.get(3, seconds),"resolved");
    }
}