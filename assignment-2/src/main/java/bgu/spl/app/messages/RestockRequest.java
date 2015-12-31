package bgu.spl.app.messages;

import bgu.spl.mics.Request;

/**
 * A request that is sent by the {@code SellingService} to the store manager so that he
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
	 * @return ShoeType for the specific {@code RestockRequest}.
	 */
	public String getShoeType(){return shoeType;}
	
	/**
	 * @return Amount of shoeType shoes for the specific {@code RestockRequest}.
	 */
	public int getAmount(){return amount;}
}
