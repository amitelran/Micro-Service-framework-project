package bgu.spl.app;

/**
 * @throws NoShoesException a specific exception meant to represent a situation in which discounted shoes are 
 * being added to the storage, while the amount of shoes of the same type in the storage is smaller or 
 * doesn't exist.
 */
public class NoShoesException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String shoeType;
	private int amount;
	
	/**
	 * @param shoeType - the type of the shoe referred to in the exception
	 * @param amount - the discounted amount of the referred shoe trying to be added 
	 */
	public NoShoesException(String shoeType, int amount){
		this.shoeType=shoeType;
		this.amount=amount;
	}

	/**
	 * @return ShoeType for the specific exception.
	 */
	public String getShoeType() {
		return shoeType;
	}

	/**
	 * @return Discounted amount trying to be added for the specific exception.
	 */
	public int getAmount() {
		return amount;
	}
	
}
