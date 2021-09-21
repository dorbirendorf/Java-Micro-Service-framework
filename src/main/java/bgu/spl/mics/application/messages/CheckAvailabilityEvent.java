package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

/**
 * This event is sent by the selling service  to check if the book is available
 */
public class CheckAvailabilityEvent implements Event<Integer> {

    private String bookName ;

    public CheckAvailabilityEvent(String bookName) {

        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }
}
