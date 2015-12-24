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
			this.notify();
		});
		this.subscribeRequest(ManufacturingOrderRequest.class, manuReq->{			//subscribing to manufacturing requests
			int finishTick = currentTick+manuReq.getAmount()+1;						
			while (currentTick != finishTick){				//waiting for the finishing tick
				try {
	                this.wait();
	            } catch (InterruptedException e) {}
			}
			Receipt rec = new Receipt("Shoe Factory", "Store", manuReq.getType(), false, finishTick, finishTick-manuReq.getAmount()-1, manuReq.getAmount());
			this.complete(manuReq,rec);
		});
	}
}
