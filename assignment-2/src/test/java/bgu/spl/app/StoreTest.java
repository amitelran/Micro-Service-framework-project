package bgu.spl.app;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.app.Store.BuyResult;

public class StoreTest {

	private static Store store;
	
	@Before
	public void setUp() throws Exception {
		store = Store.getInstance();
		ShoeStorageInfo[] shoesStock = {
				new ShoeStorageInfo("black-allstars", 5, 0),
				new ShoeStorageInfo("blue-nikey", 7, 0),
				new ShoeStorageInfo("green-SPLspecial", 9, 0)
		};
		store.load(shoesStock);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInstance() {
		assertNotNull(store);
	}

	@Test
	public void testLoad() {			//checks storage existance
		System.out.println("\n");
		System.out.println("*******testLoad method invokation*******\n");
		System.out.println("\n");
		assertTrue(store.getStorageInfo()!=null);
		assertEquals(store.getStorageInfo().size(), 3);
		store.print();
		System.out.println("\n");
		System.out.println("*******testLoad method finish*******\n");
		System.out.println("\n");
	}

	@Test
	public void testTake() throws Exception {
		System.out.println("\n");
		System.out.println("*******testTake method invokation*******\n");
		System.out.println("\n");
		assertEquals(store.take("green-flip-flops", false), BuyResult.Not_In_Stock);
		assertEquals(store.take("blue-nikey", false), BuyResult.Regular_Price);
		assertEquals(store.take("black-allstars", true), BuyResult.Not_On_Discount);
		store.print();
		System.out.println("\n");
		System.out.println("*******testTake method finish*******\n");
		System.out.println("\n");
	}

	@Test
	public void testAdd() {
		System.out.println("\n");
		System.out.println("*******testAdd method invokation*******\n");
		System.out.println("\n");
		store.add("black-allstars", 5);
		assertEquals(store.getShoeInfo("black-allstars").getAmountOnStorage(), 10);
		store.print();
		System.out.println("\n");
		System.out.println("*******testAdd method finish*******\n");
		System.out.println("\n");
	}

	@Test
	public void testAddDiscount() throws Exception {
		System.out.println("\n");
		System.out.println("*******testAddDiscount method invokation*******\n");
		System.out.println("\n");
		store.addDiscount("black-allstars", 2);
		assertEquals(store.getShoeInfo("black-allstars").getDiscountedAmountOnStorage(), 2);
		store.print();
		System.out.println("\n");
		System.out.println("*******testAddDiscount method finish*******\n");
		System.out.println("\n");
	}

}
