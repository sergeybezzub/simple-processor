package com.simple.processor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Processor<T>
{
    private Logger log = Logger.getLogger(getClass().getName());

    private IRequestHandler<T> rh;
    private int maxThreads;
    private Queue<T> requests;

    private ExecutorService processorThreadPool;

    /**
     * Processes requests from the queue with no more than maxThreads threads
     * For each request object calls IRequestHandler<T>.processRequest(o) only
     * once in a separate thread When the queue is empty and all processing is
     * finished no threads exist.
     * 
     * @param rh
     *            - an object that handles requests
     * @param maxThreads
     *            - total number of threads
     */
    public Processor(IRequestHandler<T> rh, int maxThreads)
    {
	this.rh = rh;
	this.maxThreads = maxThreads;
	requests = new LinkedList<T>();

	processorThreadPool = Executors.newFixedThreadPool(this.maxThreads);
    }

    /**
     * Puts the request into a queue, does not wait for the request to complete
     * 
     * @param o
     *            - request object
     */
    public void addRequest(T o)
    {
	/**
	 * Add a new request to process if processor is not shut down
	 */
	if (!isShutDown())
	{
	    synchronized (requests)
	    {
		requests.add(o);
		log.info("New request has been added - " + o);
	    }
	}
	else
	{
	    log.warning("Request couldn't be added because Processor.shutDown() method was started.");
	}

	try
	{
	    processRequests();
	}
	catch (Exception e)
	{
	    log.severe("Request has failed! " + e);
	}
    }

    /**
     * OPTIONAL Asynchronous shutdown, returns immediately. Instructs the
     * processor to stop accepting requests and finish existing tasks
     * 
     * @param o � if not null, notifies all waiting threads on this object
     *            upon successful shutdown
     */
    public void shutDown(Object o)
    {
	try
	{
	    log.info("Shut down was activated.");
	    processorThreadPool.shutdown();
	    boolean result = processorThreadPool.awaitTermination(5,
		    TimeUnit.MINUTES);

	    if (result && o != null)
	    {
		synchronized (this)
		{
		    o.notifyAll();
		}
	    }
	}
	catch (final InterruptedException e)
	{
	    log.severe("PoolThread awaiting has been terminated!" + e);
	}
    }

    /**
     * OPTIONAL
     * 
     * @returns true if the processor is shut down
     */
    public boolean isShutDown()
    {
	return processorThreadPool.isShutdown();
    }

    private void processRequests() throws Exception
    {

	final T request;
	synchronized (requests)
	{
	    request = requests.poll();
	}

	if (request != null)
	{
	    final Callable<Void> asyncHandler = new Callable<Void>()
	    {
		@Override
		public Void call() throws Exception
		{

		    log.info("Request processing: " + request);
		    synchronized (request)
		    {
			rh.processRequests(request);
		    }
		    return null;
		}

	    };
	    processorThreadPool.submit(asyncHandler);
	}
    }

}
