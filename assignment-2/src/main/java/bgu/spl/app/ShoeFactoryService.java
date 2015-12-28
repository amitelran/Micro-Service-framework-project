package bgu.spl.app;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

import bgu.spl.mics.MicroService;

public class ShoeFactoryService extends MicroService{
	
	private Integer currentTick;
	private Map<ManufacturingOrderRequest,Integer> ordersFinalTick;
	private int totalTicks;
	private int finalShoeTick;
	private CyclicBarrier barrier;
	
	public ShoeFactoryService(String name,CyclicBarrier barrier){
		super(name);
		currentTick=1;
		totalTicks=0;
		finalShoeTick=0;
		ordersFinalTick=new ConcurrentHashMap<ManufacturingOrderRequest,Integer>();
		this.barrier = barrier;
	}

	@Override
	protected void initialize() {
		log(getName() + " is starting");
		this.subscribeBroadcast(TerminationBroadcast.class, terB->{
			try {
				barrier.await();
			} catch (Exception e) {}
			this.terminate();
		});
		this.subscribeBroadcast(TickBroadcast.class, b->{			//getting current tick
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
		this.subscribeRequest(ManufacturingOrderRequest.class, manuReq->{			//subscribing to manufacturing requests
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
	
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}
}
