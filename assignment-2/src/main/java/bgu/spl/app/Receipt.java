package bgu.spl.app;

public class Receipt {
	
	private String seller;
	private String customer;
	private String shoeType;
	private boolean discount;
	private int issuedTick;
	private int requestTick;
	private int amountSold;
	
	public Receipt(String seller,String customer,String shoeType,boolean discount,int issuedTick,int requestTick,int amountSold){
		this.seller=seller;
		this.customer=customer;
		this.shoeType=shoeType;
		this.discount=discount;
		this.issuedTick=issuedTick;
		this.requestTick=requestTick;
		this.amountSold=amountSold;
	}
	
	public String getSeller(){return seller;}
	public String getCustomer(){return customer;}
	public String getType(){return shoeType;}
	public boolean isDiscounted(){return discount;}
	public int getIssuedTick(){return issuedTick;}
	public int getRequestTick(){return requestTick;}
	public int getAmountSold(){return amountSold;}
	
	public void print(){
		System.out.printf("%s\n%-62.60s|\n%-62.60s|\n%-62.60s|\n%-62.60s|\n%-62.60s|\n%s\n","======================Shoe Sale Receipt========================"
						, "|  Shoe Type: "+shoeType+" (Quantity: "+amountSold+")"
						, "|  Seller: "+seller
						, "|  Customer: "+customer
						, "|  On discount: " + ((discount) ? "Yes" : "No")
						, "|  Requested on "+requestTick+" tick, issued on "+issuedTick+" tick"
						, "===============================================================","|s");
	}

}
