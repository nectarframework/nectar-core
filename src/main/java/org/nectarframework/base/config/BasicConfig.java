package org.nectarframework.base.config;

import java.io.File;
import java.util.Optional;

import org.nectarframework.base.service.log.LogLevel;

public class BasicConfig {
	protected final File configFile;
	protected final String nodeName;
	protected final String nodeGroup;
	protected final LogLevel logLevel;
	protected final Optional<String> command;

	public BasicConfig(File configFile, String nodeName, String nodeGroup, LogLevel logLevel,
			Optional<String> command) {
		this.configFile = configFile;
		this.nodeName = nodeName;
		this.nodeGroup = nodeGroup;
		this.logLevel = logLevel;
		this.command = command;
	}

	public BasicConfig(BasicConfig basicConfig) {
		this.configFile = basicConfig.configFile;
		this.nodeName = basicConfig.nodeName;
		this.nodeGroup = basicConfig.nodeGroup;
		this.logLevel = basicConfig.logLevel;
		this.command = basicConfig.command;
	}

	public File getConfigFile() {
		return configFile;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getNodeGroup() {
		return nodeGroup;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public Optional<String> getCommand() {
		return command;
	}
}
