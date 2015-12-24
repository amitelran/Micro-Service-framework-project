package bgu.spl.app;

import bgu.spl.mics.*;

public class PurchaseOrderRequest implements Request<Receipt> {
	private String shoeType;
	private int amount;
	private boolean onlyOnDiscount;
	private int tick;
	private String senderName;
	
	public PurchaseOrderRequest(String shoeType,int amount,boolean onlyOnDiscount,String senderName,int tick){
		this.shoeType=shoeType;
		this.amount=amount;
		this.onlyOnDiscount=onlyOnDiscount;
		this.senderName=senderName;
		this.tick=tick;
	}
	
	public String getType(){return shoeType;}
	public int getAmount(){return amount;}
	public boolean onlyOnDiscount(){return onlyOnDiscount;}
	public String getSenderName(){return senderName;}
	public int getRequestedTime(){return tick;}
}
