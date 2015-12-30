package bgu.spl.app;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.gson.stream.JsonReader;

/**
 * A class used for parsing JSON files 
 */
public class JsonParser{
		int sellersAmount = 0;
		int factoriesAmount = 0;
		int clientsAmount = 0;
		int totalAmount;		
		private Services services;
		private ShoeStorageInfo[] initialStorage;
		
		/**
		 * A constructor which gets the participating services and initial storage data
		 * @param services - all participating services
		 * @param initialStorage - initial storage data
		 */
	public JsonParser(Services services, ShoeStorageInfo[] initialStorage) {
			this.services = services;
			this.initialStorage = initialStorage;
	}
	
	/**
	 * Default constructor
	 */
	public JsonParser(){}
	
	/**
	 * A method which gets and reads the input data, and according to it sets the initial storage data and services.
	 * The method counts the number of participating micro-services (extra 2 stands for 
	 * ManagementService and TimeService).
	 * @param in - input data
	 */
	public JsonParser readInput(InputStream in) throws IOException {
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

	/**
	 * A method which gets and reads the input data for a single shoe type, and returns its info
	 * as a ShoeStorageInfo object.
	 * @param reader - JSON file reader
	 */
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
	    
	/**
	 * A method which gets and reads the input data for all shoes, and returns it as a 
	 * ShoeStorageInfo array.
	 * @param reader - JSON file reader
	 */
	 public ShoeStorageInfo[] readShoesArray(JsonReader reader) throws IOException {	
	        List<ShoeStorageInfo> shoes = new ArrayList<ShoeStorageInfo>();
	        reader.beginArray();
	        while (reader.hasNext()) {
	        	shoes.add(readShoe(reader));
	        }
	        reader.endArray();
	        ShoeStorageInfo[] shoesInfo = (ShoeStorageInfo[]) shoes.toArray(new ShoeStorageInfo[shoes.size()] );
	        return shoesInfo;
	 }
	    
	 /**
	  * A method which gets and reads the input data for timer, and returns a TimeService corresponding to the
	  * given data.
	  * @param reader - JSON file reader
	  */
	 public TimeService readTimer(JsonReader reader) throws IOException {
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
	 
	 /**
	 * A method which gets and reads the input data for ManagementService, and returns a ManagementService
	 * corresponding to the given data.
	 * @param reader - JSON file reader
	 */
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
	    
	 /**
	  * A method which gets and reads the input data for DiscountSchedule, and returns a DiscountSchedule
	  * corresponding with the given data.
	  * @param reader - JSON file reader
	  */
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
		
	 /**
	  * A method which gets and reads the input data for all factory services, and returns it as a list
	  * of ShoeFactoryService. 
	  * @param reader - JSON file reader
	  */
	 public List<ShoeFactoryService> readFactories(JsonReader reader) throws IOException {	//shoe factories reader
		    List<ShoeFactoryService> factories = new ArrayList<ShoeFactoryService>();
		    int amount = reader.nextInt();
		    factoriesAmount=amount;
		    for (int i = 1;i<=amount;i++ ){
		        factories.add(new ShoeFactoryService("Factory Service " + Integer.toString(i),null));
		    }
		    return factories;
	 }
		
	 /**
	  * A method which gets and reads the input data for all selling services, and returns it as a list
	  * of SellingService elements.
	  * @param reader - JSON file reader
	  */
	 public List<SellingService> readSellers(JsonReader reader) throws IOException {
		    List<SellingService> sellers = new ArrayList<SellingService>();
		    int amount = reader.nextInt();
		    sellersAmount=amount;
		    for (int i = 1;i<=amount;i++ )
		        sellers.add(new SellingService("Selling Service " + Integer.toString(i),null));
		    return sellers;
	 }
		 
	 /**
	  * A method which gets and reads the input data for wishlist, and returns it as a set of strings.
	  * @param reader - JSON file reader
	  */
	 private  Set<String> readWishList(JsonReader reader) throws IOException {
			Set<String> wishList = new HashSet<>(); 
		    reader.beginArray();
		    while (reader.hasNext()) {
		        String name = reader.nextString(); 
		        wishList.add(name);
		    }
		    reader.endArray();
		    return wishList;
	 }
		
	 /**
	  * A method which gets and reads the input data for a single purchase schedule, and returns it as a 
	  * PurchaseSchedule object.
	  * @param reader - JSON file reader
	  */
	 private PurchaseSchedule readPurchaseSced(JsonReader reader) throws IOException {
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
		 
	 /**
	  * A method which gets and reads the input data for all purchase schedules, and returns it as a list
	  * of PurchaseSchedule elements.
	  * @param reader - JSON file reader
	  */
	 public List<PurchaseSchedule> readPurchaseScedList(JsonReader reader) throws IOException {	
		    List<PurchaseSchedule> list = new ArrayList<PurchaseSchedule>();
		    reader.beginArray();
		    while (reader.hasNext()) {
		       list.add(readPurchaseSced(reader));
		    }
		    reader.endArray();
		    return list;
	 }
		
	 /**
	  * A method which gets and reads the input data for a single clients, and returns it as 
	  * WebSiteClientService object.
	  * @param reader - JSON file reader
	  */
	 public WebsiteClientService readClient(JsonReader reader) throws IOException {	
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
		 
	 /**
	  * A method which gets and reads the input data for all clients, and returns it as an array of
	  * WebSiteClientService elements.
	  * @param reader - JSON file reader
	  */
	 public WebsiteClientService[] readCustomers(JsonReader reader) throws IOException {
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
		 
	 /**
	  * A method which gets and reads the input data for all services, and returns it as Services object.
	  * @param reader - JSON file reader
	  */
	 public Services readServices(JsonReader reader) throws IOException {
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
		
	 /**
	  * A getter method for the number of SellingServices
	  */
	 public int getSellersAmount() {
		 return sellersAmount;
	 }
	 
	 /**
	  * A getter method for the number of ShoeFactoryServices
	  */
	 public int getFactoriesAmount() {
		 return factoriesAmount;
	 }
	 
	 /**
	  * A getter method for the number of WebClientServices
	  */
	 public int getClientsAmount() {
		 return clientsAmount;
	 }
	 
	 /**
	  * A getter method for the total number of services
	  */
	 public int getTotalAmount() {
		 return totalAmount;
	 }
	 
	 /**
	  * A getter method for all services as a Services object
	  */
	 public Services getServices() {
		 return services;
	 }
	 
	 /**
	  * A getter method for initial storage data
	  */
	 public ShoeStorageInfo[] getInitialStorage() {
		 return initialStorage;
	 }

}		