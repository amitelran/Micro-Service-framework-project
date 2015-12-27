package bgu.spl.mics.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;
import bgu.spl.mics.RequestCompleted;

public class MessageBusImpl implements MessageBus {
	
	private Map<MicroService,ConcurrentLinkedQueue<Message>> registeredServices;		//each microService with it's own messages queue
	private Map<Class<? extends Message>,ConcurrentRoundRobinQueue<MicroService>> messageSubscriptions;		//each message with it's round robin queue
	private Map<Request<?>,MicroService> requesterMicroServices;		//each request with it's requester to respond with result
	
    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }
    
    private MessageBusImpl() {
    	registeredServices = new ConcurrentHashMap<MicroService,ConcurrentLinkedQueue<Message>>();
    	messageSubscriptions = new ConcurrentHashMap<Class<? extends Message>,ConcurrentRoundRobinQueue<MicroService>>();
    	requesterMicroServices = new ConcurrentHashMap<Request<?>,MicroService>();
    }
    
    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

	@Override
	public void subscribeRequest(Class<? extends Request<?>> type, MicroService m){
		synchronized(messageSubscriptions){
			if(messageSubscriptions.get(type)==null)
				messageSubscriptions.put(type, new ConcurrentRoundRobinQueue<MicroService>());
			messageSubscriptions.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized(messageSubscriptions){
			if(!messageSubscriptions.containsKey(type))
				messageSubscriptions.put(type, new ConcurrentRoundRobinQueue<MicroService>());
			messageSubscriptions.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Request<T> r, T result) {
		registeredServices.get(requesterMicroServices.get(r)).add(new RequestCompleted<T>(r, result));
		requesterMicroServices.remove(r);
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<MicroService> bQueue = messageSubscriptions.get(b.getClass());
		if(bQueue!=null){
			for(MicroService m : bQueue){
				registeredServices.get(m).add(b);
			}
		}
	}

	@Override
	public boolean sendRequest(Request<?> r, MicroService requester) {
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

	@Override
	public void register(MicroService m) {
		registeredServices.put(m, new ConcurrentLinkedQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		Iterator<ConcurrentRoundRobinQueue<MicroService>> it = messageSubscriptions.values().iterator();
		ConcurrentRoundRobinQueue<MicroService> RRQ;
		while(it.hasNext()){
			RRQ=it.next();
			RRQ.remove(m);
		}
		registeredServices.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		ConcurrentLinkedQueue<Message> MQ=registeredServices.get(m);
		if(MQ==null)
			throw new IllegalStateException("MicroService "+m.getName()+" is not registered");
		if(!MQ.isEmpty())
			return MQ.remove();
		return null;
	}
}