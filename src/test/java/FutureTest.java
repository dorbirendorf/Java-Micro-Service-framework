package java;

import bgu.spl.mics.Future;
import org.junit.Test;

import static org.junit.Assert.*;

public class FutureTest {

    private Future<String> myFuture=new Future <String> ();
    @Test
    public void get() {
        myFuture.resolve("Boom");
    assertEquals("Boom",myFuture.get());
    }

    @Test
    public void resolve() {
        myFuture.resolve("dor gever");
        assertTrue(myFuture.isDone());
    }

    @Test
    public void isDone() {
        myFuture.resolve("lala");
        assertTrue(myFuture.isDone());
    }

    @Test
    public void get1() {

    }
}