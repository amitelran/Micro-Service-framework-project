package bgu.spl.app;

import bgu.spl.mics.Broadcast;

/**
 * A broadcast message that is sent when the manager of the store decides to have a discounted sale
 * on a specific shoe.
 */
public class NewDiscountBroadcast implements Broadcast {
	private String shoeType;
	private int amount;
	
	/**
	 * @param shoeType - the shoe intended for discount
	 * @param amount - the amount of shoes on intended for discount from the specific shoeType given
	 */
	public NewDiscountBroadcast(String shoeType,int amount){
		this.shoeType=shoeType;
		this.amount=amount;
	}
	
	/**
	 * @return ShoeType for the specific new discount.
	 */
	public String getShoeType(){return shoeType;}
	
	/**
	 * @return Amount of shoes on discount for the specific new discount.
	 */
	public int getAmount(){return amount;}
}
