package com.free.nio.common;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TaskFutureImpl<V> implements TaskFuture<V>{

	private final AtomicBoolean isDone = new AtomicBoolean(false);
	
	private final AtomicBoolean isCanceled = new AtomicBoolean(false);
	
	private final AtomicReference<V> dataRef = new AtomicReference<>();
	
	private final AtomicReference<TaskWrapper<V>> taskRef = new AtomicReference<>();
	
	private final Object lock = new Object();
	
	private final AtomicReference<Exception> causeRef = new AtomicReference<>();
	
	
	
	TaskFutureImpl(final TaskWrapper<V> task){
		taskRef.set(task);
	}
	
	@Override
	public boolean cancel(boolean interrupted) {
		synchronized(lock){
			if(taskRef.get() != null){
				taskRef.get().getTask().cancel(interrupted);
			}
			done(true);
		}
		return true;
	}
	
	protected void done(final boolean canceled){
		synchronized(lock){
			if(!isDone.get()){
				TaskWrapper<V> task = taskRef.get();
				if(taskRef.compareAndSet(task, null)){
					dataRef.set(task.getTask().getData());
				}
				isDone.set(true);
				isCanceled.set(canceled);
			}
			lock.notify();
		}
	}
	
	protected void done(final Exception cause){
		synchronized(lock){
			if(!isDone.get()){
				TaskWrapper<V> task = taskRef.get();
				if(taskRef.compareAndSet(task, null)){
					causeRef.set(cause);
					isDone.set(true);
				}
			}
			lock.notify();
		}
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		synchronized(lock){
			while(!isDone.get()){
				lock.wait();
			}
			lock.notify();
		}
		if(causeRef.get() != null){
			throw new ExecutionException(causeRef.get());
		}
		if(isCancelled()){
			throw new CancellationException();
		}
		return dataRef.get();
	}

	@Override
	public V get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCancelled() {
		return isCanceled.get();
	}

	@Override
	public boolean isDone() {
		return isDone.get();
	}

}
