package bgu.spl.app;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;


public class ShoeStoreRunnerExample {
	public static void main(String args[]){
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%5$s%6$s%n");
		CyclicBarrier barrier=new CyclicBarrier(9);
		ShoeStorageInfo[] storage;
		storage=new ShoeStorageInfo[]{new ShoeStorageInfo("red-boots",0,0),new ShoeStorageInfo("green-flip-flops",7,0)};
		Store.getInstance().load(storage);
		List<DiscountSchedule> discountSchedule=new LinkedList<DiscountSchedule>();
		//discountSchedule.add(new DiscountSchedule("red-boots",3,1));
		discountSchedule.add(new DiscountSchedule("green-flip-flops",10,3));
		ManagementService mS=new ManagementService(discountSchedule,barrier);
		Set<String> BRURIAs=new HashSet<String>();
		BRURIAs.add("green-flip-flops");
		List<PurchaseSchedule> psBRURIA=new LinkedList<PurchaseSchedule>();
		psBRURIA.add(new PurchaseSchedule("red-boots", 3));
		WebsiteClientService cS1=new WebsiteClientService("Bruria", psBRURIA,BRURIAs,barrier);
		Set<String> SHRAGAs=new HashSet<String>();
		List<PurchaseSchedule> psSHRAGA=new LinkedList<PurchaseSchedule>();
		psSHRAGA.add(new PurchaseSchedule("green-flip-flops", 12));
		WebsiteClientService cS2=new WebsiteClientService("Shraga", psSHRAGA,SHRAGAs ,barrier);

		SellingService sS1=new SellingService("selling service 1",barrier);
		SellingService sS2=new SellingService("selling service 2",barrier);
		ShoeFactoryService sfS1=new ShoeFactoryService("factory 1",barrier);
		ShoeFactoryService sfS2=new ShoeFactoryService("factory 2",barrier);
		ShoeFactoryService sfS3=new ShoeFactoryService("factory 3",barrier);

		TimeService tS=new TimeService(1000, 24,barrier);
		
		
		new Thread(mS).start();
		new Thread(cS1).start();
		new Thread(cS2).start();
		new Thread(sS1).start();
		new Thread(sS2).start();
		new Thread(sfS1).start();
		new Thread(sfS2).start();
		new Thread(sfS3).start();
		new Thread(tS).start();
	}
}
