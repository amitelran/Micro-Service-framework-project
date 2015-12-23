package bgu.spl.app;

import bgu.spl.mics.*;

public class PurchaseOrderRequest implements Request<Receipt> {
	private String shoeType;
	private int amount;
	private boolean onlyOnDiscount;
	private int tick;
	
	public PurchaseOrderRequest(String shoeType,int amount,boolean onlyOnDiscount,int tick){
		this.shoeType=shoeType;
		this.amount=amount;
		this.onlyOnDiscount=onlyOnDiscount;
		this.tick=tick;
	}
	
	public String getType(){return shoeType;}
	public int getAmount(){return amount;}
	public boolean onlyOnDiscount(){return onlyOnDiscount;}
	public int getRequestedTime(){return tick;}
}
