package bgu.spl.app;

import bgu.spl.app.Store.BuyResult;
import bgu.spl.mics.MicroService;


public class SellingService extends MicroService {
	
	private int currentTick;

	public SellingService(String name){
		super(name);
		currentTick=1;//
		
	}

	@Override
	protected void initialize() {
		System.out.println("Selling Service " + getName() + " started");
		this.subscribeBroadcast(TickBroadcast.class, b->{
			currentTick=b.getTick();
		});
		subscribeRequest(PurchaseOrderRequest.class, purReq -> {
			BuyResult result;
			try {
					result = Store.getInstance().take(purReq.getType(), purReq.onlyOnDiscount());
				if (result == BuyResult.Regular_Price){
					Receipt rec = new Receipt(this.getName(), purReq.getSenderName(), purReq.getType(), false, this.currentTick, purReq.getRequestedTime(), 1);
					Store.getInstance().file(rec);
					this.complete(purReq,rec);
				}
				else if (result == BuyResult.Discounted_Price){
					Receipt rec = new Receipt(this.getName(), purReq.getSenderName(), purReq.getType(), true, this.currentTick, purReq.getRequestedTime(), 1);
					Store.getInstance().file(rec);
					this.complete(purReq,rec);
				}
				else if (result == BuyResult.Not_On_Discount){
							this.complete(purReq,null);
						}
				else{	// means result == BuyResult.Not_In_Stock
								
					this.sendRequest(new RestockRequest(purReq.getType(),1), ans -> {
						if (ans==false){
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
	}
}
