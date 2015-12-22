package bgu.spl.app;

public class Receipt {
	
	private String seller;
	private String customer;
	private String shoeType;
	private boolean discount;
	private int issuedTick;
	private int requestTick;
	private int amountSold;
	
	public String getSeller(){return seller;}
	public String getCustomer(){return customer;}
	public String getType(){return shoeType;}
	public boolean isDiscounted(){return discount;}
	public int getIssuedTick(){return issuedTick;}
	public int getRequestTick(){return requestTick;}
	public int getAmountSold(){return amountSold;}

}
