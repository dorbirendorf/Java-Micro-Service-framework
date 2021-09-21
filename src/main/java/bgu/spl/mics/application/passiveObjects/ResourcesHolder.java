package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	BlockingQueue<DeliveryVehicle> freeVehicles;
    BlockingQueue<Future<DeliveryVehicle>> futureVehicles;
	private static ResourcesHolder myResourcesHolder;


    private ResourcesHolder()
    {

        freeVehicles = new LinkedBlockingQueue<DeliveryVehicle>();
        futureVehicles=new LinkedBlockingQueue<Future<DeliveryVehicle>>();
    }
	/**
	 * Retrieves the single instance of this class.
	 */



	public static ResourcesHolder getInstance() {
        if(myResourcesHolder==null) {
            myResourcesHolder=new ResourcesHolder();
        }
        return myResourcesHolder;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {
        Future f = new Future();
	    if(!freeVehicles.isEmpty()) {
            f.resolve(freeVehicles.poll());
        }
        else{
	        futureVehicles.add(f);
        }

        return f;
    }

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
	    if(futureVehicles.isEmpty()) {
            freeVehicles.add(vehicle);
        }
        else {
	        futureVehicles.poll().resolve(vehicle);
        }

	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		for (DeliveryVehicle deliveryVehicle:vehicles)
		{
			freeVehicles.add(deliveryVehicle);
		}

	}

}
