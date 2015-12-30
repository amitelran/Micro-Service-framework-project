package bgu.spl.app;

/**
 * An object representing a receipt that should be sent to a client after buying a shoe (when the client’s
 * PurchaseRequest completed)
 */
public class Receipt {
	
	private String seller;
	private String customer;
	private String shoeType;
	private boolean discount;
	private int issuedTick;
	private int requestTick;
	private int amountSold;
	
	/**
	 * @param seller - represents the supplier of the shoe
	 * @param customer - represents the buyer of the shoe
	 * @param shoeType - the purchased shoe type
	 * @param discount - whether the shoe was bought on discount or not
	 * @param issuedTick - the tick in which the shoe has been supplied to the customer
	 * @param requestTick - the tick in which the customer requested to purchase the shoe
	 * @param amountSold - the amount of shoes that has been sold to the customer
	 */
	public Receipt(String seller,String customer,String shoeType,boolean discount,int issuedTick,int requestTick,int amountSold){
		this.seller=seller;
		this.customer=customer;
		this.shoeType=shoeType;
		this.discount=discount;
		this.issuedTick=issuedTick;
		this.requestTick=requestTick;
		this.amountSold=amountSold;
	}
	
	/**
	 * getter set to return the supplier of the shoe for the specific receipt
	 */
	public String getSeller(){return seller;}
	
	/**
	 * getter set to return the customer name for the specific receipt
	 */
	public String getCustomer(){return customer;}
	
	/**
	 * getter set to return the shoe type for the specific receipt
	 */
	public String getType(){return shoeType;}
	
	/**
	 * getter set to return whether the shoe has been sold on discount or not
	 */
	public boolean isDiscounted(){return discount;}
	
	/**
	 * getter set to return the tick in which the shoe has been supplied
	 */
	public int getIssuedTick(){return issuedTick;}
	
	/**
	 * getter set to return the tick in which the shoe has been requested
	 */
	public int getRequestTick(){return requestTick;}
	
	/**
	 * getter set to return the amount of shoes that have been sold for the specific receipt
	 */
	public int getAmountSold(){return amountSold;}
	
	/**
	 * A method set to print the specified receipt within a structured format
	 */
	public void print(){
		System.out.printf("%s\n%-62.60s|\n%-62.60s|\n%-62.60s|\n%-62.60s|\n%-62.60s|\n%s\n","======================Shoe Sale Receipt========================"
						, "|  Shoe Type: "+shoeType+" (Quantity: "+amountSold+")"
						, "|  Seller: "+seller
						, "|  Customer: "+customer
						, "|  On discount: " + ((discount) ? "Yes" : "No")
						, "|  Requested on tick "+requestTick+", issued on tick "+issuedTick
						, "===============================================================","|s");
	}

}
