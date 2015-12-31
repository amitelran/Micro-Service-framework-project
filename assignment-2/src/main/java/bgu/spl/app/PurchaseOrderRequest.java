package bgu.spl.app;

import bgu.spl.mics.*;

/**
 * A request that is sent when the a store client wishes to buy a shoe. Its response type expected to
 * be a Receipt. On the case the purchase was not completed successfully, null should be returned
 * as the request result.
 */
public class PurchaseOrderRequest implements Request<Receipt> {
	private String shoeType;
	private int amount;
	private boolean onlyOnDiscount;
	private int tick;
	private String senderName;
	
	/**
	 * @param shoeType - the wished shoe for purchase
	 * @param amount - the wished amount to purchase of the specified shoe type
	 * @param onlyOnDiscount - a boolean value which represents whether the client wishes to make a 
	 * purchase for a discounted shoe only
	 * @param senderName - the name of the purchase request sender
	 * @param tick - the tick on which the purchase request should be sent
	 */
	public PurchaseOrderRequest(String shoeType,int amount,boolean onlyOnDiscount,String senderName,int tick){
		this.shoeType=shoeType;
		this.amount=amount;
		this.onlyOnDiscount=onlyOnDiscount;
		this.senderName=senderName;
		this.tick=tick;
	}
	
	/**
	 * @return shoe type.
	 */
	public String getType(){return shoeType;}
	
	/**
	 * @return Desired amount for purchase of specific shoe type.
	 */
	public int getAmount(){return amount;}
	
	/**
	 * @return Boolean value which determines whether the shoe is desired only on discount.
	 */
	public boolean onlyOnDiscount(){return onlyOnDiscount;}
	
	/**
	 * @return Requester name.
	 */
	public String getSenderName(){return senderName;}
	
	/**
	 * @return Purchase request delivery tick
	 */
	public int getRequestedTime(){return tick;}
}
