package org.nectarframework.junit.service;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nectarframework.base.config.BasicConfig;
import org.nectarframework.base.config.ElementConfigurationBuilder;
import org.nectarframework.base.exception.ConfigurationException;
import org.nectarframework.base.service.Log;
import org.nectarframework.base.service.Nectar;
import org.nectarframework.base.service.cache.HashMapCacheService;
import org.nectarframework.base.service.log.LogLevel;

public class HashMapCacheServiceTest {

	@Before
	public void setup() throws FileNotFoundException, ConfigurationException {
		BasicConfig basicConfig = new BasicConfig(new File("config/junitTest.xml"), "unitTest", "hashMapCacheService",
				LogLevel.TRACE, Optional.empty());
		Nectar.run(new ElementConfigurationBuilder().buildConfiguration(basicConfig));
	}

	@After
	public void breakDown() {
		Nectar.stop();
	}

	@Test
	public void basicTest() throws FileNotFoundException {
		Log.info("test is running");

		HashMapCacheService cs = Nectar.getService(HashMapCacheService.class);
		// should be empty
		assertFalse(cs.getObject("void").isPresent());
	}

}
