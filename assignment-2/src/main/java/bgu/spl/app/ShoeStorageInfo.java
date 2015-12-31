package bgu.spl.app;

/**
 * An object which represents information about a single type of shoe in the store.
 */
public class ShoeStorageInfo {
	
	private final String shoeType;
	private int amountOnStorage;
	private int discountedAmount;
	
	/**
	 * @param type - the shoe type data
	 * @param amount - the amount of the given shoe type on storage
	 * @param discounted - the discounted amount of the given shoe type on storage
	 */
	public ShoeStorageInfo(String type, int amount, int discounted){
		shoeType=type;
		amountOnStorage=amount;
		discountedAmount=discounted;
	}
	
	/**
	 * @return Amount of shoes on storage for certain shoe type.
	 */
	public int getAmountOnStorage(){
		return amountOnStorage;
	}
	
	/**
	 * @return Discounted amount of shoes on storage for certain shoe type.
	 */
	public int getDiscountedAmountOnStorage(){
		return discountedAmount;
	}
	
	/**
	 * @return Shoe type.
	 */
	public String getName(){
		return shoeType;
	}
	
	/**
	 * A method which checks if there are shoes on storage for a certain type of shoe, and if so, sells one of them.
	 * @throws Exception
	 */
	public synchronized void sellShoe() throws Exception{
		if (amountOnStorage>0)
			amountOnStorage--;
		else throw new Exception("No shoes from that type left on storage");
	}
	
	/**
	 * A method which checks if there are discounted shoes on storage for a certain type of shoe, and if so,
	 * sells one of them.
	 * @throws Exception
	 */
	public synchronized void sellDiscountedShoe() throws Exception{
		if (discountedAmount>0){
			amountOnStorage--;
			discountedAmount--;
		}
		else throw new Exception("No discounted shoes on storage");
	}
	
	/**
	 * A method which adds the given amount of a certain shoe to the existing amount on storage.
	 * @param addToAmount - the amount of shoes to be added to the storage of a certain type of shoe
	 */
	public synchronized void addAmount(int addToAmount){
		amountOnStorage += addToAmount;
	}
	
	/**
	 * A method which adds the given discounted amount of a certain shoe to the existing discounted amount on storage.
	 * @param addToDiscountedAmount - the amount of discounted shoes to be added to the storage of a 
	 * certain type of shoe
	 */
	public synchronized void addDiscountedAmount(int addToDiscountedAmount){
		discountedAmount +=addToDiscountedAmount;
	}
	
	/**
	 * A method which prints the storage data for a certain type of shoe in a structured format.
	 */
	public void print(){
		System.out.println("Shoe Type: "+shoeType+" | Amount on storage: "+amountOnStorage+" | Amount on discount: "+discountedAmount);
	}
	
}

