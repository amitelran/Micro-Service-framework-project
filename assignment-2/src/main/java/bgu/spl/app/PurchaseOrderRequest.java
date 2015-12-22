package bgu.spl.app;

import bgu.spl.mics.*;

public class PurchaseOrderRequest implements Request<Receipt> {
	private String shoeType;
	private int amount;
	private boolean onlyOnDiscount;
	
	public PurchaseOrderRequest(String shoeType,int amount,boolean onlyOnDiscount){
		this.shoeType=shoeType;
		this.amount=amount;
		this.onlyOnDiscount=onlyOnDiscount;
	}
	
	public String getType(){return shoeType;}
	public int getAmount(){return amount;}
	public boolean onlyOnDiscount(){return onlyOnDiscount;}
}
