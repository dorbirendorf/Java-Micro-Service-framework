package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.ThreadsCounter;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private Customer customer;
	private List < Pair> orders;
	private List<Future> recipts;



	public APIService(Customer customer,List<Pair> orderSchedule) {

        super("API_Service");
        this.customer = customer;
        this.orders = orderSchedule;
        recipts = new ArrayList<Future>();

    }


	@Override
	protected void initialize() {

        subscribeBroadcast(TickBroadcast.class, MyEvent -> {

            int cuurrentTick=MyEvent.getTick();
            for(Pair p :orders){
                if(p.getTick() == (cuurrentTick)){                //if there is a order in this tick new order event..
                    Future<OrderReceipt>futureRecipt=sendEvent(new BookOrderEvent(p.getBookName(),customer,cuurrentTick));
                    recipts.add(futureRecipt);   //save the recipt
                }
            }

            List<Future> toRemove = new ArrayList<Future>();
            for(Future f:recipts){
                if(f.isDone() && f.get()!=null) {    //if there is a "real" recipt and not null or future
                    ((OrderReceipt) f.get()).setIssuedTick(MyEvent.getTick());
                    sendEvent(new DeliveryEvent(customer));//deliver
                    toRemove.add(f);
                     f.get();
                }
            }
            recipts.removeAll(toRemove);



        });

        subscribeBroadcast(TerminateBrodcast.class,myevent->{

            this.terminate();
        });



        ThreadsCounter.getInstance().Add1();
    }
}