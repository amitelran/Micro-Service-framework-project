package bgu.spl.mics.impl;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentRoundRobinQueue<T> extends ConcurrentLinkedQueue<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Iterator<T> rrIterator = this.iterator();
	
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
