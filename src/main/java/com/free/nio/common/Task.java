package com.free.nio.common;

public interface Task<T> {
	
	boolean execute() throws TaskException;
	
	Exception getException();
	
	boolean hasError();
	
	T getData();
	
	TrafficManager getTrafficManager();
	
	boolean cancel(boolean interrupted);
	
}
