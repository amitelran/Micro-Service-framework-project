package bgu.spl.app.microservices;

import java.util.concurrent.CyclicBarrier;

import bgu.spl.app.Receipt;
import bgu.spl.app.Store;
import bgu.spl.app.Store.BuyResult;
import bgu.spl.app.messages.PurchaseOrderRequest;
import bgu.spl.app.messages.RestockRequest;
import bgu.spl.app.messages.TerminationBroadcast;
import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.mics.MicroService;

/**
 * A micro-service which handles {@code PurchaseOrderRequest}.
 * When the {@code SellingService} receives a {@code PurchaseOrderRequest}, it handles it by trying
 * to take the required shoe from the storage. If it succeeded it creates a receipt, files it in the store
 * and passes it to the client as  the result of completing the {@code PurchaseOrderRequest}.
 * If there were no shoes of the requested type on stock, the {@code SellingService} will send a 
 * {@code RestockRequest}. If the request completed with the value “false”, 
 * the {@code SellingService} will complete the {@code PurchaseOrderRequest} with the value of “null” to indicate
 * to the client that the purchase was unsuccessful. If the client indicates in the order that he wishes to
 * get this shoe only on discount and no more discounted shoes are left, then it will complete the client
 * request with null result (to indicate to the client that the purchase was unsuccessful).
 */
public class SellingService extends MicroService {
	
	private int currentTick;
	private CyclicBarrier barrier;

	/**
	 * The constructor sets the {@code SellingService} name, shared {@link CyclicBarrier} and starting tick
	 * (defaulted as 1).
	 * @param name - the name of the {@code SellingService}
	 * @param barrier - the shared program {@link CyclicBarrier}
	 */
	public SellingService(String name,CyclicBarrier barrier){
		super(name);
		currentTick=1;
		this.barrier = barrier;
		
	}

	/**
	 * The method which initializes the {@code SellingService}.
	 * The method subscribes the {@code SellingService} to the broadcast messages of {@code TickBroadcast}
	 * and {@code TerminationBroadcast}.
	 * The method sends {@code PurchaseOrderRequests}, gets an answer for a request, and proceeds according
	 * to the given response:
	 * If the the {@code SellingService} managed to buy the shoe, a receipt is filed. Else, a {@code RestockRequest}
	 * is sent, and according to the {@code RestockRequest} response, either a receipt is filed for purchasing the
	 * shoe, or the purchase request is returned with a null value, indicating an unsuccessful purchase.
	 */
	@Override
	protected void initialize() {
		log(getName() + " is starting");
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
			purchaseRequestHandler(purReq);
        });
		try {
			barrier.await();
		} catch (Exception e) {}
	}
	
	private void purchaseRequestHandler(PurchaseOrderRequest purReq){
		log(curTickLogPrefix()+ this.getName() + " got a purchase request for: "+purReq.getType() + ( (purReq.onlyOnDiscount()) ? " (only on discount!)" : ""));
		BuyResult result;
		try {
			result = Store.getInstance().take(purReq.getType(), purReq.onlyOnDiscount());
			if (result == BuyResult.Regular_Price || result == BuyResult.Discounted_Price){
				Receipt rec = new Receipt(this.getName(), purReq.getSenderName(), purReq.getType(), ( (result == BuyResult.Discounted_Price) ? true : false) , this.currentTick, purReq.getRequestedTime(), 1);
				log(curTickLogPrefix() + this.getName() + " sold one pair of " + purReq.getType() + " (on discount: " + ( (result == BuyResult.Discounted_Price) ? "yes" : "no") +")");
				Store.getInstance().file(rec);
				this.complete(purReq,rec);
			}
			else if (result == BuyResult.Not_On_Discount){
				log(curTickLogPrefix() + this.getName() + " could not sell " + purReq.getType() + " because it doesn't have a discount");
				this.complete(purReq,null);
			}
			else if(!purReq.onlyOnDiscount()){
				this.sendRequest(new RestockRequest(purReq.getType(),1), ans -> {
					if (ans==false){
						log(curTickLogPrefix() + this.getName() + " could not sell " + purReq.getType() + " because restock request got refused");
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
		catch (Exception e) {e.printStackTrace();}
	}
	
	private String curTickLogPrefix(){
		return "tick #" + currentTick + ": ";
	}
	
	/**
	 * @param barr - a shared {@link CyclicBarrier} for all services.
	 */
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}
}
