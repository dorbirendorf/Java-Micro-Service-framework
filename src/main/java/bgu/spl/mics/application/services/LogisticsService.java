package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.ThreadsCounter;
import bgu.spl.mics.application.messages.GetVehicleEvent;
import bgu.spl.mics.application.messages.ReleseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBrodcast;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.DeliveryEvent;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {



	public LogisticsService(String name) {
		super(name);

	}

	@Override
    protected void initialize() {
        subscribeEvent(DeliveryEvent.class, event -> {

                    Future<Future<DeliveryVehicle>> vehicle = sendEvent(new GetVehicleEvent());
                    if (vehicle != null) {
                        Future<DeliveryVehicle> f = vehicle.get();
                        if (f != null) {
                            DeliveryVehicle mycar = f.get();
                            if (mycar != null) {
                                mycar.deliver(event.getCustomer().getAddress(), event.getCustomer().getDistance());
                                sendEvent(new ReleseVehicleEvent(mycar));
                            }
                        }

                    }

                });
        subscribeBroadcast(TerminateBrodcast.class, myevent->{

            this.terminate();
        });
        ThreadsCounter.getInstance().Add1();


    }

}
