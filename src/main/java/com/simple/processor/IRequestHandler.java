package com.simple.processor;

public interface IRequestHandler<T> 
{
	/**
     * A thread-safe method to process a single request
     * @param o - request object
     */
    void processRequests(T o) throws Exception;
}
