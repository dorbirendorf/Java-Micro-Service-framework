package bgu.spl.mics;

import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<Class<?>, BlockingQueue<MicroService>> eventSubscribe;
	private Object subBlockE;
	private ConcurrentHashMap<Class<?>, BlockingQueue<MicroService>> broadSubscribe;
	private Object subBlockB;
	private ConcurrentHashMap<Event<?>, Future> futureEvent;
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceQ;


	private static class MsgBusSingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl() {
		this.eventSubscribe = new ConcurrentHashMap<>();
		this.subBlockE = new Object();
		this.broadSubscribe = new ConcurrentHashMap<>();
		this.subBlockB = new Object();
		this.futureEvent = new ConcurrentHashMap<>();
		this.microServiceQ = new ConcurrentHashMap<>();
	}
	public static MessageBusImpl getInstance() {
		return MsgBusSingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribe.putIfAbsent(type, new LinkedBlockingQueue<>());
		try{
			eventSubscribe.get(type).put(m);
		} catch (Exception e) {e.printStackTrace();};
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadSubscribe.putIfAbsent(type, new LinkedBlockingQueue<>());
		try{
			broadSubscribe.get(type).put(m);
		} catch (Exception e) {e.printStackTrace();};
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futureEvent.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		broadSubscribe.putIfAbsent(b.getClass(), new LinkedBlockingQueue<>());
		if(broadSubscribe.containsKey(b.getClass())){
			for(MicroService micro:broadSubscribe.get(b.getClass())){
				try {
					microServiceQ.get(micro).put(b);
				} catch (InterruptedException e){}
			}
		}

	}

	@Override
	public  <T> Future<T> sendEvent(Event<T> e) {

            eventSubscribe.putIfAbsent(e.getClass(), new LinkedBlockingQueue<>());
		synchronized (eventSubscribe.get(e.getClass())) {
			MicroService tempMicro = eventSubscribe.get(e.getClass()).poll();
			if (tempMicro == null) //check if there is microservice to handle event
				return null;

			//case there is a microservice that deals with this kind of events..
			Future<T> myfuture = new Future<>();
			futureEvent.putIfAbsent(e, myfuture);                        //e iss responsible to future
			try {
				eventSubscribe.get(e.getClass()).put(tempMicro);         //push the microService to the back of queue

				microServiceQ.get(tempMicro).put(e);                 //add the event to the microService list

			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return myfuture;
		}
        }


	@Override
	public void register(MicroService m) {
		if (microServiceQ.contains(m))
			return;
		BlockingQueue<Message> bq = new LinkedBlockingQueue<>();
		microServiceQ.put(m,bq);
	}

	@Override
	public void unregister(MicroService m) {
		for (ConcurrentHashMap.Entry<Class<?>, BlockingQueue<MicroService>> microEventQueue : eventSubscribe.entrySet()) {
			if (microEventQueue.getValue().contains(m)) microEventQueue.getValue().remove(m);
			if (microEventQueue.getValue().isEmpty()) {
				eventSubscribe.remove(microEventQueue.getValue());
			}
		}

		for (ConcurrentHashMap.Entry<Class<?>, BlockingQueue<MicroService>> microBroadQueue : broadSubscribe.entrySet()) {
			if (microBroadQueue.getValue().contains(m)) microBroadQueue.getValue().remove(m);
			if (microBroadQueue.getValue().isEmpty()) {
				broadSubscribe.remove(microBroadQueue.getValue());
			}
		}

		BlockingQueue<Message> myqueue = microServiceQ.get(m);
		if (myqueue.size()>0) {
			for (Message msg : myqueue) {
				Future<Object> myfuture = futureEvent.get(msg);
				if(!(myfuture==null)) {
					myfuture.resolve(null);
					futureEvent.remove(msg);
				}
			}
		}

		if(microServiceQ.containsKey(m))  {microServiceQ.remove(m);}    //delete the microservice queue of MSGs
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!microServiceQ.containsKey(m)){throw new InterruptedException(); }   //check if this micro service exist
		return microServiceQ.get(m).take();
	}
}