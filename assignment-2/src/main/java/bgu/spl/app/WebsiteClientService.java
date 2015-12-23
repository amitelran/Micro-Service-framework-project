package bgu.spl.app;

import java.util.List;
import java.util.Set;

import bgu.spl.mics.MicroService;

public class WebsiteClientService extends MicroService {
	
	private List<PurchaseSchedule> purchaseSchedule;
	private Set<String> wishList;

	public WebsiteClientService(String name,List<PurchaseSchedule> purchaseSchedule,Set<String> wishList) {
		super(name);
		this.purchaseSchedule=purchaseSchedule;
		this.wishList=wishList;
	}

	@Override
	protected void initialize() {	
		this.subscribeBroadcast(NewDiscountBroadcast.class, b->{
			String shoeType=b.getShoeType();
			if(wishList.contains(shoeType)){
				sendRequest(new PurchaseOrderRequest(shoeType, 1, true), t->{
					if(t!=null){
						wishList.remove(shoeType);
					}
				});
			}
		});
		
		this.subscribeBroadcast(TickBroadcast.class, b->{
			int tick=b.getTick();
			for(PurchaseSchedule pS : purchaseSchedule){
				if(pS.getTick()>=tick){
					purchaseSchedule.remove(pS);
					sendRequest(new PurchaseOrderRequest(pS.getShoeType(), 1, false), t->{
						System.out.println("tick# " + t.getIssuedTick() + ": " + this.getName()
							+ ((t==null) ? " could not buy " : " successfuly bought ") +pS.getShoeType());
					});
				}
			}
		});
	}

}
