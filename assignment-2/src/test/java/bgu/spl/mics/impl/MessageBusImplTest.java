package bgu.spl.mics.impl;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.app.DiscountSchedule;
import bgu.spl.app.ManagementService;
import bgu.spl.app.PurchaseOrderRequest;
import bgu.spl.app.PurchaseSchedule;
import bgu.spl.app.WebsiteClientService;
import bgu.spl.mics.Request;

public class MessageBusImplTest {
	
	private static MessageBusImpl messageBus;

	@Before
	public void setUp() throws Exception {
		messageBus = MessageBusImpl.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInstance() {
		assertFalse(messageBus.getInstance()==null);
	}

	@Test
	public void testSubscribeRequest() {
		WebsiteClientService Bruria = new WebsiteClientService("Bruria",null,null,null);
		PurchaseOrderRequest pur = new PurchaseOrderRequest("red-boots",1,false,"Bruria",2);
		messageBus.subscribeRequest((PurchaseOrderRequest)pur,Bruria);
	}

	@Test
	public void testSubscribeBroadcast() {
		fail("Not yet implemented");
	}

	@Test
	public void testComplete() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendBroadcast() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegister() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnregister() {
		fail("Not yet implemented");
	}

	@Test
	public void testAwaitMessage() {
		fail("Not yet implemented");
	}

}
