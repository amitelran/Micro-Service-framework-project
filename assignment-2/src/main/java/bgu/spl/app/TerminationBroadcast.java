package bgu.spl.app;

import bgu.spl.mics.Broadcast;

/**
 * a broadcast message that is sent to all running micro-services notifying program termination.
 */
public class TerminationBroadcast implements Broadcast {
	public TerminationBroadcast(){}
}
