package org.nectarframework.base.config;

import java.io.File;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.nectarframework.base.exception.ConfigurationException;
import org.nectarframework.base.service.Log;
import org.nectarframework.base.Main;
import org.nectarframework.base.service.Nectar;
import org.nectarframework.base.service.log.LogLevel;

public class CommandLineConfigurationBuilder implements ConfigurationBuilder<String[]> {

	@Override
	public Configuration buildConfiguration(String[] args) throws ConfigurationException {
		try {
			CommandLineParser parser = new DefaultParser();
			Options options = buildArgumentOptions();

			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// override the log level
			if (line.hasOption("log")) {
				setLogLevel(options, line);
			}

			// if user asked for version or help, don't start up, just print
			// that and exit.
			if (line.hasOption("version") || line.hasOption("help")) {
				if (line.hasOption("version")) {
					runVersion();
				} else if (line.hasOption("help")) {
					runHelp(options);
				}
				throw new SilentExitConfigurationException();
			} else {
				// check config
				Optional<BasicConfig> basicConfigOpt = parseArgs(options, line);
				if (!basicConfigOpt.isPresent()) {
					// config check failed, bail out, but print help...
					runHelp(options);
					throw new SilentExitConfigurationException();
				} else {
					// we're good to continue, pass to the next stage...
					BasicConfig basicConfig = basicConfigOpt.get();
					return new ElementConfigurationBuilder().buildConfiguration(basicConfig);
				}
			}
		} catch (ParseException e) {
			throw new ConfigurationException("Couldn't parse command line", e);
		}
	}

	private static Options buildArgumentOptions() {
		Options options = new Options();

		options.addOption("v", "version", false, "print Version number and exit");
		options.addOption("h", "help", false, "print this message");
		options.addOption("cc", "configCheck", false, "run some basic sanity checks on the configuration file");
		options.addOption("s", "scriptMode", false,
				"Instead of running as a server, script mode starts and runs all configured Services, then shuts down.");
		Option opt = new Option("c", "configFile", true, "path to the configuration XML file");
		opt.setArgName("PATH");
		options.addOption(opt);
		opt = new Option("n", "nodeName", true, "give this instance a name");
		opt.setArgName("NAME");
		options.addOption(opt);
		opt = new Option("g", "nodeGroup", true, "the run mode to use, as described in the config file");
		opt.setArgName("GROUP");
		options.addOption(opt);
		opt = new Option("l", "log", true, "set initial log level to {trace, debug, info, warn, fatal, silent}");
		opt.setArgName("LOG");
		options.addOption(opt);

		return options;
	}

	private static void runHelp(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -> " + Main.class.getName(), opts);
	}

	private static void runVersion() {
		System.out.println("Nectar Framework");
		System.out.println("Version: " + Nectar.VERSION);
	}

	private static void setLogLevel(Options options, CommandLine line) {
		if (line.hasOption("log")) {
			String logStr = line.getOptionValue("log");
			LogLevel ll = LogLevel.valueOf(logStr.toUpperCase());
			if (ll != null) {
				Log.logLevelOverride = ll;
				Log.debug("Log Level Override set to " + ll);
			} else {
				Log.warn(
						"Log Level command line argument could not be parsed, reverting to default. Please use the -h switch to show command line usage.");
			}
		}
	}

	private static Optional<BasicConfig> parseArgs(Options options, CommandLine line) {
		boolean missingArgs = false;
		boolean failedArgs = false;
		if (!line.hasOption("configFile")) {
			System.err.println("ERROR: configFile command line argument is required.");
			missingArgs = true;
		}
		if (!line.hasOption("nodeName")) {
			System.err.println("ERROR: nodeName command line argument is required.");
			missingArgs = true;
		}
		if (!line.hasOption("nodeGroup")) {
			System.err.println("ERROR: nodeGroup command line argument is required.");
			missingArgs = true;
		}

		if (missingArgs) {
			return Optional.empty();
		}

		File configFile = new File(line.getOptionValue("configFile"));
		if (!configFile.exists()) {
			System.err.println("configuration file: " + line.getOptionValue("configFile") + " cannot be found.");
			failedArgs = true;
		} else if (!configFile.canRead()) {
			System.err.println("configuration file: " + line.getOptionValue("configFile") + " is not readable.");
			failedArgs = true;
		}

		String nodeName = line.getOptionValue("nodeName");
		String nodeGroup = line.getOptionValue("nodeGroup");

		LogLevel logLevel = Log.DEFAULT_LOG_LEVEL;
		if (!line.hasOption("log")) {
			String logLevelStr = line.getOptionValue("log");
			Optional<LogLevel> logLevelOpt = LogLevel.lookup(logLevelStr);
			if (logLevelOpt.isPresent()) {
				logLevel = logLevelOpt.get();
			} else {
				System.err.println("log argument doesn't look right: '" + logLevelStr
						+ "'. It should be one of these: {trace, debug, info, warn, fatal, silent}.");
				failedArgs = true;
			}
		}

		if (failedArgs) {
			return Optional.empty();
		}

		return Optional.of(new BasicConfig(configFile, nodeName, nodeGroup, logLevel, Optional.empty()));
	}

}
