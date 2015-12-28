package bgu.spl.app;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

import bgu.spl.mics.MicroService;

public class ManagementService extends MicroService {
	private CyclicBarrier barrier;
	
	private class orderedAndReserved{
		int ordered;
		int reserved;
		ConcurrentLinkedQueue<RestockRequest> requests;
		
		public orderedAndReserved(int ordered,int reserved,RestockRequest req){
			this.ordered=ordered;
			this.reserved=reserved;
			requests=new ConcurrentLinkedQueue<RestockRequest>();
			requests.add(req);
		}
	}
	
	private List<DiscountSchedule> DiscountSchedule;
	private int currentTick;
	private Map<String,orderedAndReserved> orders;
	
	public ManagementService(List<DiscountSchedule> DiscountSchedule,CyclicBarrier barrier) {
		super("manager");
		this.DiscountSchedule=DiscountSchedule;
		currentTick=1;
		orders = new ConcurrentHashMap<String,orderedAndReserved>();
		this.barrier=barrier;
	}

	@Override
	protected void initialize() {
		log("Managament Service is starting");
		this.subscribeBroadcast(TerminationBroadcast.class, terB->{
			log("tick #"+(currentTick+1)+": manager got a Termination Broadcast, waiting for all services to gracefully terminate ...");
			try {
				barrier.await();
			} catch (Exception e) {}
			log("everyone terminated gracefully... printing stock info and receipts.");
			Store.getInstance().print();
			this.terminate();
		});
		
		this.subscribeBroadcast(TickBroadcast.class, b->{
			currentTick=b.getTick();
			for(DiscountSchedule dS : DiscountSchedule){
				if(dS.getTick()<=currentTick){
					DiscountSchedule.remove(dS);
					try {
						Store.getInstance().addDiscount(dS.getShoeType(), dS.getAmount());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.sendBroadcast(new NewDiscountBroadcast(dS.getShoeType(), dS.getAmount()));
				}
			}
		});
		
		this.subscribeRequest(RestockRequest.class, req -> {
			log("tick #"+currentTick+": "+this.getName() + " got a restock request");
			synchronized(orders){
				String shoe=req.getShoeType();
				int amount=req.getAmount();
				orderedAndReserved order = orders.get(shoe);
				if( order==null || order.ordered-order.reserved<amount ){
					int newOrderAmount=currentTick%5+1;
					if(order==null){
						order=new orderedAndReserved(newOrderAmount,1,req);
						orders.put(shoe, order);
					}
					else{
						order.ordered+=newOrderAmount;
						order.reserved++;
						order.requests.add(req);
					}
					boolean requestSucceeded=sendManufacturingRequest(shoe,newOrderAmount);
					if(!requestSucceeded){
						log("tick #"+currentTick+": no factories could manufacture new shoes :(");
						this.complete(req, false);
					}
				}
				else{
					log("tick #"+currentTick+": " + req.getShoeType() + " already ordered from factory with sufficient amount");
					order.requests.add(req);
				}
			}
		});
		try {
			barrier.await();
		} catch (Exception e) {}
	}
	
	private boolean sendManufacturingRequest(String shoeType,int amount){
		log("tick #"+currentTick+": " + this.getName() + " sends a manufacturing order request for "+ amount +" pairs of "+shoeType);
		boolean ans=this.sendRequest(new ManufacturingOrderRequest(shoeType, amount), res->{
			synchronized(orders){				
				if(res!=null){
					Store.getInstance().file(res);
					if(res.getAmountSold()-orders.get(shoeType).reserved>0)
						Store.getInstance().add(shoeType, res.getAmountSold()-orders.get(shoeType).reserved);
					
					for(int i=0;i<res.getAmountSold()&&!orders.get(shoeType).requests.isEmpty();i++)
						this.complete(orders.get(shoeType).requests.poll(), true);
					
					if(orders.get(shoeType).requests.isEmpty())orders.remove(shoeType);
				}
				else{
					for(RestockRequest rr : orders.get(shoeType).requests){
						this.complete(rr, false);
					}
					orders.remove(shoeType);
				}
			}
		});
		return ans;
	}
	
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}

}
