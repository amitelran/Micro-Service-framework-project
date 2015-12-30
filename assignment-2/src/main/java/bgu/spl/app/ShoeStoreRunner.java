package bgu.spl.app;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * Runner class which accepts a JSON input file as an argument, reads it and runs the program with the
 * given data from the input file.
 */
public class ShoeStoreRunner {
	
	/**
	 * Reads the JSON input file, setting a cyclicBarrier with all participating services, initializing
	 * the store and all of the services, and runs all of the threads.
	 */
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
    
    /**
	 * A method which gets a JSON input file and parse it
	 * @param inputFile - a JSON file to read given as an input
	 */
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
	
	/**
	 * Getting the list of all factories and shared cyclicBarrier, sets the barrier for each factory,
	 * and runs them
	 * @param factories - a list of all participating factories
	 * @param barrier - a shared cyclicBarrier for all services
	 */
    private static void runFactories(List<ShoeFactoryService> factories, CyclicBarrier barrier){
    	for (ShoeFactoryService factory: factories){
    		factory.setBarrier(barrier);
    		Thread thread = new Thread(factory);
    		thread.start();
    	}
    }
    
    /**
	 * Getting the list of all WebSiteClientServices and shared cyclicBarrier, sets the barrier for each
	 * WebSiteClientService, and runs them
	 * @param clients - an array of all participating WebsiteClientServices
	 * @param barrier - a shared cyclicBarrier for all services
	 */
    private static void runClients(WebsiteClientService[] clients, CyclicBarrier barrier){
    	for (WebsiteClientService client: clients){
    		client.setBarrier(barrier);
    		Thread thread = new Thread(client);
    		thread.start();
    	}
    }
    
    /**
	 * Getting the list of all SellingServices and shared cyclicBarrier, sets the barrier for each
	 * SellingService, and runs them
	 * @param sellers - a list of all participating SellingServices
	 * @param barrier - a shared cyclicBarrier for all services
	 */
    private static void runSellers(List<SellingService> sellers, CyclicBarrier barrier) {
    	for (SellingService seller: sellers){
    		seller.setBarrier(barrier);
    		Thread thread = new Thread(seller);
    		thread.start();
    	}		
	}
    
    /**
	 * Getting a ManagementService and shared cyclicBarrier, sets the barrier the ManagementService,
	 * and runs the manager
	 * @param manager - the participating ManagementService
	 * @param barrier - a shared cyclicBarrier for all services
	 */
    private static void runManager(ManagementService manager, CyclicBarrier barrier) {
    	manager.setBarrier(barrier);
		Thread thread = new Thread(manager);
		thread.start();	
	}
    
    /**
	 * Getting the TimeService and shared cyclicBarrier, sets the barrier for the timer, and runs it
	 * @param time - the TimeService set for the program
	 * @param barrier - a shared cyclicBarrier for all services
	 */
    private static void runTimer(TimeService time, CyclicBarrier barrier) {
    	time.setBarrier(barrier);
    	Thread thread = new Thread(time);
		thread.start();
	}
    
    /**
	 * A method used for running all of the participating services
	 * @param input - the JSON input file data
	 * @param barrier - a shared cyclicBarrier for all services
	 */
    private static void runServices(JsonParser input,CyclicBarrier barrier) {
    	runFactories(input.getServices().getFactories(), barrier);
		runClients(input.getServices().getCustomers(), barrier);
		runSellers(input.getServices().getSellers(), barrier); 
		runManager(input.getServices().getManager(), barrier);
		runTimer(input.getServices().getTime(), barrier);
    }   

}

	
