package bgu.spl.app;

import bgu.spl.mics.MicroService;

public class ShoeFactoryService extends MicroService{
	
	private int currentTick;
	
	public ShoeFactoryService(String name){
		super(name);
		currentTick=1;
	}

	@Override
	protected void initialize() {
		System.out.println("Shoe Factory service " + getName() + " started");
		this.subscribeBroadcast(TickBroadcast.class, b->{			//getting current tick
			currentTick=b.getTick();
		});
		this.subscribeRequest(ManufacturingOrderRequest.class, manuReq->{			//subscribing to manufacturing requests
			int finishTick = currentTick+manuReq.getAmount()+1;						
			while (currentTick != finishTick){
				System.out.println("Manufacturing request for " + manuReq.getAmount() + " " + manuReq.getType() + "...");
			}
			Receipt rec = new Receipt("Shoe Factory", "Store", manuReq.getType(), false, finishTick, finishTick-manuReq.getAmount()-1, manuReq.getAmount());
			
		});
		
	}
	
	

}
