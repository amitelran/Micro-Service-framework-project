package bgu.spl.mics.impl;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A class extending {@link ConcurrentLinkedQueue} in order to manage a queue in a Round-Robin manner.
 */
public class ConcurrentRoundRobinQueue<T> extends ConcurrentLinkedQueue<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Setting a new {@link Iterator} for the ConcurrentRoundRobinQueue.
	 */
	private Iterator<T> rrIterator = this.iterator();
	
	/**
	 * A method that gets the next element in the ConcurrentRoundRobinQueue if not empty.
	 * The next element after the last element in the queue, is the first element in the queue. Thus,
	 * the iterator goes through the queue elements in a Round-Robin fashion.
	 * @return Returns the next element in the ConcurrentRoundRobinQueue, or null if there is none.
	 */
	public T getNextRR(){
		synchronized(rrIterator){
			if(this.size()>0){
				if(!rrIterator.hasNext())
					rrIterator=this.iterator();
				return rrIterator.next();
			}
			else return null;
		}
	}


}
