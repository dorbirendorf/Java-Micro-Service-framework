package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.ThreadsCounter;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;
//import sun.misc.Lock;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

    private MoneyRegister moneyRegister;
    int currTick;

	public SellingService(String name) {
		super(name);

		this.moneyRegister=MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {

        subscribeBroadcast(TickBroadcast.class,Myevent->{
            this.currTick=Myevent.getTick();
        });

        subscribeEvent(BookOrderEvent.class, MyEvent->{
		    Future<Integer> bookprice=sendEvent(new CheckAvailabilityEvent(MyEvent.getBookName()));
		    if(bookprice!=null) {
                if (bookprice.get() == -1) {
                    complete(MyEvent, null);
                }
                synchronized (MyEvent.getCustomer()) { //sync by customer so hee wont get charge by someone else or book will be taken
                    if (MyEvent.getCustomer().getAvailableCreditAmount() < bookprice.get()) { //case cant afford the book
                        complete(MyEvent, null);
                    } else { //case he have the money to buy the book
                        Future<OrderResult> bookAvaliable_Future = (Future<OrderResult>) sendEvent(new TakeBookEvent(MyEvent.getBookName()));
                        if (bookAvaliable_Future.get().equals(OrderResult.SUCCESSFULLY_TAKEN)) {
                            moneyRegister.chargeCreditCard(MyEvent.getCustomer(), bookprice.get()); //charge money
                            OrderReceipt myrecipt=new OrderReceipt(this.getName(), MyEvent.getCustomer().getId(), MyEvent.getBookName(), bookprice.get(), MyEvent.gettick(), MyEvent.gettick(), MyEvent.gettick());
                            moneyRegister.file(myrecipt);
                            MyEvent.getCustomer().addRecipt(myrecipt);
                            OrderReceipt receipt = new OrderReceipt(this.getName(), MyEvent.getCustomer().getId(), MyEvent.getBookName(), bookprice.get(), 0,MyEvent.gettick(),currTick);

                            complete(MyEvent, receipt);


                        }
                        else {
                            complete(MyEvent, null);
                        }

                    }
                }

            }
            else {
                System.out.println("No Micro-Service has registered to handle BookOrderEvent events! The event cannot be processed");
            }
		});



		subscribeBroadcast(TerminateBrodcast.class,myevent->{

            this.terminate();
        });

        ThreadsCounter.getInstance().Add1();

	}

}
