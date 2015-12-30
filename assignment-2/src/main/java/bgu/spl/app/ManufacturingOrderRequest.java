package bgu.spl.app;

import bgu.spl.mics.*;

/**
 * A request that is sent from the store to a factory when the the store manager wants that a shoe factory
 * will manufacture shoe for the store.
 * Its response type expected to be a Receipt.
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
	 * getter set to return the shoeType requested for manufacture for the specific manufacturing request
	 */
	public String getType(){return shoeType;}
	
	/**
	 * getter set to return the amount of shoes from shoeType requested for manufacture for the 
	 * specific manufacturing request
	 */
	public int getAmount(){return amount;}
}
