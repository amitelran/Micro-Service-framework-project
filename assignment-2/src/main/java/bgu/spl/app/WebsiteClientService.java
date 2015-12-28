package bgu.spl.app;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

import bgu.spl.mics.MicroService;

public class WebsiteClientService extends MicroService {
	
	private List<PurchaseSchedule> purchaseSchedule;
	private Set<String> wishList;
	private int currentTick;
	private CyclicBarrier barrier;

	public WebsiteClientService(String name,List<PurchaseSchedule> purchaseSchedule,Set<String> wishList,CyclicBarrier barrier) {
		super(name);
		this.purchaseSchedule=purchaseSchedule;
		this.wishList=wishList;
		currentTick=1;
		this.barrier = barrier;
	}

	@Override
	protected void initialize() {
		log("Website Client Service " + getName() + " is starting");
		this.subscribeBroadcast(TerminationBroadcast.class, terB->{
			try {
				barrier.await();
			} catch (Exception e) {}
			this.terminate();
		});
		
		this.subscribeBroadcast(NewDiscountBroadcast.class, b->{
			log("tick #" + currentTick + ": " + this.getName()+" got NewDiscountBroadcast on shoe type "+b.getShoeType());
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
		try {
			barrier.await();
		} catch (Exception e) {}
	}
	
	private void sendPurchseRequest(String shoeType,boolean onlyDiscount){
		log("tick #" + currentTick + ": " + this.getName() + " is sending a Purchase Request for: " + shoeType);
		sendRequest(new PurchaseOrderRequest(shoeType, 1, onlyDiscount,this.getName(),currentTick), t->{
			if(t==null){
				log("tick #" + currentTick + ": " + this.getName() + " could not buy " + shoeType + "");
			}
			else
				log("tick #" + currentTick + ": " + this.getName()
				+ " successfuly bought " +t.getType() + " from " + t.getSeller() + " (requested on tick# "+t.getRequestTick() +")");
			if(t!=null&&onlyDiscount==true)
				wishList.remove(shoeType);
		});
	}
	
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}
}