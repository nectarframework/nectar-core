package org.nectarframework.base.service.log;

import java.util.Optional;

public enum LogLevel {
	TRACE(1), DEBUG(2), INFO(3), WARN(4), ERROR(5), FATAL(6), SILENT(7);

	private final int logLevelInt;

	LogLevel(int logLevelInt) {
		this.logLevelInt = logLevelInt;
	}

	public static Optional<LogLevel> lookup(String logLevelName) {
		for (LogLevel logLevel : LogLevel.values()) {
			if (logLevel.name().compareToIgnoreCase(logLevelName) == 0) {
				return Optional.of(logLevel);
			}
		}
		return Optional.empty();
	}
	
	public int getLogLevelIntValue() {
		return logLevelInt;
	}
}
