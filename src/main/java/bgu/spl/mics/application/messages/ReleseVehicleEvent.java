package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * This event is sent by the resource service to relese a vehicle
 */

public class ReleseVehicleEvent implements Event {

    DeliveryVehicle vehicleToReturn;

    public ReleseVehicleEvent(DeliveryVehicle vehicleToReturn) {
        this.vehicleToReturn = vehicleToReturn;
    }

    public DeliveryVehicle getVehicleToReturn() {
        return vehicleToReturn;
    }
}
