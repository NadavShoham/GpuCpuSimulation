package bgu.spl.mics;



import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//private final Object sendEventLock;
	private static class SingletonHolder {
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceQueueMap;
	private final ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> messageQueueMap;
	private final ConcurrentHashMap<Event, Future> eventFutureMap;

	private MessageBusImpl() {
		microServiceQueueMap = new ConcurrentHashMap<>();
		messageQueueMap = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
		//sendEventLock = new Object();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (type) {
			messageQueueMap.putIfAbsent(type, new LinkedBlockingQueue<>());
			messageQueueMap.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (type) {
			messageQueueMap.putIfAbsent(type, new LinkedBlockingQueue<>());
			messageQueueMap.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO print
		//System.out.println("resolving " + e.toString() + " with " + result);
		eventFutureMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// goes over all microServices and adds the message to their queue
		synchronized (b.getClass()) {
			for (MicroService m: messageQueueMap.get(b.getClass())) {
				microServiceQueueMap.get(m).add(b);
			}
		}
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		//find the type fo the event
		Class<? extends Message> type =  e.getClass();

		MicroService addTo;
		synchronized (type) {
			//find the specific microservice that need to handle the event according the round rubin order
			addTo = messageQueueMap.get(type).poll();
			assert addTo != null;
			//return the microservice to the event's queue
			messageQueueMap.get(type).add(addTo);
			//add the event to the microservice's queue
			microServiceQueueMap.get(addTo).add(e);
		}
		eventFutureMap.put(e, new Future<>());
		return eventFutureMap.get(e);
	}

	@Override
	public void register(MicroService m) {
		// creates a new queue for the new microservice
		if (!isMSRegistered(m))
			microServiceQueueMap.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		// searches all messages if microservice is subscribed to them and removes him
		for (Class<? extends Message> type: messageQueueMap.keySet()) {
			synchronized (type) {
				if (isMSSubscribedToMessage(type, m))
					messageQueueMap.get(type).remove(m);
			}
		}
		microServiceQueueMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!isMSRegistered(m))
			throw new IllegalStateException("MicroService not registered");
		return microServiceQueueMap.get(m).take();
	}


	@Override
	public boolean isMSRegistered(MicroService m) {
		return microServiceQueueMap.containsKey(m);
	}

	@Override
	public <T> boolean isMSSubscribedToEvent(Class<? extends Event<T>> type, MicroService m) {
		return isMSSubscribedToMessage(type, m);
	}

	@Override
	public boolean isMSSubscribedToBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return isMSSubscribedToMessage(type, m);
	}

	@Override
	public boolean isMSSubscribedToMessage(Class<? extends Message> type, MicroService m) {
		return messageQueueMap.get(type).contains(m);
	}

	@Override
	public boolean isBroadcastInQueue(Broadcast b, MicroService m) {
		return isMessageInQueue(b, m);
	}

	@Override
	public <T> boolean isEventInQueue(Event<T> e, MicroService m) {
		return isMessageInQueue(e, m);
	}

	public boolean isMessageInQueue(Message message, MicroService m) {
		return microServiceQueueMap.get(m).contains(message);
	}

	@Override
	public <T> Future<T> getFuture(Event<T> e) {
		return eventFutureMap.get(e);
	}

	@Override
	public <T> MicroService peekEventQueue(Class<? extends Event<T>> type) {
		return messageQueueMap.get(type).peek();
	}

	/**
	 * gets the instance of the thread safe singleton
	 * @return singleton instance*/
	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	public boolean isEmpty(MicroService m) {
		return microServiceQueueMap.get(m).isEmpty();
	}

	//TODO print func
	/*
	public ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> getDeleteMe() {
		return messageQueueMap;
	}
	 */

}
