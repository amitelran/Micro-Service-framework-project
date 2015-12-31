package bgu.spl.app;

import bgu.spl.mics.*;

/**
 * A request that is sent from the store to a factory when the store manager wants that a shoe factory
 * will manufacture shoe for the store.
 * Its response type expected to be a {@code Receipt}.
 */
public class ManufacturingOrderRequest implements Request<Receipt> {
	private String shoeType;
	private int amount;
	
	/**
	 * @param shoeType - the shoe requested for manufacturing
	 * @param amount - the amount of shoes of given shoeType requested for manufacturing
	 */
	public ManufacturingOrderRequest(String shoeType,int amount){
		this.shoeType=shoeType;
		this.amount=amount;
	}
	
	/**
	 * @return Shoe type.
	 */
	public String getType(){return shoeType;}
	
	/**
	 * @return Amount of shoes from shoeType requested for manufacture for the 
	 * specific manufacturing request.
	 */
	public int getAmount(){return amount;}
}
