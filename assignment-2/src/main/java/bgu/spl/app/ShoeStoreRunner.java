package bgu.spl.app;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class ShoeStoreRunner {
	
    public static void main(String[] args) {
    	JsonParser input=readJson();
		final CountDownLatch latch = new CountDownLatch(input.getTotalAmount());
		runServices(input, latch);
    }
    
	public static JsonParser readJson() {
    	File file = new File("c:\\json\\1stcheck.json");
        try {
			InputStream targetStream = new FileInputStream(file);
			JsonParser input=new JsonParser();
			input=input.readInput(targetStream);
			return input;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
	
    public static void runFactories(List<ShoeFactoryService> factories,CountDownLatch latch){
    	for (ShoeFactoryService factory: factories){
    		factory.setM_latchObject(latch);
    		Thread thread = new Thread(factory);
    		thread.start();
    	}
    }
    
    public static void runClients(WebsiteClientService[] clients,CountDownLatch latch){
    	for (WebsiteClientService it: clients)
    	{
    		it.setM_latchObject(latch);
    		Thread thread = new Thread(it);
    		thread.start();
    	}
    }
    
    private static void runSellers(List<SellingService> sellers, CountDownLatch latch) {
    	for (SellingService it: sellers)
    	{
    		it.setM_latchObject(latch);
    		Thread thread = new Thread(it);
    		thread.start();
    	}		
	}
    
    private static void runManager(ManagementService manager, CountDownLatch latch) {
    	manager.setM_latchObject(latch);
		Thread thread = new Thread(manager);
		thread.start();
		
	}
    
    private static void runTimer(TimeService time, CountDownLatch latch) {
    	time.setM_latchObject(latch);
		time.start();
		
	}
    
    private static void runServices(JsonParser input,CountDownLatch latch) {
    	runFactories(input.getServices().getFactories(), latch);
		runClients(input.getServices().getCustomers(), latch);
		runSellers(input.getServices().getSellers(), latch); 
		runManager(input.getServices().getManager(), latch);
		runTimer(input.getServices().getTime(), latch);
    }   

}

	
