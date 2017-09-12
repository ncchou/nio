package com.free.nio.common;

public class TaskWrapper<V> {
	
	private final Task<V> task;
	private final TaskFuture<V> future;
	
	public TaskWrapper(final Task<V> task){
		this.task = task;
		future = new TaskFutureImpl<V>(this);
		
	}
	
	boolean execute() throws TaskException{
		return task.execute();
	}
	
	public TaskFuture<V> getFuture(){
		return future;
	}
	
	public Task<V> getTask(){
		return this.task;
	}
	
	
	public TrafficManager getTrafficManager(){
		return task.getTrafficManager();
	}
}
