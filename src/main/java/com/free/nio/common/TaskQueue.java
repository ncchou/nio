package com.free.nio.common;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskQueue {
	private final Queue<TaskWrapper> queue = new ConcurrentLinkedQueue<>();
	
	//private final List<Future<W>>
	
	TaskWrapper getTask(){
		return queue.poll();
	}
	
	public void add(final TaskWrapper task) throws TaskException{
		if(!queue.offer(task)){
			throw new TaskException("task can't be added");
		}
	}
	
	boolean isEmpty(){
		return queue.isEmpty();
	}
	
	
}
