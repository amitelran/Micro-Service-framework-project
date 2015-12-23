package bgu.spl.app;

import bgu.spl.mics.Request;

public class RestockRequest implements Request<Boolean> {
	private String shoeType;
	private int amount;
	
	public RestockRequest(String shoeType, int amount){
		this.shoeType=shoeType;
		this.amount=amount;
	}
	
	public String getShoeType(){return shoeType;}
	
	public int getAmount(){return amount;}
}
