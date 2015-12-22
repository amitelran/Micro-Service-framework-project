package bgu.spl.app;

public class ShoeStorageInfo {
	
	private final String shoeType;
	private int amountOnStorage;
	private int discountedAmount;
	
	public ShoeStorageInfo(String type, int amount, int discounted){
		shoeType=type;
		amountOnStorage=amount;
		discountedAmount=discounted;
	}
	
	public int getAmountOnStorage(){
		return amountOnStorage;
	}
	
	public int getDiscountedAmountOnStorage(){
		return discountedAmount;
	}
	
	public synchronized void sellShoe() throws Exception{
		if (amountOnStorage>0)
			amountOnStorage--;
		else throw new Exception("No shoes on storage");
	}
	
	public synchronized void sellDiscountedShoe() throws Exception{
		if (discountedAmount>0){
			amountOnStorage--;
			discountedAmount--;
		}
		else throw new Exception("No discounted shoes on storage");
	}
	
	
}

