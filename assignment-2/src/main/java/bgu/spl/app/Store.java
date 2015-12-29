package bgu.spl.app;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class Store {

	private ConcurrentHashMap<String,ShoeStorageInfo> shoesInfo;	//holds information for each shoe type <key=shoeType,value=ShoeStorageInfo>
	private List<Receipt> issuedReceipts;		//list of receipts issued to and by the store <key=shoeType,value=Receipt>
	
	private static class SingletonHolder {
        private static Store instance = new Store();
    }
	
	public static Store getInstance() {
        return SingletonHolder.instance;
    }
	 
	private Store(){
		shoesInfo=new ConcurrentHashMap<String,ShoeStorageInfo>();
	 	issuedReceipts=new LinkedList<Receipt>();
	}
	 
	public void load(ShoeStorageInfo[] storage){		//initializing store storage before execution with given info in array
		for (int i=0; i<storage.length; i++){
			shoesInfo.put(storage[i].getName(), storage[i]);
		}
	}
	 
	public enum BuyResult{
		Not_In_Stock, Not_On_Discount, Regular_Price, Discounted_Price;
	}
	 
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
			if(shoe!=null&&shoe.getAmountOnStorage()==0)
				add(shoeType,0);
			return BuyResult.Not_In_Stock;
		}
	}

	public synchronized void add(String shoeType, int amount){	
		if (!shoesInfo.containsKey(shoeType)){
			ShoeStorageInfo shoe = new ShoeStorageInfo(shoeType,amount,0);
			shoesInfo.put(shoeType,shoe);
		}
		else{
			shoesInfo.get(shoeType).addAmount(amount);
		}
	}

	public synchronized void addDiscount(String shoeType, int amount) throws NoShoesException{
		if (shoesInfo.containsKey(shoeType)){
			shoesInfo.get(shoeType).addDiscountedAmount(amount);
		}
		else{
			throw new NoShoesException(shoeType,amount);
		}
	}

	public synchronized void file(Receipt receipt){
		issuedReceipts.add(receipt);
	}
	 
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