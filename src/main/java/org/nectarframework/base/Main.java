package org.nectarframework.base;

import org.nectarframework.base.config.CommandLineConfigurationBuilder;
import org.nectarframework.base.service.Nectar;

public class Main {
	public static void main(String[] args) {
		try {
			Nectar.run(new CommandLineConfigurationBuilder().buildConfiguration(args));
		} catch (Throwable t) {
			System.err.println("Unexpected Throwable from org.nectarframework.base.Main.main()");
			t.printStackTrace(System.err);
			Nectar.stop();
			System.exit(-1);
		}
	}
}
