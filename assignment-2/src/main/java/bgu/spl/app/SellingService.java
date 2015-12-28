package bgu.spl.app;

import java.util.concurrent.CyclicBarrier;

import bgu.spl.app.Store.BuyResult;
import bgu.spl.mics.MicroService;


public class SellingService extends MicroService {
	
	private int currentTick;
	private CyclicBarrier barrier;

	public SellingService(String name,CyclicBarrier barrier){
		super(name);
		currentTick=1;
		this.barrier = barrier;
		
	}

	@Override
	protected void initialize() {
		log("Selling Service " + getName() + " is starting");
		this.subscribeBroadcast(TerminationBroadcast.class, terB->{
			try {
				barrier.await();
			} catch (Exception e) {}
			this.terminate();
		});
		
		this.subscribeBroadcast(TickBroadcast.class, b->{
			currentTick=b.getTick();
		});
		subscribeRequest(PurchaseOrderRequest.class, purReq -> {
			log("tick #" + currentTick + ": "+ this.getName() + " got a purchase request for: "+purReq.getType() + ( (purReq.onlyOnDiscount()) ? " (only on discount!)" : ""));
			BuyResult result;
			try {
				result = Store.getInstance().take(purReq.getType(), purReq.onlyOnDiscount());
				if (result == BuyResult.Regular_Price || result == BuyResult.Discounted_Price){
					Receipt rec = new Receipt(this.getName(), purReq.getSenderName(), purReq.getType(), ( (result == BuyResult.Discounted_Price) ? true : false) , this.currentTick, purReq.getRequestedTime(), 1);
					log("tick #" + currentTick + ": " + this.getName() + " sold one pair of " + purReq.getType() + " (on discount: " + ( (result == BuyResult.Discounted_Price) ? "yes" : "no") +")");
					Store.getInstance().file(rec);
					this.complete(purReq,rec);
				}
				else if (result == BuyResult.Not_On_Discount){
					log("tick #" + currentTick + ": " + this.getName() + " could not sell " + purReq.getType() + " because it doesn't have a discount");
					this.complete(purReq,null);
				}
				else{	// means result == BuyResult.Not_In_Stock
								
					this.sendRequest(new RestockRequest(purReq.getType(),1), ans -> {
						if (ans==false){
							log("tick #" + currentTick + ": " + this.getName() + " could not sell " + purReq.getType() + " because restock request got refused");
							this.complete(purReq, null);
						}
						else{
							Receipt rec = new Receipt(this.getName(), purReq.getSenderName(), purReq.getType(), false, this.currentTick, purReq.getRequestedTime(), 1);
							Store.getInstance().file(rec);
							this.complete(purReq,rec);
						}
					});
	            }
			}
			catch (Exception e) {
				e.printStackTrace();
			}
        });

		try {
			barrier.await();
		} catch (Exception e) {}
	}
	
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}
}
