package bgu.spl.app.schedules;

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
	 * @return ShoeType for the specific purchase.
	 */
	public String getShoeType(){
		return shoeType;
	}
	
	/**
	 * @return Tick on which to send the {@code PurchaseOrderRequest}.
	 */
	public int getTick(){
		return tick;
	}
}
