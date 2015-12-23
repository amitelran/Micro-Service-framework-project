package bgu.spl.app;

import java.util.Timer;
import java.util.TimerTask;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.impl.MessageBusImpl;

public class TimeService extends MicroService {
	private int time;
	private int speed;
	private int duration;
	private Timer timer;
	
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
		// TODO Auto-generated constructor stub
	}
	
	class Ticker extends TimerTask {

		@Override
		public void run() {
			time++;
			if(time<duration)
				MessageBusImpl.getInstance().sendBroadcast(new TickBroadcast(time));
			else{
				MessageBusImpl.getInstance().sendBroadcast(new TerminationBroadcast());
				this.cancel();
			}
		}
		
	}

	@Override
	protected void initialize() {
		timer.schedule(new Ticker(), speed, speed);
	}

}
