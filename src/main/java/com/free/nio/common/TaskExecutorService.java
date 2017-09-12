package com.free.nio.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;

public class TaskExecutorService {
	
	private final TaskQueue taskQueue;
	
	private final ExecutorService executorService; 
	
	private volatile boolean isShutdown = false;
	
	private final int numThreads;
	
	private List<Future<ConsumerState>> futures = Collections.synchronizedList(new ArrayList<>()); 
	
	public TaskExecutorService(final int numThreads){
		executorService = Executors.newFixedThreadPool(numThreads);
		taskQueue = new TaskQueue();
		this.numThreads = numThreads;
	}
	
	public <V> TaskFuture<V> submit(final Task<V> task) throws TaskException{
		if(task.getTrafficManager() == null){
			throw new TaskException("resource manager is null");
		}
		final TaskWrapper<V> taskWrapper = new TaskWrapper<>(task);
		
		if(futures.size()<numThreads){
			futures.add(executorService.submit(new Consumer(this,taskQueue)));
		}
		
		taskQueue.add(taskWrapper);
		return taskWrapper.getFuture();
	}
	
	public void shutdown(final long timeout, final TimeUnit unit){
		if(isShutdown){
			return;
		}
		isShutdown = true;
		executorService.shutdown();
		try {
			executorService.awaitTermination(timeout, unit);
		} catch (InterruptedException e) {
			//do nothing here
		}
		if(!executorService.isShutdown() || !executorService.isTerminated()){
			throw new RuntimeException("executor service failed to shutdown");
		}
	}
	
	public boolean isShutdown(){
		return isShutdown;
	}
}
