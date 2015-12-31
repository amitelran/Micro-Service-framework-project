package bgu.spl.app.microservices;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

import bgu.spl.app.messages.NewDiscountBroadcast;
import bgu.spl.app.messages.PurchaseOrderRequest;
import bgu.spl.app.messages.TerminationBroadcast;
import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.app.schedules.PurchaseSchedule;
import bgu.spl.mics.MicroService;

/**
 * This micro-service describes one client (as if he is connected to the web-site).
 * In order to get notified when a new discount is available, the client is subscribed to the 
 * {@code NewDiscountBroadcast} message. If the client finishes receiving all its purchases and have nothing in its
 * wishList it immediately terminates.
 */
public class WebsiteClientService extends MicroService {
	
	private List<PurchaseSchedule> purchaseSchedule;
	private Set<String> wishList;
	private int currentTick;
	private CyclicBarrier barrier;

	/**
	 * The constructor sets the {@code WebsiteClientService} name, purchase schedule, starting tick,
	 * wish list and a shared {@link CyclicBarrier}.
	 * @param name - the name of the {@code WebsiteClientService}
	 * @param purchaseSchedule - a list which represents the purchase requests schedule
	 * @param wishList - a set of strings contains name of shoe types that the client will buy only when 
	 * there is a discount on them (and immediately when he found out of such discount).
	 * Once the client bought a shoe from its wish list - he removes it from the list 
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
	public WebsiteClientService(String name,List<PurchaseSchedule> purchaseSchedule,Set<String> wishList,CyclicBarrier barrier){
		super(name);
		this.purchaseSchedule=purchaseSchedule;
		this.wishList=wishList;
		currentTick=1;
		this.barrier = barrier;
	}

	/**
	 * Initializes the {@code WebsiteClientService}.
	 * It subscribes the {@code WebsiteClientService} for {@code TickBroadcast}, {@code NewDiscountBroadcast}
	 * and {@code TerminationBroadcast}.
	 * Every tick the {@code WebsiteClientService} checks its purchase schedule, and if there is a request to be sent
	 * at the current global tick, it sends a purchase request.
	 * When receiving a {@code NewDiscountBroadcast}, the {@code WebsiteClientService} checks its wishlist,
	 * and if there is a desired discounted shoe on its wishlist that fits the {@code NewDiscountBroadcast},
	 * the client sends a purchase request for the discounted shoe.
	 */
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
			readPurchaseSchedule();
		});
		try {
			barrier.await();
		} catch (Exception e) {}
	}
	
	private void readPurchaseSchedule(){
		Iterator<PurchaseSchedule> it=purchaseSchedule.iterator();
		PurchaseSchedule ps;
		while(it.hasNext()){
			ps=it.next();
			if(ps.getTick()<=currentTick){
				it.remove();
				sendPurchseRequest(ps.getShoeType(),false);
			}
		}
	}
	
	private void sendPurchseRequest(String shoeType,boolean onlyDiscount){
		log("tick #" + currentTick + ": " + this.getName() + " is sending a Purchase Request for: " + shoeType);
		sendRequest(new PurchaseOrderRequest(shoeType, 1, onlyDiscount,this.getName(),currentTick), t->{
			if(t==null){
				log("tick #" + currentTick + ": " + this.getName() + " could not buy " + shoeType + "");
			}
			else
				log("tick #" + currentTick + ": " + this.getName()
				+ " successfully bought " +t.getType() + " from " + t.getSeller() + " (requested on tick# "+t.getRequestTick() +")");
			if(t!=null&&onlyDiscount==true)
				wishList.remove(shoeType);
		});
	}
	
	/**
	 * A setter which sets the shared {@link CyclicBarrier} to the {@code WebsiteClientService}.
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}
}