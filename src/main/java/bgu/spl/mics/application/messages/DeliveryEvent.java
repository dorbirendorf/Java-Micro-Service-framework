package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * This event is sent by the API service to deliver the book
 */

public class DeliveryEvent implements Event {
    private Customer customer;

    public DeliveryEvent(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
