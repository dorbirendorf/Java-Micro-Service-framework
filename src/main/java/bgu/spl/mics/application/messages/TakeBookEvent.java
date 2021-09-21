package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

/**
 *This event is sent by the Inventory service to take a book
 */

//take book from the inventory
public class TakeBookEvent implements Event {
    String BookName;

    public TakeBookEvent(String bookName) {
        BookName = bookName;
    }

    public String getBookName() {
        return BookName;
    }
}
