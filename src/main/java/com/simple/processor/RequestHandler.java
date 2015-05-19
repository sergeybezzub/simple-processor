package com.simple.processor;

import java.util.logging.Logger;

public class RequestHandler<T> implements IRequestHandler<T>
{
    private Logger log = Logger.getLogger(getClass().getName());
    
    @Override
    public void processRequests(T o) throws Exception
    {
	log.info("Request processing... "+o);
	Thread.sleep(2000);	
    }

}
