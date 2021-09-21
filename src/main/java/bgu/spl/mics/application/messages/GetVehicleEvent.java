package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * This event is sent by the resource service to get a vehicle
 */
public class GetVehicleEvent implements Event<Future<DeliveryVehicle>> {


    public GetVehicleEvent() {
    }
}
