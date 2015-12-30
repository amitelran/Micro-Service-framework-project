package bgu.spl.mics.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;
import bgu.spl.mics.RequestCompleted;

public class MessageBusImplTest {
	
	private MessageBusImpl messageBus = MessageBusImpl.getInstance();
	public static MicroService dani;
	public static MicroService moshe;
	public static MicroService yaffa;
	
	private static class SimpleMicroService extends MicroService{

		public SimpleMicroService(String name) {
			super(name);
		}

		@Override
		protected void initialize() {			
		}
	}

	public class EmptyRequest implements Request<Integer>{}
	public class EmptyBroadcast implements Broadcast{}
	
	
	@BeforeClass
	public static void init() throws Exception {

		dani=new SimpleMicroService("dani");
		moshe=new SimpleMicroService("moshe");
		yaffa=new SimpleMicroService("yaffa");
	}
	
	@Before
	public void setUp() throws Exception {
		messageBus.register(dani);
		messageBus.register(moshe);
		messageBus.register(yaffa);
	}

	@After
	public void tearDown() throws Exception {
		messageBus.unregister(dani);
		messageBus.unregister(moshe);
		messageBus.unregister(yaffa);
	}

	@Test
	public void testGetInstance() {
		assertNotNull(messageBus);
	}

	@Test
	public void testSubscribeAndSendRequest() {
		messageBus.subscribeRequest(EmptyRequest.class,dani);
		messageBus.subscribeRequest(EmptyRequest.class,moshe);
		messageBus.subscribeRequest(EmptyRequest.class,yaffa);
		EmptyRequest req1=new EmptyRequest();
		EmptyRequest req2=new EmptyRequest();
		EmptyRequest req3=new EmptyRequest();
		MicroService m=new SimpleMicroService("");
		messageBus.register(m);
		messageBus.sendRequest(req1, m);
		messageBus.sendRequest(req2, m);
		messageBus.sendRequest(req3, m);
		messageBus.sendRequest(req2, m);
		try {
			assertEquals(req1,messageBus.awaitMessage(dani));
			assertEquals(req2,messageBus.awaitMessage(moshe));
			assertEquals(req3,messageBus.awaitMessage(yaffa));
			assertEquals(req2,messageBus.awaitMessage(dani));
		} catch (InterruptedException e) {
			fail();
		}
		finally{
			messageBus.unregister(m);
		}
	}
	//test awaitMessage as well
	@Test
	public void testSubscribeAndSendBroadcast() {
		messageBus.subscribeBroadcast(EmptyBroadcast.class,dani);
		messageBus.subscribeBroadcast(EmptyBroadcast.class,moshe);
		messageBus.subscribeBroadcast(EmptyBroadcast.class,yaffa);
		EmptyBroadcast bcast=new EmptyBroadcast();
		messageBus.sendBroadcast(bcast);
		try {
			assertEquals(messageBus.awaitMessage(dani),bcast);
			assertEquals(messageBus.awaitMessage(moshe),bcast);
			assertEquals(messageBus.awaitMessage(yaffa),bcast);
		} catch (InterruptedException e) {
			fail();
		}
	}
	
	@Test
	public void testComplete() {
		messageBus.subscribeRequest(EmptyRequest.class,dani);
		EmptyRequest req=new EmptyRequest();
		messageBus.sendRequest(req, moshe);
		messageBus.complete(req, 34);
		try {
			Message compl=messageBus.awaitMessage(moshe);
			assertEquals(RequestCompleted.class,compl.getClass());
			if(compl.getClass()==RequestCompleted.class)
				assertEquals(((RequestCompleted<?>)compl).getResult(),34);
		} catch (InterruptedException e) {
			fail();
		}
	}


	@Test
	public void testRegister() {
		MicroService m=new SimpleMicroService("");
		try{
			messageBus.subscribeBroadcast(EmptyBroadcast.class,m);
			fail();
		}
		catch(Exception e){
			assertEquals(IllegalStateException.class,e.getClass());
		}
		messageBus.register(m);
		try{
			messageBus.subscribeBroadcast(EmptyBroadcast.class,m);
		}
		catch(Exception e){
			fail();
		}
		messageBus.unregister(m);
	}

	@Test
	public void testUnregister() {
		MicroService m=new SimpleMicroService("");
		messageBus.register(m);
		messageBus.unregister(m);
		try{
			messageBus.subscribeBroadcast(EmptyBroadcast.class,m);
			fail();
		}
		catch(Exception e){
			assertEquals(IllegalStateException.class,e.getClass());
		}
		try{
			messageBus.awaitMessage(m);
			fail();
		}
		catch(Exception e){
			assertEquals(IllegalStateException.class,e.getClass());
		}
		try{
			messageBus.sendRequest(new EmptyRequest(),m);
			fail();
		}
		catch(Exception e){
			assertEquals(IllegalStateException.class,e.getClass());
		}
	}
}
