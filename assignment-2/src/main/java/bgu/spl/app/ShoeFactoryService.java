package bgu.spl.app;

import java.util.concurrent.CountDownLatch;

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
			int manufactureTime = manuReq.getAmount()+1;							//counting ticks for manufacturing
			CountDownLatch count = new CountDownLatch(manufactureTime);
			count.countDown();
		});
		
	}
	
	

}
