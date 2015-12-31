package bgu.spl.app;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link thread safe singleton} which holds a collection of the shoe types being offered and a collection of 
 * the receipts issued to and by the store.
 */
public class Store {

	private ConcurrentHashMap<String,ShoeStorageInfo> shoesInfo;	//holds information for each shoe type <key=shoeType,value=ShoeStorageInfo>
	private List<Receipt> issuedReceipts;		//list of receipts issued to and by the store <key=shoeType,value=Receipt>
	
	private static class SingletonHolder {
        private static Store instance = new Store();
    }
	
	public static Store getInstance() {
        return SingletonHolder.instance;
    }
	 
	/**
	 * @param shoesInfo - a {@link ConcurrentHashMap} which holds the shoe types as a key and the shoes information
	 * as value for each shoe type.
	 * @param issuedReceipts - a {@link LinkedList} of receipts which holds the receipts issued to and by the store.
	 */
	private Store(){
		shoesInfo=new ConcurrentHashMap<String,ShoeStorageInfo>();
	 	issuedReceipts=new LinkedList<Receipt>();
	}
	 
	/**
	 * Method which receives shoes information and initializing the store storage before execution
	 * of the store with the given information.
	 * @param storage - a collection of shoes information
	 */
	public void load(ShoeStorageInfo[] storage){
		for (int i=0; i<storage.length; i++){
			shoesInfo.put(storage[i].getName(), storage[i]);
		}
	}
	 
	/**
	 * An {@link Enum} which represents all the possible outcomes of purchase execution.
	 */
	public enum BuyResult{
		Not_In_Stock, Not_On_Discount, Regular_Price, Discounted_Price;
	}
	 
	/**
	 * Method that receives a discounted or regular shoe to take from the storage and returns 
	 * the outcome based on the storage status and purchase demand.
	 * @param shoeType - the shoe to take from storage
	 * @param onlyDiscount - a boolean value which represents whether the requested shoe is 
	 * demanded discounted or not. 
	 * @return A {@code BuyResult} matching value
	 * @throws {@code NoShoesException} 
	 */
	public synchronized BuyResult take(String shoeType, boolean onlyDiscount) throws Exception{
		ShoeStorageInfo shoe = shoesInfo.get(shoeType);
		if (shoe!=null&&shoe.getAmountOnStorage()>0){
			if (shoe.getDiscountedAmountOnStorage()>0){
				shoe.sellDiscountedShoe();
				return BuyResult.Discounted_Price;
			}
			else if(onlyDiscount) {
				return BuyResult.Not_On_Discount;
			}
			else{
				shoe.sellShoe();
				return BuyResult.Regular_Price;
			}
		}
		else{							//no shoes in stock
			return BuyResult.Not_In_Stock;
		}
	}

	/**
	 * Method which adds given amount of given shoe to the store storage
	 * @param shoeType - the shoes to be added to the storage
	 * @param amount - the amount of the shoe to be added to the storage
	 */
	public synchronized void add(String shoeType, int amount){	
		if (!shoesInfo.containsKey(shoeType)){
			ShoeStorageInfo shoe = new ShoeStorageInfo(shoeType,amount,0);
			shoesInfo.put(shoeType,shoe);
		}
		else{
			shoesInfo.get(shoeType).addAmount(amount);
		}
	}

	/**
	 * Method which tries to add a discounted amount of shoe to the storage, and @throws NoShoesException
	 * if the regular amount of the same shoe in the storage is lower than the given discounted amount to be added.
	 * @param shoeType - the type of shoe to add the discounted amount in the storage to
	 * @param amount - the discounted amount to add to storage of the given shoe type
	 */
	public synchronized void addDiscount(String shoeType, int amount) throws NoShoesException{
		if (shoesInfo.containsKey(shoeType)&&shoesInfo.get(shoeType).getAmountOnStorage()-shoesInfo.get(shoeType).getDiscountedAmountOnStorage()>=amount){
			shoesInfo.get(shoeType).addDiscountedAmount(amount);
		}
		else{
			throw new NoShoesException(shoeType,amount);
		}
	}

	/**
	 * Method used to add a given {@code Receipt} to the issued receipts list of the store.
	 * @param receipt - the receipt to add to the list
	 */
	public synchronized void file(Receipt receipt){
		issuedReceipts.add(receipt);
	}
	 
	/**
	 * This method prints to the standard output the following information:
	 * • For each item on stock - its name, amount and discountedAmount
	 * • For each receipt filed in the store - all of its fields
	 * The method prints the data in a structured and organized format.
	 */
	public synchronized void print(){		
		int i=1;
		System.out.println("============================Store Info============================");
		if(shoesInfo.isEmpty())
			System.out.println("No shoes on storage yet.");
		else{
			for (ShoeStorageInfo shoe : shoesInfo.values()){
				System.out.print("Stock: Shoe Model #"+i+":\n\t");
				shoe.print();
				i++;
			}
		}
		if(!issuedReceipts.isEmpty()){
			System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
			for (Receipt rec : issuedReceipts){
				rec.print();
				System.out.println();
			}
		}
		System.out.println("Total Receipts: "+issuedReceipts.size());
		System.out.println("==================================================================");
	}
	
}