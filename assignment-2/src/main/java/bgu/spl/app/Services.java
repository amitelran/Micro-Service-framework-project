package bgu.spl.app;

import java.util.List;

import bgu.spl.app.microservices.ManagementService;
import bgu.spl.app.microservices.SellingService;
import bgu.spl.app.microservices.ShoeFactoryService;
import bgu.spl.app.microservices.TimeService;
import bgu.spl.app.microservices.WebsiteClientService;

/**
 * An object used in order to help with parsing the JSON files by making a collection of all of
 * the program participating services.
 */
public class Services{
	
	private TimeService time;
    private ManagementService manager;
    private WebsiteClientService[] customers;
    private List<SellingService> sellers;
    private List<ShoeFactoryService> factories;
    
    /**
     * @param sellers - a list of all to-be-participating {@code SellingServices}
     * @param time - the {@code TimeService} of the program
     * @param manager - the {@code ManagementService} of the program
     * @param customers - an array of all to-be-participating {@code WebsiteClientServices}
     * @param factories - a list of all to-be-participating {@code ShoeFactoryServices}
     */
	public Services(List<SellingService> sellers, TimeService time, ManagementService manager,
		WebsiteClientService[] customers, List<ShoeFactoryService> factories) {
		this.sellers = sellers;
		this.time = time;
		this.manager = manager;
		this.customers = customers;
		this.factories = factories;
	}
	
	/**
	 * @return List of all {@code SellingServices}.
	 */
    public List<SellingService> getSellers() {
		return sellers;
	}

    /**
	 * @return Participating {@code TimeService}.
	 */
	public TimeService getTime() {
		return time;
	}

	/**
	 * @return Participating {@code ManagementService}.
	 */
	public ManagementService getManager() {
		return manager;
	}

	/**
	 * @return Array of all participating {@code WebSiteClientServices}.
	 */
	public WebsiteClientService[] getCustomers() {
		return customers;
	}

	/**
	 * @return List of all participating {@code ShoeFactoryServices}.
	 */
	public List<ShoeFactoryService> getFactories() {
		return factories;
	}

}   