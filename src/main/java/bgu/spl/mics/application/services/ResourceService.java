package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.ThreadsCounter;
import bgu.spl.mics.application.messages.GetVehicleEvent;
import bgu.spl.mics.application.messages.ReleseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBrodcast;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.LinkedList;
import java.util.List;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	ResourcesHolder myresources;
	List<Future> futures;


	public ResourceService(String name) {
		super(name);
		this.myresources=ResourcesHolder.getInstance();
		futures=new LinkedList<Future>();
	}

	@Override
	protected void initialize() {
        subscribeEvent(GetVehicleEvent.class, event -> {
            Future<DeliveryVehicle> vehicleFuture=myresources.acquireVehicle();
            complete(event,vehicleFuture);
            futures.add(vehicleFuture);

        });

        subscribeEvent(ReleseVehicleEvent.class,event->{
            myresources.releaseVehicle(event.getVehicleToReturn());
        });


		subscribeBroadcast(TerminateBrodcast.class, myevent->{
           for(Future f:futures){
               if(!f.isDone()){
                   f.resolve(null);
               }
           }
			this.terminate();
		});
		ThreadsCounter.getInstance().Add1();


	}

}
