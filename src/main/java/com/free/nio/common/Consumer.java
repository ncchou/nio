package com.free.nio.common;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.Callable;

@SuppressWarnings("rawtypes")
public class Consumer implements Callable<ConsumerState>{
	
	private final TaskExecutorService executorService;
	
	private LinkedList<TaskWrapper> tasks;
	
	private ListIterator<TaskWrapper> taskIter;
	
	private final TaskQueue taskQueue;
	
	public Consumer(final TaskExecutorService executorService,final TaskQueue taskQueue){
		this.executorService = executorService;
		this.taskQueue = taskQueue;
		tasks = new LinkedList<>();
		taskIter = tasks.listIterator();
	}
	
	@Override
	public ConsumerState call() throws Exception {
		boolean done = false;
		while(!done && !executorService.isShutdown()){
			TaskWrapper oldTask = null;
			final TaskWrapper newTask = taskQueue.getTask();
			
			if(!taskIter.hasNext()){
				taskIter = tasks.listIterator();
			}
			if(taskIter.hasNext()){
				oldTask = taskIter.next();
			}
			
			if(taskIter.hasNext()){
				oldTask = taskIter.next();
			}else{
				taskIter = tasks.listIterator();
			}
			if(newTask!=null){
				if(!runTask(newTask)){
					taskIter.add(newTask);
				}
			}
			if(oldTask!=null){
				if(runTask(oldTask)){
					taskIter.remove();
				}
			}
		}
		return ConsumerState.SUCCESS;
	}
	
	private boolean runTask(TaskWrapper task){
		boolean finished = false;
		TrafficManager resourceManager = task.getTrafficManager();
		if(resourceManager != null){
			
			try {
				if(resourceManager.enterExecute()){
					finished = task.execute();
				}
			} catch (TaskException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				resourceManager.exitExecute();
			}
		}
		return finished;
	}

}
