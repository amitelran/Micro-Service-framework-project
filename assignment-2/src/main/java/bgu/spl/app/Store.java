package bgu.spl.app;

import java.util.concurrent.ConcurrentHashMap;


public class Store {

	private ConcurrentHashMap<String,ShoeStorageInfo> shoesInfo;	//holds information for each shoe type <key=shoeType,value=ShoeStorageInfo>
	private ConcurrentHashMap<String,Receipt> issuedReceipts;		//list of receipts issued to and by the store <key=shoeType,value=Receipt>
	
	private static class SingletonHolder {
        private static Store instance = new Store();
    }
	
	 public static Store getInstance() {
	        return SingletonHolder.instance;
	    }
	 
	 public Store(){
		 shoesInfo=new ConcurrentHashMap<String,ShoeStorageInfo>();
		 issuedReceipts=new ConcurrentHashMap<String,Receipt>();
	 }
	 
	 public void load(ShoeStorageInfo[] storage){		//initializing store storage before execution with given info in array
		 for (int i=0; i<storage.length; i++){
			 shoesInfo.put(storage[i].getName(), storage[i]);
		 }
	 }
	 
	 public enum BuyResult{
		 Not_In_Stock, Not_On_Discount, Regular_Price, Discounted_Price;
	 }
	 
	 public BuyResult take(String shoeType, boolean onlyDiscount) throws Exception{
		 BuyResult result;
		 if (shoesInfo.containsKey(shoeType)){
			 ShoeStorageInfo shoe = shoesInfo.get(shoeType);
			 if (onlyDiscount){								//checks for only discounted shoe
				 if (shoe.getDiscountedAmountOnStorage()>0){
					 shoe.sellDiscountedShoe();
					 result = BuyResult.Discounted_Price;
					 return result;
				 }
				 else {
					 result = BuyResult.Not_On_Discount;
					 return result;
				 }
			 }
			 else {				//selling regular shoe
				 if (shoe.getDiscountedAmountOnStorage()>0){
					 shoe.sellDiscountedShoe();
					 result = BuyResult.Discounted_Price;
					 return result;
				 }
				 else {
					 shoe.sellShoe();
					 result = BuyResult.Regular_Price;
					 return result;
				 }
			 }
		 }
		 else{							//no shoes in stock
			 result = BuyResult.Not_In_Stock;
			 return result;
		 }
	 }
	 
	 public void add(String shoeType, int amount){	
		 if (!shoesInfo.containsKey(shoeType)){
			 ShoeStorageInfo shoe = new ShoeStorageInfo(shoeType,amount,0);
			 shoesInfo.put(shoeType,shoe);
		 }
		 else{
			 shoesInfo.get(shoeType).addAmount(amount);
		 }
	 }
	 
	 public void addDiscount(String shoeType, int amount){
		 if (!shoesInfo.containsKey(shoeType)){
			 ShoeStorageInfo shoe = new ShoeStorageInfo(shoeType,amount,amount);
			 shoesInfo.put(shoeType,shoe);
		 }
		 else{
			 shoesInfo.get(shoeType).addAmount(amount);
			 shoesInfo.get(shoeType).addDiscountedAmount(amount);
		 }
	 }
	 
	 public void file(Receipt receipt){
		 issuedReceipts.put(receipt.getType(), receipt);
	 }
	 
	 public void print(){
		 shoesInfo.forEach((shoe,info) -> System.out.println("Shoe Type: "+info.getName()+", amount on storage: "+info.getAmountOnStorage()+", discounted amount on storage: "+info.getDiscountedAmountOnStorage()));
		 issuedReceipts.forEach((shoe,receipt) -> receipt.print());
	 }
	 
}
