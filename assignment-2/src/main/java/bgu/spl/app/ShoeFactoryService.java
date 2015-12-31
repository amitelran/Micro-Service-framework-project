package bgu.spl.app;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

import bgu.spl.mics.MicroService;

/**
 * This micro-service describes a shoe factory that manufactures shoes for the store.
 * This micro-service handles the {@code ManufacturingOrderRequest} and it takes it exactly 1 tick to manufacture
 * a single shoe (starting from the tick following the request). When done manufacturing,
 * this micro-service completes the request with a receipt (which has the value “store” in the
 * customer field and “discount” = false). The micro-service cannot manufacture more than one shoe per tick.
 */
public class ShoeFactoryService extends MicroService{
	
	private Integer currentTick;
	private Map<ManufacturingOrderRequest,Integer> ordersFinalTick;
	private int totalTicks;
	private int finalShoeTick;
	private CyclicBarrier barrier;
	
	/**
	 * The constructor sets the {@code ShoeFactoryService} name, starting tick, a counter of total ticks to wait
	 * before starting a new manufacture request according to existing requests, current finishing manufacture
	 * requests tick, a shared {@link CyclicBarrier} and a {@link ConcurrentHashMap} which gets a
	 * {@code ManufacturingOrderRequest} as a key, and its value is the tick to complete the manufacture request.
	 * @param name - the name of the {@code ShoeFactoryService}
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
	public ShoeFactoryService(String name,CyclicBarrier barrier){
		super(name);
		currentTick=1;
		totalTicks=0;
		finalShoeTick=0;
		ordersFinalTick=new ConcurrentHashMap<ManufacturingOrderRequest,Integer>();
		this.barrier = barrier;
	}

	/**
	 * Initializes the {@code ShoeFactoryService}.
	 * It subscribes the {@code ShoeFactoryService} for {@code TickBroadcast}, {@code ManufacturingOrderRequest}
	 * and {@code TerminationBroadcast}.
	 * Every tick it removes the completed {@code ManufacturingOrderRequests} and sends a corresponding receipt.
	 * It gets {@code ManufcaturingOrderRequests}, calculates the tick to finish the manufacturing according to
	 * other on-going manufacturing requests. Every tick it removes the completed {@code ManufacturingOrderRequests}
	 * and sends a corresponding receipt.
	 */
	@Override
	protected void initialize() {
		log(getName() + " is starting");
		this.subscribeBroadcast(TerminationBroadcast.class, terB->{
			try {
				barrier.await();
			} catch (Exception e) {}
			this.terminate();
		});
		this.subscribeBroadcast(TickBroadcast.class, b->{		
			currentTick=b.getTick();
			if(totalTicks>0){
				totalTicks--;
				for(Entry<ManufacturingOrderRequest,Integer> e : ordersFinalTick.entrySet()){
					if(e.getValue()<=currentTick){
						ordersFinalTick.remove(e.getKey());
						log("tick #" + currentTick + ": " + this.getName() + " finished manufacturing " 
					+ e.getKey().getAmount() + " pairs of " + e.getKey().getType() + ". Shipping theme ( with a receipt of course ) ...");
						Receipt rec = new Receipt(this.getName(), "Store", e.getKey().getType(), false, currentTick, currentTick-e.getKey().getAmount()-1, e.getKey().getAmount());
						this.complete(e.getKey(),rec);
					}
				}
			}
		});
		this.subscribeRequest(ManufacturingOrderRequest.class, manuReq->{		
			log("tick #" + currentTick + ": " + this.getName() + 
					" got a Manufacturing Order Request for " + manuReq.getAmount() + 
					" pairs of " + manuReq.getType() + ". Adding to manufacturing schedule queue...");
			if(totalTicks==0)
				finalShoeTick=currentTick+1;
			int finishTick = finalShoeTick+manuReq.getAmount();	
			finalShoeTick=finishTick;
			ordersFinalTick.put(manuReq, finishTick);
			totalTicks+=manuReq.getAmount()+1;
		});

		try {
			barrier.await();
		} catch (Exception e) {}
	}
	
	/**
	 * A setter which sets the shared {@link CyclicBarrier} to the {@code ShoeFactoryService}.
	 * @param barrier - a shared {@link} CyclicBarrier for all services
	 */
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}
}
