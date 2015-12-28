package bgu.spl.app;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.gson.stream.JsonReader;

public class JsonParser{
		int sellersAmount = 0;
		int factoriesAmount = 0;
		int clientsAmount = 0;
		int totalAmount;		
		private Services services;
		private ShoeStorageInfo[] initialStorage;
		
	public JsonParser(Services services, ShoeStorageInfo[] initialStorage) {	//constructor
			this.services = services;
			this.initialStorage = initialStorage;
	}
	
	public JsonParser(){}		//default constructor
	
	public JsonParser readInput(InputStream in) throws IOException {		//initial storage and services reader
	        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	        try {
	        	reader.beginObject();
	            while (reader.hasNext()) {
	              String name = reader.nextName();
	              if (name.equals("initialStorage")) {
	            	  initialStorage = readShoesArray(reader);
	              } else if (name.equals("services")) {
	            	  services = readServices(reader);
	              } else reader.skipValue();  
	            }
	            totalAmount=2+sellersAmount+factoriesAmount+clientsAmount;
	            return this;     
	        } finally {
	        	reader.close();
	        }
	    }

		public ShoeStorageInfo readShoe(JsonReader reader) throws IOException {
	    	String shoeType=null;;
	    	int amountOnStorage=-1;
	        reader.beginObject();
	        while (reader.hasNext()) {
	          String name = reader.nextName();
	          if (name.equals("shoeType")) {
	        	  shoeType = reader.nextString();
	          } else if (name.equals("amount")) {
	        	  amountOnStorage = reader.nextInt();
	          }  else {
	        	  reader.skipValue();
	          }
	        }
	        reader.endObject();
	        return new ShoeStorageInfo(shoeType,amountOnStorage,0);
	    }
	    
	    public ShoeStorageInfo[] readShoesArray(JsonReader reader) throws IOException {		//getting shoes info
	        List<ShoeStorageInfo> shoes = new ArrayList<ShoeStorageInfo>();
	        reader.beginArray();
	        while (reader.hasNext()) {
	        	shoes.add(readShoe(reader));
	        }
	        reader.endArray();
	        ShoeStorageInfo[] shoesInfo = (ShoeStorageInfo[]) shoes.toArray(new ShoeStorageInfo[shoes.size()] );
	        return shoesInfo;
	    }
	    
	    public TimeService readTimer(JsonReader reader) throws IOException {	//setting ticks speed and duration
	    	int speed=-1;
	    	int duration=-1;
	        reader.beginObject();
	        while (reader.hasNext()) {
	          String name = reader.nextName();
	          if (name.equals("speed")) {
	        	  speed = reader.nextInt();
	          } else if (name.equals("duration")) {
	        	  duration = reader.nextInt();
	          } else reader.skipValue();
	        }
	        reader.endObject();
	        return new TimeService(speed, duration,null);
	    }
	    
	    public ManagementService readManager(JsonReader reader) throws IOException {	//reading management service
	    	List<DiscountSchedule> discountSchedule = new ArrayList<DiscountSchedule>();
	        reader.beginObject();
	        String name = reader.nextName();
	        if(name.equals("discountSchedule")){
	        	reader.beginArray();
		        while (reader.hasNext()) {
		        	discountSchedule.add(readDiscountSced(reader));
		        }
		        reader.endArray();
		        reader.endObject();
	        }
	        return new ManagementService(discountSchedule,null);
	    }
	    
		private DiscountSchedule readDiscountSced(JsonReader reader) throws IOException {		//discount schedule reader
			String shoeType=null;
	    	int amount=-1;
	    	int tick=-1;
	        reader.beginObject();
	        while (reader.hasNext()) {
	          String name = reader.nextName();	           
	          if (name.equals("shoeType")) {
	        	  shoeType = reader.nextString();
	          } else if (name.equals("amount")) {
	        	  amount = reader.nextInt();
	          } else if (name.equals("tick")) {
	        	  tick = reader.nextInt();
	          } else reader.skipValue();
	        }
	        reader.endObject();
	        return new DiscountSchedule(shoeType,tick,amount);	
		}
		
		public List<ShoeFactoryService> readFactories(JsonReader reader) throws IOException {	//shoe factories reader
		    List<ShoeFactoryService> factories = new ArrayList<ShoeFactoryService>();
		    int amount = reader.nextInt();
		    factoriesAmount=amount;
		    for (int i = 1;i<=amount;i++ ){
		        factories.add(new ShoeFactoryService("Factory Service " + Integer.toString(i),null));
		    }
		    return factories;
		 }
		
		public List<SellingService> readSellers(JsonReader reader) throws IOException {	//selling services reader
		    List<SellingService> sellers = new ArrayList<SellingService>();
		    int amount = reader.nextInt();
		    sellersAmount=amount;
		    for (int i = 1;i<=amount;i++ )
		        sellers.add(new SellingService("Selling Service " + Integer.toString(i),null));
		    return sellers;
		}
		 
		private  Set<String> readWishList(JsonReader reader) throws IOException {	//wishlist reader)
			Set<String> wishList = new HashSet<>(); 
		    reader.beginArray();
		    while (reader.hasNext()) {
		        String name = reader.nextString(); 
		        wishList.add(name);
		    }
		    reader.endArray();
		    return wishList;
		}
		
		private PurchaseSchedule readPurchaseSced(JsonReader reader) throws IOException {		//purchase schedule reader
			String shoeType=null;;
		    int tick=-1;
		    reader.beginObject();
		    while (reader.hasNext()) {
		        String name = reader.nextName();
		        if (name.equals("shoeType")) {
		        	shoeType = reader.nextString();
		          } else if (name.equals("tick")) {
		        	  tick = reader.nextInt();
		          } else reader.skipValue();
		        }
		    reader.endObject();
		    return new PurchaseSchedule(shoeType,tick);
		}
		 
		
		public List<PurchaseSchedule> readPurchaseScedList(JsonReader reader) throws IOException {	//purchase schedule reader as a list
		    List<PurchaseSchedule> list = new ArrayList<PurchaseSchedule>();
		    reader.beginArray();
		    while (reader.hasNext()) {
		       list.add(readPurchaseSced(reader));
		    }
		    reader.endArray();
		    return list;
		}
		
		public WebsiteClientService readClient(JsonReader reader) throws IOException {		//websiteClientServices reader
			 String name1 = null;
			 Set<String> wishList = new HashSet<String>();
			 List<PurchaseSchedule> purchaseScheduleList = new ArrayList<PurchaseSchedule>();
		     reader.beginObject();
		     while (reader.hasNext()) {
		        String name = reader.nextName();
		        if (name.equals("name")) {
		        	name1 = reader.nextString();
		        } else if (name.equals("wishList")) {
		        	 wishList = readWishList(reader);	          
		        } else if (name.equals("purchaseSchedule")) {
		        	 purchaseScheduleList = readPurchaseScedList(reader);	          
		        } else reader.skipValue();
		     }
		     reader.endObject();
		     return new WebsiteClientService(name1, purchaseScheduleList, wishList, null);
		}
		 
		public WebsiteClientService[] readCustomers(JsonReader reader) throws IOException {		//customers reader
		        List<WebsiteClientService> customers = new ArrayList<WebsiteClientService>();
		        reader.beginArray();
		        while (reader.hasNext()) {
		        	customers.add(readClient(reader));
		        	clientsAmount++;
		        }
		        reader.endArray();
		        WebsiteClientService[] custArr = (WebsiteClientService[]) customers.toArray(new WebsiteClientService[customers.size()]);
		        return custArr;
		 }
		 
		public Services readServices(JsonReader reader) throws IOException {	//services reader
			  List<SellingService> sellers = null;
			  TimeService time = null;
			  ManagementService manager = null;
			  WebsiteClientService[] customers = null;
			  List<ShoeFactoryService> factories = null;
			  reader.beginObject();
			  while (reader.hasNext()) {
				  String name = reader.nextName();
				  if (name.equals("time")) {
					  time = readTimer(reader);  
				  } else if (name.equals("manager")) {
					  manager = readManager(reader);
				  } else if (name.equals("factories")) {
					  factories = readFactories(reader);
				  } else if (name.equals("sellers")) {
					  sellers = readSellers(reader);
				  } else if (name.equals("customers")) {
					  customers = readCustomers(reader);
				  } else  reader.skipValue();
			  }
		      reader.endObject();
		      return new Services(sellers, time, manager, customers, factories);
		}
		
		public int getSellersAmount() {
			return sellersAmount;
		}
		public int getFactoriesAmount() {
			return factoriesAmount;
		}
		public int getClientsAmount() {
			return clientsAmount;
		}
		public int getTotalAmount() {
			return totalAmount;
		}
		public Services getServices() {
			return services;
		}
		public ShoeStorageInfo[] getInitialStorage() {
			return initialStorage;
		}

}		