package bgu.spl.app;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.gson.stream.JsonReader;

import bgu.spl.app.microservices.ManagementService;
import bgu.spl.app.microservices.SellingService;
import bgu.spl.app.microservices.ShoeFactoryService;
import bgu.spl.app.microservices.TimeService;
import bgu.spl.app.microservices.WebsiteClientService;
import bgu.spl.app.schedules.DiscountSchedule;
import bgu.spl.app.schedules.PurchaseSchedule;

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
	 * The method counts the number of participating micro-services (the extra 2 stands for 
	 * {@code ManagementService} and {@code TimeService}).
	 * @param in - input data
	 * @return JsonParser object
	 * @throws IOException
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
	 * A method which gets and reads the input data for a single shoe type.
	 * @param reader - JSON file reader
	 * @return {@code ShoeStorageInfo} object for a single shoe type
	 * @throws IOException
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
	 * A method which gets and reads the input data for all shoes.
	 * @param reader - JSON file reader
	 * @return array of {@code ShoeStorageInfo} for each given shoe data
	 * @throws IOException
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
	  * A method which gets and reads the input data for timer.
	  * @param reader - JSON file reader
	  * @return {@code TimeService} corresponding to the given data
	  * @throws IOException
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
	 * A method which gets and reads the input data for {@code ManagementService}.
	 * @param reader - JSON file reader
	 * @return ManagementService corresponding to the given data
	 * @throws IOException
	 */
	 public ManagementService readManager(JsonReader reader) throws IOException {
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
	  * A method which gets and reads the input data for {@code DiscountSchedule}.
	  * @param reader - JSON file reader
	  * @return discount schedule
	  * @throws IOException
	  */
	 private DiscountSchedule readDiscountSced(JsonReader reader) throws IOException {		
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
	  * A method which gets and reads the input data for all {@code ShoeFactoryServices}.
	  * @param reader - JSON file reader
	  * @return a list of all ShoeFactoryServices
	  * @throws IOException
	  */
	 public List<ShoeFactoryService> readFactories(JsonReader reader) throws IOException {
		    List<ShoeFactoryService> factories = new ArrayList<ShoeFactoryService>();
		    int amount = reader.nextInt();
		    factoriesAmount=amount;
		    for (int i = 1;i<=amount;i++ ){
		        factories.add(new ShoeFactoryService("Factory Service " + Integer.toString(i),null));
		    }
		    return factories;
	 }
		
	 /**
	  * A method which gets and reads the input data for all {@code SellingServices}.
	  * @param reader - JSON file reader
	  * @return a list of all SellingServices
	  * @throws IOException
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
	  * A method which gets and reads the input data for wishlist.
	  * @param reader - JSON file reader
	  * @return wish list set as a set of strings
	  * @throws IOException
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
	  * A method which gets and reads the input data for a single purchase schedule.
	  * @param reader - JSON file reader
	  * @return a single purchase schedule
	  * @throws IOException
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
	  * A method which gets and reads the input data for all purchase schedules.
	  * @param reader - JSON file reader
	  * @return a list of all purchase schedules
	  * @throws IOExcpetion
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
	  * A method which gets and reads the input data for a single client.
	  * @param reader - JSON file reader
	  * @return single client data
	  * @throws IOException
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
	  * A method which gets and reads the input data for all clients.
	  * @param reader - JSON file reader
	  * @return array of all clients
	  * @throws IOException
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
	  * A method which gets and reads the input data for all services.
	  * @param reader - JSON file reader
	  * @return A collection of all services
	  * @throws IOException
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
	  * @return Number of {@code SellingServices}.
	  */
	 public int getSellersAmount() {
		 return sellersAmount;
	 }
	 
	 /**
	  * @return Number of {@code ShoeFactoryServices}.
	  */
	 public int getFactoriesAmount() {
		 return factoriesAmount;
	 }
	 
	 /**
	  * @return Number of {@code WebsiteClientServices}.
	  */
	 public int getClientsAmount() {
		 return clientsAmount;
	 }
	 
	 /**
	  * @return Number of all services.
	  */
	 public int getTotalAmount() {
		 return totalAmount;
	 }
	 
	 /**
	  * @return Collection of all services set as a {@code Services} object.
	  */
	 public Services getServices() {
		 return services;
	 }
	 
	 /**
	  * @return Initial storage data.
	  */
	 public ShoeStorageInfo[] getInitialStorage() {
		 return initialStorage;
	 }

}		