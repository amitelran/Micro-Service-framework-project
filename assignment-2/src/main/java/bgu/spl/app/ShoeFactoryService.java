package bgu.spl.app;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import bgu.spl.mics.MicroService;

public class ShoeFactoryService extends MicroService{
	
	private Integer currentTick;
	private Map<ManufacturingOrderRequest,Integer> ordersFinalTick;
	private int totalTicks;
	
	public ShoeFactoryService(String name){
		super(name);
		currentTick=1;
		totalTicks=0;
		ordersFinalTick=new ConcurrentHashMap<ManufacturingOrderRequest,Integer>();
	}

	@Override
	protected void initialize() {	
		this.subscribeBroadcast(TerminationBroadcast.class, terB->{
			this.terminate();
		});
		
		logger.log(Level.INFO,"Shoe Factory service " + getName() + " started");
		this.subscribeBroadcast(TickBroadcast.class, b->{			//getting current tick
			currentTick=b.getTick();
			if(totalTicks>0){
				totalTicks--;
				for(Entry<ManufacturingOrderRequest,Integer> e : ordersFinalTick.entrySet()){
					if(e.getValue()<=currentTick){
						Receipt rec = new Receipt(this.getName(), "Store", e.getKey().getType(), false, currentTick, currentTick-e.getKey().getAmount()-1, e.getKey().getAmount());
						this.complete(e.getKey(),rec);
					}
				}
			}
		});
		this.subscribeRequest(ManufacturingOrderRequest.class, manuReq->{			//subscribing to manufacturing requests
			int finishTick = currentTick+manuReq.getAmount()+1;	
			ordersFinalTick.put(manuReq, finishTick);
			totalTicks+=manuReq.getAmount()+1;
		});
	}
}
