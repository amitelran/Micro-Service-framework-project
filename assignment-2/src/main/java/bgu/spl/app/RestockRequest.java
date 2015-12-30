package bgu.spl.app;

import bgu.spl.mics.Request;

/**
 * A request that is sent by the selling service to the store manager so that he
 * will know that he needs to order new shoes from a factory.
 */
public class RestockRequest implements Request<Boolean> {
	private String shoeType;
	private int amount;
	
	/**
	 * @param shoeType - the shoe requested for restock
	 * @param amount - the amount of shoeType shoes requested for restock
	 */
	public RestockRequest(String shoeType, int amount){
		this.shoeType=shoeType;
		this.amount=amount;
	}
	
	/**
	 * getter set to return the shoeType for the specific restockRequest
	 */
	public String getShoeType(){return shoeType;}
	
	/**
	 * getter set to return the amount of shoeType shoes for the specific restockRequest
	 */
	public int getAmount(){return amount;}
}
