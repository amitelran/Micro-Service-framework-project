package bgu.spl.app;
<<<<<<< HEAD

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class ShoeStoreRunner {
	public static void main(String args[]){
		System.setProperty("java.util.logging.SimpleFormatter.format",
				" %2$s %5$s%6$s%n");
		ShoeStorageInfo[] storage;
		storage=new ShoeStorageInfo[]{new ShoeStorageInfo("red-boots",0,0),new ShoeStorageInfo("green-flip-flops",7,0)};
		Store.getInstance().load(storage);
		List<DiscountSchedule> discountSchedule=new LinkedList<DiscountSchedule>();
		//discountSchedule.add(new DiscountSchedule("red-boots",3,1));
		discountSchedule.add(new DiscountSchedule("green-flip-flops",10,3));
		ManagementService mS=new ManagementService(discountSchedule);
		Set<String> BRURIAs=new HashSet<String>();
		BRURIAs.add("green-flip-flops");
		List<PurchaseSchedule> psBRURIA=new LinkedList<PurchaseSchedule>();
		psBRURIA.add(new PurchaseSchedule("red-boots", 3));
		WebsiteClientService cS1=new WebsiteClientService("Bruria", psBRURIA,BRURIAs );
		Set<String> SHRAGAs=new HashSet<String>();
		List<PurchaseSchedule> psSHRAGA=new LinkedList<PurchaseSchedule>();
		psSHRAGA.add(new PurchaseSchedule("green-flip-flops", 12));
		WebsiteClientService cS2=new WebsiteClientService("Shraga", psSHRAGA,SHRAGAs );

		SellingService sS1=new SellingService("selling service 1");
		SellingService sS2=new SellingService("selling service 2");
		ShoeFactoryService sfS1=new ShoeFactoryService("factory 1");
		ShoeFactoryService sfS2=new ShoeFactoryService("factory 2");
		ShoeFactoryService sfS3=new ShoeFactoryService("factory 3");

		new Thread(mS).start();
		new Thread(cS1).start();
		new Thread(cS2).start();
		new Thread(sS1).start();
		new Thread(sS2).start();
		new Thread(sfS1).start();
		new Thread(sfS2).start();
		new Thread(sfS3).start();

		TimeService tS=new TimeService(1000, 24);
		Thread t=new Thread(tS);
		t.start();
	}
=======
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import com.google.code.gson;

public class ShoeStoreRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

>>>>>>> refs/remotes/origin/master
}
