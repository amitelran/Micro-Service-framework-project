package bgu.spl.app;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import bgu.spl.app.microservices.ManagementService;
import bgu.spl.app.microservices.SellingService;
import bgu.spl.app.microservices.ShoeFactoryService;
import bgu.spl.app.microservices.TimeService;
import bgu.spl.app.microservices.WebsiteClientService;

/**
 * Runner class which accepts a JSON input file as an argument, reads it and runs the program with the
 * given data from the input file.
 */
public class ShoeStoreRunner {
	
	/**
	 * Reads the JSON input file, setting a {@link CyclicBarrier} with all participating services as 
	 * an argument, initializing the {@code Store} and all of the services, and runs all of the threads.
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
	 * @return Parsed JSON file
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
	 * Getting the list of all factories and shared {@link CyclicBarrier}, sets the barrier for each factory,
	 * and runs them.
	 * @param factories - a list of all participating factories
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
    private static void runFactories(List<ShoeFactoryService> factories, CyclicBarrier barrier){
    	for (ShoeFactoryService factory: factories){
    		factory.setBarrier(barrier);
    		Thread thread = new Thread(factory);
    		thread.start();
    	}
    }
    
    /**
	 * Getting the list of all {@code WebSiteClientServices} and shared {@link CyclicBarrier},
	 * sets the barrier for each {@code WebSiteClientService}, and runs them.
	 * @param clients - an array of all participating {@code WebsiteClientServices}
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
    private static void runClients(WebsiteClientService[] clients, CyclicBarrier barrier){
    	for (WebsiteClientService client: clients){
    		client.setBarrier(barrier);
    		Thread thread = new Thread(client);
    		thread.start();
    	}
    }
    
    /**
	 * Getting the list of all {@code SellingServices} and shared {@link CyclicBarrier}, sets the barrier for each
	 * {@code SellingService}, and runs them.
	 * @param sellers - a list of all participating {@code SellingServices}
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
    private static void runSellers(List<SellingService> sellers, CyclicBarrier barrier) {
    	for (SellingService seller: sellers){
    		seller.setBarrier(barrier);
    		Thread thread = new Thread(seller);
    		thread.start();
    	}		
	}
    
    /**
	 * Getting a {@code ManagementService} and shared {@link CyclicBarrier}, sets the barrier for the 
	 * {@code ManagementService}, and runs the manager.
	 * @param manager - the participating {@code ManagementService}
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
    private static void runManager(ManagementService manager, CyclicBarrier barrier) {
    	manager.setBarrier(barrier);
		Thread thread = new Thread(manager);
		thread.start();	
	}
    
    /**
	 * Getting the {@code TimeService} and shared {@link CyclicBarrier}, sets the barrier for the timer, and runs it.
	 * @param time - the {@code TimeService} set for the program
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
    private static void runTimer(TimeService time, CyclicBarrier barrier) {
    	time.setBarrier(barrier);
    	Thread thread = new Thread(time);
		thread.start();
	}
    
    /**
	 * A method used for running all of the participating services.
	 * @param input - the JSON input file data
	 * @param barrier - a shared {@link CyclicBarrier} for all services
	 */
    private static void runServices(JsonParser input,CyclicBarrier barrier) {
    	runFactories(input.getServices().getFactories(), barrier);
		runClients(input.getServices().getCustomers(), barrier);
		runSellers(input.getServices().getSellers(), barrier); 
		runManager(input.getServices().getManager(), barrier);
		runTimer(input.getServices().getTime(), barrier);
    }   

}

	
