package bgu.spl.app;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CyclicBarrier;


public class ShoeStoreRunner {
	
    public static void main(String[] args) {
    	if(args.length>0) {
            System.setProperty("java.util.logging.SimpleFormatter.format","%5$s [%1$tc]%n");
            JsonParser input=readJson(new File(args[0]));
    		final CyclicBarrier barrier = new CyclicBarrier(input.getTotalAmount());
    		Store.getInstance().load(input.getInitialStorage());
    		runServices(input, barrier);
    	}
    	else System.out.println("No file to read");
    }
    
	public static JsonParser readJson(File inputFile) {
    	File file = inputFile;
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
	
    public static void runFactories(List<ShoeFactoryService> factories, CyclicBarrier barrier){
    	for (ShoeFactoryService factory: factories){
    		factory.setBarrier(barrier);
    		Thread thread = new Thread(factory);
    		thread.start();
    	}
    }
    
    public static void runClients(WebsiteClientService[] clients, CyclicBarrier barrier){
    	for (WebsiteClientService it: clients)
    	{
    		it.setBarrier(barrier);
    		Thread thread = new Thread(it);
    		thread.start();
    	}
    }
    
    private static void runSellers(List<SellingService> sellers, CyclicBarrier barrier) {
    	for (SellingService it: sellers)
    	{
    		it.setBarrier(barrier);
    		Thread thread = new Thread(it);
    		thread.start();
    	}		
	}
    
    private static void runManager(ManagementService manager, CyclicBarrier barrier) {
    	manager.setBarrier(barrier);
		Thread thread = new Thread(manager);
		thread.start();
		
	}
    
    private static void runTimer(TimeService time, CyclicBarrier barrier) {
    	time.setBarrier(barrier);
    	Thread thread = new Thread(time);
		thread.start();
		
	}
    
    private static void runServices(JsonParser input,CyclicBarrier barrier) {
    	runFactories(input.getServices().getFactories(), barrier);
		runClients(input.getServices().getCustomers(), barrier);
		runSellers(input.getServices().getSellers(), barrier); 
		runManager(input.getServices().getManager(), barrier);
		runTimer(input.getServices().getTime(), barrier);
    }   

}

	
