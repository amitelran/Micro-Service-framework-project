package bgu.spl.app;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import bgu.spl.mics.MicroService;

public class WebsiteClientService extends MicroService {
	
	private List<PurchaseSchedule> purchaseSchedule;
	private Set<String> wishList;
	private int currentTick;

	public WebsiteClientService(String name,List<PurchaseSchedule> purchaseSchedule,Set<String> wishList) {
		super(name);
		this.purchaseSchedule=purchaseSchedule;
		this.wishList=wishList;
		currentTick=1;
	}

	@Override
	protected void initialize() {	
		this.subscribeBroadcast(TerminationBroadcast.class, terB->{
			this.terminate();
		});
		
		this.subscribeBroadcast(NewDiscountBroadcast.class, b->{
			String shoeType=b.getShoeType();
			if(wishList.contains(shoeType)){
				sendPurchseRequest(shoeType,true);
			}
		});
		
		this.subscribeBroadcast(TickBroadcast.class, b->{
			currentTick=b.getTick();
			for(PurchaseSchedule pS : purchaseSchedule){
				if(pS.getTick()<=currentTick){
					purchaseSchedule.remove(pS);
					sendPurchseRequest(pS.getShoeType(),false);
				}
			}
		});
	}
	
	private void sendPurchseRequest(String shoeType,boolean onlyDiscount){
		sendRequest(new PurchaseOrderRequest(shoeType, 1, onlyDiscount,this.getName(),currentTick), t->{
			logger.log(Level.INFO,"tick# " + t.getIssuedTick() + ": " + this.getName()
				+ ((t==null) ? " could not buy " : " successfuly bought ") +t.getType()
					+ " from " + t.getSeller() + " (requested on tick# "+t.getRequestTick() +")");
			if(t!=null&&onlyDiscount==true)
				wishList.remove(shoeType);
		});
	}
}