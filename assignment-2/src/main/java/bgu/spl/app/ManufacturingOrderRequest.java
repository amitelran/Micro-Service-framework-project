package bgu.spl.app;

import bgu.spl.mics.*;

public class ManufacturingOrderRequest implements Request<Receipt> {
	private String shoeType;
	private int amount;
	
	public ManufacturingOrderRequest(String shoeType,int amount){
		this.shoeType=shoeType;
		this.amount=amount;
	}
	
	public String getType(){return shoeType;}
	public int getAmount(){return amount;}
}
