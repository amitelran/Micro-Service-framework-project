package bgu.spl.app;

import bgu.spl.mics.Broadcast;

/**
 * a broadcast message that is sent at every passed clock tick
 */
public class TickBroadcast implements Broadcast {
	private final int tick;
	
	/**
	 * @param tick - current clock tick
	 */
	public TickBroadcast(int tick){
		this.tick=tick;
	}
	
	/**
	 * getter set to return current tick
	 */
	public int getTick(){return tick;}
}
