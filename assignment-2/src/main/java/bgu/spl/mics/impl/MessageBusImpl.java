package bgu.spl.mics.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;
import bgu.spl.mics.RequestCompleted;

/**
 * The thread safe singleton implementation of the {@link #MessageBus(bgu.spl.mics)}.
 */
public class MessageBusImpl implements MessageBus {
	
	private Map<MicroService,ConcurrentLinkedQueue<Message>> registeredServices;	
	private Map<Class<? extends Message>,ConcurrentRoundRobinQueue<MicroService>> messageSubscriptions;
	private Map<Request<?>,MicroService> requesterMicroServices;	
	
    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }
    
    /**
     * The constructor of the {@code MessageBusImpl} declare three {@link ConcurrentHashMap}:
     * registeredServices - A map consists of micro-services as a key, and for each micro-service it holds a 
     * {@link ConcurrentLinkedQueue} of the micro-service messages as a value.
     * messageSubscriptions - A map consists of messages as a key, and for each message it holds a 
     * {@link #ConcurrentRoundRobinQueue(bgu.spl.mics.impl)} of the subscribed micro-services.
     * requesterMicroServices - A map consists of requests as a key, and for each request, it holds
     * the micro-service which sent the request, in order to reply the result to the requester easily.
     */
    private MessageBusImpl() {
    	registeredServices = new ConcurrentHashMap<MicroService,ConcurrentLinkedQueue<Message>>();
    	messageSubscriptions = new ConcurrentHashMap<Class<? extends Message>,ConcurrentRoundRobinQueue<MicroService>>();
    	requesterMicroServices = new ConcurrentHashMap<Request<?>,MicroService>();
    }
    
    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * subscribes {@code m} to receive {@link Request}s of type {@code type}.
     * <p>
     * @param type the type to subscribe to
     * @param m    the subscribing micro-service
     */
	@Override
	public void subscribeRequest(Class<? extends Request<?>> type, MicroService m){
		synchronized(messageSubscriptions){
			returnRegistered(m);
			if(messageSubscriptions.get(type)==null)
				messageSubscriptions.put(type, new ConcurrentRoundRobinQueue<MicroService>());
			messageSubscriptions.get(type).add(m);
		}
	}

	/**
     * subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     * @param type the type to subscribe to
     * @param m    the subscribing micro-service
     */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized(messageSubscriptions){
			returnRegistered(m);
			if(!messageSubscriptions.containsKey(type))
				messageSubscriptions.put(type, new ConcurrentRoundRobinQueue<MicroService>());
			messageSubscriptions.get(type).add(m);
		}
	}

	/**
     * Notifying the MessageBus that the request {@code r} is completed and its
     * result was {@code result}.
     * When this method is called, the message-bus will implicitly add the
     * special {@link RequestCompleted} message to the queue
     * of the requesting micro-service, the RequestCompleted message will also
     * contain the result of the request ({@code result}).
     * <p>
     * @param <T>    the type of the result expected by the completed request
     * @param r      the completed request
     * @param result the result of the completed request
     */
	@Override
	public <T> void complete(Request<T> r, T result) {
		returnRegistered(requesterMicroServices.get(r)).add(new RequestCompleted<T>(r, result));
		requesterMicroServices.remove(r);
		
	}

	/**
     * add the {@link Broadcast} {@code b} to the message queues of all the
     * micro-services subscribed to {@code b.getClass()}.
     * <p>
     * @param b the message to add to the queues.
     */
	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<MicroService> bQueue = messageSubscriptions.get(b.getClass());
		if(bQueue!=null){
			for(MicroService m : bQueue){
				returnRegistered(m).add(b);
			}
		}
	}

	/**
     * add the {@link Request} {@code r} to the message queue of one of the
     * micro-services subscribed to {@code r.getClass()} in a round-robin
     * fashion.
     * <p>
     * @param r         the request to add to the queue.
     * @param requester the {@link MicroService} sending {@code r}.
     * @return true if there was at least one micro-service subscribed to
     *         {@code r.getClass()} and false otherwise.
     */
	@Override
	public boolean sendRequest(Request<?> r, MicroService requester) {
		synchronized(messageSubscriptions){
			returnRegistered(requester);
			ConcurrentRoundRobinQueue<MicroService> rQueue = messageSubscriptions.get(r.getClass());
			if(rQueue!=null){
				MicroService rrMS=rQueue.getNextRR();
				if(rrMS!=null){
					registeredServices.get(rrMS).add(r);
					requesterMicroServices.put(r, requester);
					return true;
				}
			}
			return false;
		}
	}

	/**
     * allocates a message-queue for the {@link MicroService} {@code m}.
     * <p>
     * @param m the micro-service to create a queue for.
     */
	@Override
	public void register(MicroService m) {
		registeredServices.put(m, new ConcurrentLinkedQueue<Message>());
	}

	 /**
     * remove the message queue allocated to {@code m} via the call to
     * {@link #register(bgu.spl.mics.MicroService)} and clean all references
     * related to {@code m} in this message-bus. If {@code m} was not
     * registered, nothing should happen.
     * <p>
     * @param m the micro-service to unregister.
     */
	@Override
	public void unregister(MicroService m) {
		Iterator<ConcurrentRoundRobinQueue<MicroService>> it1 = messageSubscriptions.values().iterator();
		ConcurrentRoundRobinQueue<MicroService> RRQ;
		while(it1.hasNext()){
			RRQ=it1.next();
			RRQ.remove(m);
			if(RRQ.isEmpty())
				it1.remove();
		}
		Iterator<Entry<Request<?>,MicroService>> it2 = requesterMicroServices.entrySet().iterator();
		while(it2.hasNext()){
			if(it2.next().getValue()==m)
				it2.remove();
		}
		registeredServices.remove(m);
	}

	/**
     * using this method, a <b>registered</b> micro-service can take message
     * from its allocated queue.
     * This method is blocking -meaning that if no messages
     * are available in the micro-service queue it
     * should wait until a message became available.
     * The method should throw the {@link IllegalStateException} in the case
     * where {@code m} was never registered.
     * <p>
     * @param m the micro-service requesting to take a message from its message
     *          queue
     * @return the next message in the {@code m}'s queue (blocking)
     * @throws InterruptedException if interrupted while waiting for a message
     *                              to became available.
     */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		ConcurrentLinkedQueue<Message> MQ=returnRegistered(m);
		if(!MQ.isEmpty())
			return MQ.remove();
		return null;
	}
	
	private ConcurrentLinkedQueue<Message> returnRegistered(MicroService m){
		if(registeredServices.get(m)==null)
			throw new IllegalStateException("MicroService "+m.getName()+" is not registered");
		return registeredServices.get(m);
	}
}