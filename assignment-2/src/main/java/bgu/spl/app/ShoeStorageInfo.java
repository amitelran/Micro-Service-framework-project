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
	
	public String getName(){
		return shoeType;
	}
	
	public synchronized void sellShoe() throws Exception{
		if (amountOnStorage>0)
			amountOnStorage--;
		else throw new Exception("No shoes from that type left on storage");
	}
	
	public synchronized void sellDiscountedShoe() throws Exception{
		if (discountedAmount>0){
			amountOnStorage--;
			discountedAmount--;
		}
		else throw new Exception("No discounted shoes on storage");
	}
	
	public synchronized void addAmount(int addToAmount){
		amountOnStorage += addToAmount;
	}
	
	public synchronized void addDiscountedAmount(int addToDiscountedAmount){
		discountedAmount +=addToDiscountedAmount;
	}
	
	public void print(){
		System.out.println("Shoe Type: "+shoeType+" | Amount on storage: "+amountOnStorage+" | Amount on discount: "+discountedAmount);
	}
	
}

