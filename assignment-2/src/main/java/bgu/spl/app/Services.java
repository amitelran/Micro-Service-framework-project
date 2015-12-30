package bgu.spl.app;

import java.util.List;

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
     * @param sellers - a list of all to-be-participating SellingServices
     * @param time - the TimeService of the program
     * @param manager - the ManagementService of the program
     * @param customers - an array of all to-be-participating WebsiteClientServices
     * @param factories - a list of all to-be-participating factory services
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
	 * A getter which returns the list of all SellingServices
	 */
    public List<SellingService> getSellers() {
		return sellers;
	}

    /**
	 * A getter which returns the participating timeService
	 */
	public TimeService getTime() {
		return time;
	}

	/**
	 * A getter which returns the participating ManagementService 
	 */
	public ManagementService getManager() {
		return manager;
	}

	/**
	 * A getter which returns the array of all participating WebSiteClientServices
	 */
	public WebsiteClientService[] getCustomers() {
		return customers;
	}

	/**
	 * A getter which returns the list of all participating ShoeFactoryServices
	 */
	public List<ShoeFactoryService> getFactories() {
		return factories;
	}

}   