package org.nectarframework.base.config;

import org.nectarframework.base.exception.ConfigurationException;

public interface ConfigurationBuilder<T> {
	public Configuration buildConfiguration(T configSource) throws ConfigurationException;
}
