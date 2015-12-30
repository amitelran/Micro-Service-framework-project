package bgu.spl.app;

/**
 * An object which describes a schedule of a single client-purchase at a specific tick.
 */
public class PurchaseSchedule {
	private String shoeType;
	private int tick;
	
	/**
	 * @param shoeType - the type of shoe to purchase
	 * @param tick - the tick number to send the PurchaseOrderRequest at
	 */
	public PurchaseSchedule(String shoeType,int tick){
		this.shoeType=shoeType;
		this.tick=tick;
	}
	
	/**
	 * getter set to return the shoeType for the specific purchase
	 */
	public String getShoeType(){
		return shoeType;
	}
	
	/**
	 * getter set to return the tick on which to send the PurchaseOrderRequest
	 */
	public int getTick(){
		return tick;
	}
}
