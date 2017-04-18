package org.nectarframework.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nectarframework.base.config.Configuration;
import org.nectarframework.base.exception.ServiceUnavailableException;
import org.nectarframework.base.exception.ServiceUnavailableRuntimeException;

public final class Nectar {
	public static final String VERSION = "0.1.0";

	Configuration config = null;
	/** only one instance of this class allowed!! */
	static Nectar instance = null;

	RunState runState = RunState.none;

	HashMap<String, Service> serviceDirectory = new HashMap<String, Service>();
	/**
	 * if you're not on the register, you're not a real Service...
	 */
	HashMap<Class<? extends Service>, Service> registerByClass = new HashMap<>();
	HashMap<Service, List<Service>> dependencies = new HashMap<>();

	NectarShutdownHandler mainShutdownHandler;

	public enum RunState {
		none, configured, initialized, running, restarting, shutdown
	}

	public enum ExitCode {
		OK(0), BAD(-1), CONFIG_ERROR(-2), INITIALIZATION_ERROR(-3);

		private int exitCodeValue;

		ExitCode(int exitCodeValue) {
			this.exitCodeValue = exitCodeValue;
		}

		public int getValue() {
			return exitCodeValue;
		}
	}

	Nectar() {
	}

	public static boolean run(Configuration config) {
		return new Nectar().runNectar(config);
	}

	synchronized boolean runNectar(Configuration config) {
		if (instance != null) { // there can only be one!!
			throw new IllegalStateException("Cannot instantiate Nectar more than once.");
		}
		instance = this;
		setConfig(config);
		hookInNewShutdownHandler();
		return (initServices() && startServices());
	}

	synchronized void hookInNewShutdownHandler() {
		if (mainShutdownHandler != null) {
			Runtime.getRuntime().removeShutdownHook(mainShutdownHandler);
		}
		mainShutdownHandler = new NectarShutdownHandler(this);
		Runtime.getRuntime().addShutdownHook(mainShutdownHandler);
	}

	void setConfig(Configuration config) {
		if (config == null) {
			throw new NullPointerException("Cannot set a null Configuration");
		}
		if (runState != RunState.none) {
			throw new IllegalStateException("Nectar is already running.");
		}
		this.config = config;
		runState = RunState.configured;
	}

	/**
	 * The second stage to the booting process.
	 * 
	 * Here we look at the config, and instantiate services...
	 * 
	 * then we make sure dependendancies are available.
	 * 
	 * We switch to RUN_STATE.initialized
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	boolean initServices() {
		Log.info("ServiceRegister is initializing services for " + config.getNodeName() + "@" + config.getNodeGroup());
		if (runState != RunState.configured && runState != RunState.restarting) {
			throw new IllegalStateException();
		}

		List<Service> serviceList = config.getServiceList(this.config.getNodeGroup());
		if (serviceList == null) {
			Log.fatal("No services configured for nodeGroup " + config.getNodeGroup());
			return false;
		}
		for (Service s : serviceList) {
			Log.trace("Registering " + s.getClass().getName());
			registerByClass.put(s.getClass(), s);
			Class<?> superClass = s.getClass().getSuperclass();
			while (!superClass.equals(Service.class)) {
				Log.trace("Registering " + superClass.getName());
				registerByClass.put((Class<? extends Service>) superClass, s);
				superClass = superClass.getSuperclass();
			}
		}
		for (Service s : serviceList) {
			if (s.getRunState() == Service.State.none) {
				try {
					if (!initService(s))
						return false;
				} catch (ServiceUnavailableException e) {
					Log.fatal(e);
					return false;
				}
			} else if (s.getRunState() != Service.State.initialized) {
				Log.fatal("ServiceRegister.init() has Service " + s.getClass().getName() + " in irregular state: "
						+ s.getRunState());
				return false;
			}
		}

		this.runState = RunState.initialized;
		return true;
	}

	boolean initService(Service s) throws ServiceUnavailableException {
		if (!s.establishDependencies()) {
			// Log.fatal("ServiceResgster.init: Service " +
			// s.getClass().getName() +
			// " returned false on Service.establishDependancies().");
			throw new ServiceUnavailableException("ServiceResgister.init: Service " + s.getClass().getName()
					+ " returned false on Service.establishDependancies().");
		}

		if (s.init()) {
			s.setRunState(Service.State.initialized);
		} else {
			Log.fatal("Service: " + s.getClass().toString() + " failed to initialize!");
			return false;
		}
		Class<?> cl = s.getClass().getSuperclass();
		while (cl.getName().compareTo("nectar.base.service.Service") != 0 && cl.getName() != "java.lang.Object") {
			cl = cl.getClass().getSuperclass();
		}

		return true;
	}

	public static boolean stop() {
		if (instance != null) {
			return instance.shutdown();
		}
		return true;
	}

	boolean shutdown() {
		Log.info("[ServiceRegister] shutdown triggered.");
		for (Service s : serviceDirectory.values()) {
			shutdownDependancies(s);
		}

		for (Service s : serviceDirectory.values()) {
			s.__rootServiceShutdown();
		}
		if (runState != RunState.restarting) {
			runState = RunState.shutdown;
		}
		serviceDirectory.clear();
		instance = null;
		Log.info("[ServiceRegister] Nectar is closed for business.");
		return true;
	}

	boolean startServices() {
		Log.info("ServiceRegister is running services...");
		List<Service> serviceList = config.getServiceList(this.config.getNodeGroup());
		if (serviceList == null) {
			Log.fatal("Null services configured for node group " + config.getNodeGroup());
			return false;
		}
		if (serviceList.isEmpty()) {
			Log.fatal("No services configured for node group " + config.getNodeGroup());
			return false;
		}
		for (Service s : serviceList) {
			Log.trace("running Service: " + s.getClass().getName());
			if (!s.rootServiceRun()) {
				Log.info("ServiceRegister: Service " + s.getClass().getName() + " failed to run()");
				System.exit(-1);
			}
		}
		this.runState = RunState.running;
		Log.info("ServiceRegister: All services are up and running.");
		return true;
	}

	public static Nectar getInstance() {
		return instance;
	}

	public Configuration getConfiguration() {
		return config;
	}

	Service addServiceDependancy(Service service, Class<? extends Service> serviceClass)
			throws ServiceUnavailableException {
		Service dependant = instance.registerByClass.get(serviceClass);
		if (dependant == null) {
			return null;
		}

		if (dependant.getRunState() == Service.State.none) {
			if (!initService(dependant)) {
				return null;
			}
		}

		if (dependencies.containsKey(service)) {
			dependencies.get(service).add(dependant);
		} else {
			ArrayList<Service> list = new ArrayList<Service>();
			list.add(dependant);
			dependencies.put(service, list);
		}

		return dependant;
	}

	boolean shutdownDependancies(Service service) {
		List<Service> list = dependencies.get(service);

		for (Service dependant : list) {
			if (dependant.getRunState() == Service.State.running) {
				if (!shutdownDependancies(dependant)) {
					return false;
				}
				if (!dependant.rootServiceRun()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Get the instance of the Service implemented by the given serviceClass.
	 * 
	 * 
	 * 
	 * @param serviceClass
	 * @return
	 * @throws ServiceUnavailableRuntimeException
	 */
	public static <T extends Service> T getService(Class<T> serviceClass) {
		if (instance == null) {
			throw new ServiceUnavailableRuntimeException("Nectar is not running, or not available.");
		}
		switch (instance.runState) {
		case initialized:
		case running:
			Service service = instance.getServiceByClass(serviceClass);
			if (service == null)
				return null;
		case none:
			throw new ServiceUnavailableRuntimeException("Nectar is not started.");
		case restarting:
			throw new ServiceUnavailableRuntimeException("Nectar is restarting.");
		case configured:
			throw new ServiceUnavailableRuntimeException("Nectar is configured, but not yet initialized.");
		case shutdown:
			throw new ServiceUnavailableRuntimeException("Nectar is shut down.");
		}
		// unreachable code since all switch cases are addressed.
		throw new ServiceUnavailableRuntimeException(
				"Nectar is in an unknown RUN_STATE, and has no idea what's going on.");
	}

	@SuppressWarnings("unchecked")
	<T> T getServiceByClass(Class<T> serviceClass) {
		return (T) this.registerByClass.get(serviceClass);
	}

}
