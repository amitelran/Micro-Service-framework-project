package bgu.spl.app;

public class NoShoesException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String shoeType;
	private int amount;
	
	public NoShoesException(String shoeType, int amount){
		this.shoeType=shoeType;
		this.amount=amount;
	}

	public String getShoeType() {
		return shoeType;
	}

	public int getAmount() {
		return amount;
	}
	
}
