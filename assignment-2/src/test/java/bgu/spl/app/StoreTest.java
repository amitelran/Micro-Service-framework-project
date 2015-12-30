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
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInstance() {
		assertNotNull(store);
	}

	@Test
	public void testLoad() throws Exception {
		try{
			assertEquals(store.take("pink-shkafkafim",false), BuyResult.Not_In_Stock);
			ShoeStorageInfo[] shoesStock = {
					new ShoeStorageInfo("pink-shkafkafim", 5, 0),
					new ShoeStorageInfo("blue-nikey", 7, 0),
					new ShoeStorageInfo("green-SPLspecial", 9, 0)
			};
			store.load(shoesStock);
			assertEquals(store.take("pink-shkafkafim",false), BuyResult.Regular_Price);
		}catch(RuntimeException e){
			fail("No exception thrown");
		}
		
	}

	@Test
	public void testTake() throws Exception {
		ShoeStorageInfo[] shoesStock = {
				new ShoeStorageInfo("black-allstars", 5, 0),
				new ShoeStorageInfo("blue-nikey", 7, 0),
				new ShoeStorageInfo("green-SPLspecial", 9, 0)
		};
		store.load(shoesStock);
		try{
			assertEquals(store.take("green-flip-flops", false), BuyResult.Not_In_Stock);
			assertEquals(store.take("blue-nikey", false), BuyResult.Regular_Price);
			assertEquals(store.take("black-allstars", true), BuyResult.Not_On_Discount);
		}catch(RuntimeException e){
			fail("No exception thrown");
		}
	}

	@Test
	public void testAdd() throws Exception {
		try{
			assertEquals(store.take("red-boots",false), BuyResult.Not_In_Stock);
			store.add("red-boots", 1);
			assertEquals(store.take("red-boots",false), BuyResult.Regular_Price);
		}catch(RuntimeException e){
			fail("No exception thrown");
		}
		
	}

	@Test
	public void testAddDiscount() throws Exception {
		try{
			store.addDiscount("brown-blundstones", 1);
			fail("NoShoesException not thrown");
		}catch(NoShoesException e){
			assertTrue(e instanceof NoShoesException);
			store.add("black-allstars",1);
			store.addDiscount("black-allstars",1);
			assertEquals(store.take("black-allstars",true), BuyResult.Discounted_Price);
		}
	}

}
