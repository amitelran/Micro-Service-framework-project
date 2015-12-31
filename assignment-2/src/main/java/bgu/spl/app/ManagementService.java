package bgu.spl.app;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

import bgu.spl.mics.MicroService;

/**
 * A micro-service which can add discount to shoes in the store and send {@code NewDiscountBroadcast}
 * to notify clients and selling services. In addition, this micro-service handles {@code RestockRequests}
 * from selling services, checks if there is already an order for the shoe, and if not, it send 
 * {@code ManufacturingOrderRequest} to a factory. When the order is completed, it updates the store storage,
 * files the receipt and completes the {@code RestockRequest} with the result of true (if there is
 * no factory to respond to the request, it completes the request with the result of false). 
 */
public class ManagementService extends MicroService {
	
	/**
	 * An object which holds the information of the {@code RestockRequests}, how many shoes have been ordered
	 * in the request and how many has been reserved. Reserved means the if a client request a shoe of some type, a 
	 * manufacturing request has been sent and another client is requesting for same shoe, then, the
	 * reserved calculates the delta between the amount requested from clients and the amount ordered 
	 * for restock, in order to check whether a new {@code RestockRequest} is needed, or the amount requested for 
	 * restock suffies the current amount requested from clients.
	 */
	private class orderedAndReserved{
		int ordered;
		int reserved;
		ConcurrentLinkedQueue<RestockRequest> requests;
		
		/**
		 * @param ordered - amount of shoes ordered
		 * @param reserved - amount of shoes reserved
		 * @param req - a {@code RestockRequest} to be added to the linked queue
		 */
		public orderedAndReserved(int ordered,int reserved,RestockRequest req){
			this.ordered=ordered;
			this.reserved=reserved;
			requests=new ConcurrentLinkedQueue<RestockRequest>();
			requests.add(req);
		}
	}
	
	
	private List<DiscountSchedule> DiscountSchedule;
	private int currentTick;
	private Map<String,orderedAndReserved> orders;
	private CyclicBarrier barrier;
	
	/**
	 * The constructor defines the manager with name, discount schedule, starting tick, orders map and 
	 * a shared {@link CyclicBarrier}.
	 * @param DiscountSchedule - a list of the discounts to be issued
	 * @param barrier - a shared {@link CyclicBarrier} for all services used for verifying all other services are down 
	 * before self termination.
	 */
	public ManagementService(List<DiscountSchedule> DiscountSchedule,CyclicBarrier barrier) {
		super("manager");
		this.DiscountSchedule=DiscountSchedule;
		currentTick=1;
		orders = new ConcurrentHashMap<String,orderedAndReserved>();
		this.barrier=barrier;
	}

	/**
	 * Initializing the {@code ManagementService}. 
	 * Subscribing the {@code ManagementService} to the {@code TickBroadcast}, in which it updates the
	 * global hour and performing it actions as required according to the corresponding tick.
	 * The iterator goes through all past {@code DiscountSchedule} and sends the requests according to the schedule.
	 * Subscribing to {@code RestockRequests} and sending them to the factories for manufacturing or not.
	 * Subscribing the {@code ManagementService} to {@code TerminationBroadcast}.
	 * After receiving {@code TerminationBroadcast}, the {@code ManagementService} will wait for all other 
	 * services to gracefully terminate, and only then will print the store data, and self terminate itself.
	 */
	@Override
	protected void initialize() {
		log("Management Service is starting");
		this.subscribeBroadcast(TerminationBroadcast.class, terB->{
			log("manager got a Termination Broadcast, waiting for all services to gracefully terminate ...");
			try {
				barrier.await();
			} catch (Exception e) {}
			log("everyone terminated gracefully... printing stock info and receipts.");
			Store.getInstance().print();
			this.terminate();
		});
		
		this.subscribeBroadcast(TickBroadcast.class, b->{
			currentTick=b.getTick();
			Iterator<DiscountSchedule> it=DiscountSchedule.iterator();
			DiscountSchedule ds;
			while(it.hasNext()){
				ds=it.next();
				if(ds.getTick()<=currentTick){
					it.remove();
					try {
						Store.getInstance().addDiscount(ds.getShoeType(), ds.getAmount());
						this.sendBroadcast(new NewDiscountBroadcast(ds.getShoeType(), ds.getAmount()));
					} catch (NoShoesException e) {
						log("tick #" + currentTick + ": no enough shoes of type " + e.getShoeType() + " in stock for discount (requested " + e.getAmount() + ")");
					}
				}
			}
		});
		
		this.subscribeRequest(RestockRequest.class, req -> {
			log("tick #"+currentTick+": "+this.getName() + " got a restock request");
			synchronized(orders){
				String shoe=req.getShoeType();
				int amount=req.getAmount();
				orderedAndReserved order = orders.get(shoe);
				if( order==null || order.ordered-order.reserved<amount ){
					int newOrderAmount=currentTick%5+1;
					if(order==null){
						order=new orderedAndReserved(newOrderAmount,1,req);
						orders.put(shoe, order);
					}
					else{
						order.ordered+=newOrderAmount;
						order.reserved++;
						order.requests.add(req);
					}
					boolean requestSucceeded=sendManufacturingRequest(shoe,newOrderAmount);
					if(!requestSucceeded){
						log("tick #"+currentTick+": no factories could manufacture new shoes :(");
						this.complete(req, false);
					}
				}
				else{
					log("tick #"+currentTick+": " + req.getShoeType() + " already ordered from factory with sufficient amount");
					order.requests.add(req);
					order.reserved++;
				}
			}
		});
		try {
			barrier.await();
		} catch (Exception e) {}
	}
	
	
	private boolean sendManufacturingRequest(String shoeType,int amount){
		log("tick #"+currentTick+": " + this.getName() + " sends a manufacturing order request for "+ amount +" pairs of "+shoeType);
		boolean ans=this.sendRequest(new ManufacturingOrderRequest(shoeType, amount), res->{
			synchronized(orders){				
				if(res!=null){
					Store.getInstance().file(res);
					Store.getInstance().add(shoeType, res.getAmountSold()-orders.get(shoeType).reserved);
					
					for(int i=0;i<res.getAmountSold()&&!orders.get(shoeType).requests.isEmpty();i++)
						this.complete(orders.get(shoeType).requests.poll(), true);
					
					if(orders.get(shoeType).requests.isEmpty())orders.remove(shoeType);
				}
				else{
					for(RestockRequest rr : orders.get(shoeType).requests){
						this.complete(rr, false);
					}
					orders.remove(shoeType);
				}
			}
		});
		return ans;
	}
	
	/**
	 * A setter which sets the shared {@link CyclicBarrier} to the {@code ManagementService}.
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}

}
