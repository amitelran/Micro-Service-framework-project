package bgu.spl.app;

import bgu.spl.app.Store.BuyResult;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.example.messages.ExampleRequest;
import bgu.spl.mics.impl.MessageBusImpl;

public class SellingService extends MicroService {

	public SellingService(String name){
		super(name);
		
	}

	@Override
	protected void initialize() {
		System.out.println("Selling Service " + getName() + " started");
		subscribeRequest(PurchaseOrderRequest.class, purReq -> {
			BuyResult result = Store.getInstance().take(purReq.getType(), purReq.onlyOnDiscount());
			if (result == BuyResult.Regular_Price){
				Receipt rec = new Receipt(this.getName(), name, name, terminated, 0, 0, 0)
			}
			else {
				if (result == BuyResult.Discounted_Price){
					//
				}
				else{
					if (result == BuyResult.Not_On_Discount){
						
					}
					else{
						if (result == BuyResult.Not_In_Stock){
							
						}
					}
				}
			
           
            }
        });
		
	}

	
	
}
