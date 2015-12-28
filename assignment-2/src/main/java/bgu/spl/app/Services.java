package bgu.spl.app;

import java.util.List;

public class Services{
	public Services(List<SellingService> sellers, TimeService time, ManagementService manager,
		WebsiteClientService[] customers, List<ShoeFactoryService> factories) {
		this.sellers = sellers;
		this.time = time;
		this.manager = manager;
		this.customers = customers;
		this.factories = factories;
	}
	
    public void setSellers(List<SellingService> sellers) {
		this.sellers = sellers;
	}

	public void setTime(TimeService time) {
		this.time = time;
	}

	public void setManager(ManagementService manager) {
		this.manager = manager;
	}

	public void setCustomers(WebsiteClientService[] customers) {
		this.customers = customers;
	}

	public void setFactories(List<ShoeFactoryService> factories) {
		this.factories = factories;
	}
	
	private List<SellingService> sellers;

    public List<SellingService> getSellers() {
		return sellers;
	}

	public TimeService getTime() {
		return time;
	}

	public ManagementService getManager() {
		return manager;
	}

	public WebsiteClientService[] getCustomers() {
		return customers;
	}

	public List<ShoeFactoryService> getFactories() {
		return factories;
	}

	private TimeService time;

    private ManagementService manager;

    private WebsiteClientService[] customers;

    private List<ShoeFactoryService> factories;

}   