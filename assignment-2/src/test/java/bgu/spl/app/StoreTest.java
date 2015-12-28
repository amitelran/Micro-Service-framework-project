package bgu.spl.app;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StoreTest {

	private static Store store;
	
	@Before
	public void setUp() throws Exception {
		store = Store.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		store.print();
	}

	@Test
	public void testGetInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoad() {
		fail("Not yet implemented");
	}

	@Test
	public void testTake() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddDiscount() {
		fail("Not yet implemented");
	}

	@Test
	public void testFile() {
	}

	@Test
	public void testPrint() {
		fail("Not yet implemented");
	}

}
