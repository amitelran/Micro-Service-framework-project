package bgu.spl.app;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CyclicBarrier;

import bgu.spl.mics.MicroService;

public class TimeService extends MicroService {
	private int time;
	private int speed;
	private int duration;
	private Timer timer;
	private CyclicBarrier barrier;
	
	/**
	 * 
	 * @param s number of milliseconds each clock tick takes
	 * @param d number of ticks before termination
	 */
	public TimeService(int speed,int duration,CyclicBarrier barrier) {
		super("timer");
		time=0;
		this.speed=speed;
		this.duration=duration;
		this.barrier = barrier;
	}
	
	class Ticker extends TimerTask {

		@Override
		public void run() {
			time++;
			if(time<duration)
				TimeService.this.sendBroadcast(new TickBroadcast(time));
			else{
				TimeService.this.sendBroadcast(new TerminationBroadcast());
				try {
					barrier.await();
				} catch (Exception e) {}
				TimeService.this.terminate();
				timer.cancel();
			}
		}
		
	}

	@Override
	protected void initialize() {
		log("Time Service is waiting for all services to initialize...");
		try {
			barrier.await();
		} catch (Exception e) {}
		log("Everyone's aboard! timer is starting to run... tick tock... tick tock...");
		timer=new Timer();
		timer.scheduleAtFixedRate(new Ticker(), speed, speed);
	}
	
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}

}
