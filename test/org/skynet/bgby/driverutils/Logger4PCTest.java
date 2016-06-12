package org.skynet.bgby.driverutils;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

public class Logger4PCTest {

	@Test
	public void testLogger() {
		String tag1 = "ABCD";
		String tag2 = "1234";
		
		Logger4PC logger = new Logger4PC();
		
		Logger lg1 = logger.logger(tag1);
		Logger lg2 = logger.logger(tag2);
		
		System.out.println(lg1.getName());
		System.out.println(lg2.getName());
		
		lg1.info("I'm log1");
		lg2.info("I'm log2");
	}

}
