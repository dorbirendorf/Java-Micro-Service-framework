package bgu.spl.mics.application;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadsCounter {

    private static ThreadsCounter myThreadsCounter;
    private AtomicInteger threadsInit;

    private ThreadsCounter() {
        this.threadsInit=new AtomicInteger(0);

    }

    public static synchronized ThreadsCounter getInstance(){

        if(myThreadsCounter==null) {
            myThreadsCounter = new ThreadsCounter();
        }
        return myThreadsCounter;

    }



    public synchronized AtomicInteger getThreadsInit() {
        return threadsInit;
    }

    public void Add1(){
        threadsInit.getAndIncrement();
    }
}

