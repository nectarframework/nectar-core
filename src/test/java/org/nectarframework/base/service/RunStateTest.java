package org.nectarframework.base.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Test;
import org.nectarframework.base.config.BasicConfig;
import org.nectarframework.base.config.Configuration;
import org.nectarframework.base.service.log.LogLevel;

public class RunStateTest {

	Configuration buildEmptyConfiguration() {
		return new Configuration(new HashMap<String, List<Service>>(),
				new BasicConfig(null, "test-node", "test-group", LogLevel.TRACE, Optional.empty()));
	}

	@After
	public void reset() {
		Nectar.instance = null;
	}
	
	@Test
	public void noneTest() {
		Nectar nectar = new Nectar();
		assertEquals(Nectar.RunState.none, nectar.runState);
	}

	@Test(expected = IllegalStateException.class)
	public void singletonTest() {
		Nectar nectar = new Nectar();
		Nectar.instance = nectar;
		nectar.runNectar(buildEmptyConfiguration());
	}

	@Test
	public void configuredTest() {
		Nectar nectar = new Nectar();
		nectar.runNectar(buildEmptyConfiguration());
		assertEquals(Nectar.RunState.none, nectar.runState);
	}
}
