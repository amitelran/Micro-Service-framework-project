package bgu.spl.app;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CyclicBarrier;

import bgu.spl.mics.MicroService;

/**
 * A micro-service which serves as a global system timer (handles the clock ticks in the system).
 * It is responsible for counting how much clock ticks passed since the beginning of its execution and 
 * notifying every other micro-service (that is subscribed to TickBroadcast) using TickBroadcast.
 */
public class TimeService extends MicroService {
	private int time;
	private int speed;
	private int duration;
	private Timer timer;
	private CyclicBarrier barrier;
	
	/**
	 * 
	 * @param speed - number of milliseconds each clock tick takes
	 * @param duration - number of ticks before termination
	 * @param barrier - a cyclicBarrier intended to count all live threads before global-clock execution/termination
	 */
	public TimeService(int speed,int duration,CyclicBarrier barrier) {
		super("timer");
		time=0;
		this.speed=speed;
		this.duration=duration;
		this.barrier = barrier;
	}
	
	/**
	 * A thread which runs the time clock as long as current tick < duration.
	 * While running, the ticker sends TickBroadcast to let all other micro-services about global time.
	 * When reaching duration with current tick, the ticker sends a termination message and waiting for all
	 * other working threads to finish their tasks before terminating the program.
	 */
	class Ticker extends TimerTask {

		@Override
		public void run() {
			time++;
			if(time<=duration)
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

	/**
	 * A method which uses cyclicBarrier to count all supposed-to-be working threads before 
	 * running the invoking the timer, causing the entire system to run the given program.
	 * The method schedules the ticking at fixed rate.
	 */
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
	
	
	/**
	 * A setter method which recieves a cyclic barrier to set to the timer
	 * @param barr - the cyclicBarrier to set for timer
	 */
	public void setBarrier(CyclicBarrier barr){
		this.barrier=barr;
	}

}
