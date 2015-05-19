package com.simple.processor;

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import com.simple.processor.Processor;
import com.simple.processor.RequestHandler;

public class ProcessorTest
{
    private Logger log = Logger.getLogger(getClass().getName());
    Processor<String> processor;

    @Before
    public void init()
    {
	processor = new Processor<String>(new RequestHandler<String>(), 3);
    }
    
    @Test
    public void runProcessorPositive()
    {
	processor.addRequest("Request1");
	processor.addRequest("Request2");
	processor.addRequest("Request3");
	processor.addRequest("Request4");
	processor.addRequest("Request5");
	
	processor.shutDown(null);

	processor.addRequest("Request6");

	while(!processor.isShutDown())
	{
	    log.info("There are opened requests so far");
	}
    }
    
}
