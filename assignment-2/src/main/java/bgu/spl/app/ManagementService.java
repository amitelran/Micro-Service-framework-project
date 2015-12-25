package bgu.spl.app;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.MicroService;

public class ManagementService extends MicroService {
	
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
	
	public ManagementService(List<DiscountSchedule> DiscountSchedule) {
		super("manager");
		this.DiscountSchedule=DiscountSchedule;
		currentTick=1;
		orders = new ConcurrentHashMap<String,orderedAndReserved>();
	}

	@Override
	protected void initialize() {	
		this.subscribeBroadcast(TerminationBroadcast.class, terB->{
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
					this.sendRequest(new ManufacturingOrderRequest(shoe, newOrderAmount), res->{
						synchronized(orders){
							
							if(res!=null){
								Store.getInstance().file(res);
								if(res.getAmountSold()-orders.get(shoe).reserved>0)
									Store.getInstance().add(shoe, res.getAmountSold()-orders.get(shoe).reserved);
								
								for(int i=0;i<res.getAmountSold()&&!orders.get(shoe).requests.isEmpty();i++)
									this.complete(orders.get(shoe).requests.poll(), true);
								
								if(orders.get(shoe).requests.isEmpty())orders.remove(shoe);
							}
							else{
								for(RestockRequest rr : orders.get(shoe).requests){
									this.complete(rr, false);
								}
								orders.remove(shoe);
							}
						}
					});
				}
				else{
					order.requests.add(req);
				}
			}
		});
		
	}

}
