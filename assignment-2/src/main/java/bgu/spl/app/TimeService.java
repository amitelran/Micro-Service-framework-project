package bgu.spl.app;

import java.util.Timer;
import java.util.TimerTask;

import bgu.spl.mics.MicroService;

public class TimeService extends MicroService {
	private int time;
	private int speed;
	private int duration;
	private Timer timer;
	TimeService x=this;
	
	/**
	 * 
	 * @param s number of milliseconds each clock tick takes
	 * @param d number of ticks before termination
	 */
	public TimeService(int s,int d) {
		super("timer");
		time=1;
		speed=s;
		duration=d;
		timer=new Timer();
	}
	
	class Ticker extends TimerTask {

		@Override
		public void run() {
			time++;
			if(time<duration)
				TimeService.this.sendBroadcast(new TickBroadcast(time));
			else{
				TimeService.this.sendBroadcast(new TerminationBroadcast());
				this.cancel();
			}
		}
		
	}

	@Override
	protected void initialize() {
		timer.scheduleAtFixedRate(new Ticker(), speed, speed);
	}

}
