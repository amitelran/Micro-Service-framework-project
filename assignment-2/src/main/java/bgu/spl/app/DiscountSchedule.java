package bgu.spl.app;

/**
 * An object which describes a schedule of a single discount that the manager will add to a specific
 * shoe at a specific tick.
 */
public class DiscountSchedule {
	private String shoeType;
	private int tick;
	private int amount;
	
	
	/**
	 * @param shoeType - the shoe intended for discount
	 * @param tick - the tick on which the discount should be broadcasted and activated
	 * @param amount - the amount of shoes on discount from the specific shoeType given
	 */
	public DiscountSchedule(String shoeType,int tick,int amount){
		this.shoeType=shoeType;
		this.tick=tick;
		this.amount=amount;
	}
	
	/**
	 * @return shoeType for the specific {@code DiscountSchedule}.
	 */
	public String getShoeType(){return shoeType;}
	
	/**
	 * @return tick from which the discount is available for the specific {@code DiscountSchedule}.
	 */
	public int getTick(){return tick;}
	
	/**
	 * @return amount of shoes on discount for the specific {@code DiscountSchedule}.
	 */
	public int getAmount(){return amount;}
}
