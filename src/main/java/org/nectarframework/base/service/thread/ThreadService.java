package org.nectarframework.base.service.thread;

import java.util.HashMap;

/**
 * This version of the ThreadService manages multiple pools of Threads. ThreadTasks must define which pool they are to be executed in. This allows much finer control over task priority and resource usage.
 */

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.nectarframework.base.exception.ServiceUnavailableException;
import org.nectarframework.base.service.Log;
import org.nectarframework.base.service.Service;
import org.nectarframework.base.service.ServiceParameters;
import org.nectarframework.base.tools.Sanity;

public class ThreadService extends Service {

	private MasterThread masterThread = null;

	private boolean keepThreadsRunning = false;

	private ThreadPool generalThreadPool;

	private ConcurrentHashMap<String, ThreadPool> threadPools = new ConcurrentHashMap<>();

	private ConcurrentHashMap<ThreadPool, PriorityQueue<ThreadServiceTask>> taskQueue = new ConcurrentHashMap<>();

	public static final String generalThreadPoolName = "generalThreadPool";

	@Override
	protected boolean establishDependencies() throws ServiceUnavailableException {
		// ThreadService is don't need no one!
		return true;
	}

	@Override
	public void checkParameters(ServiceParameters sp) {
		int minWorkerThreads = sp.getInt("minWorkerThreads", 1, 1000, 10);
		int maxWorkerThreads = sp.getInt("maxWorkerThreads", 1, 10000, 20);
		int maxQueueLength = sp.getInt("maxQueueLength", 1, Integer.MAX_VALUE, 10000);
		generalThreadPool = new ThreadPool(this, generalThreadPoolName, minWorkerThreads, maxWorkerThreads, maxQueueLength);
		threadPools.put(generalThreadPoolName, generalThreadPool);
	}

	@Override
	protected boolean init() {
		keepThreadsRunning = true;
		masterThread = new MasterThread(this);

		masterThread.start();

		for (ThreadPool threadPool : threadPools.values()) {
			threadPool.init();
		}

		return true;
	}

	@Override
	protected synchronized boolean run() {
		return true;
	}

	public void wakeUp() {
		synchronized(masterThread) {
			masterThread.notify();
		}
	}
	
	/**
	 * Execute the given task as soon as possible
	 * 
	 * @param task
	 */

	public synchronized void execute(ThreadServiceTask task) {
		if (!keepThreadsRunning) {
			Log.warn("ThreadService was asked to execute a task while shut down, request ignored.");
		} else if (idleWorkers.empty()) {
			if (threads.size() < maxWorkerThreads) {
				Sanity.nn(task.getThreadPoolName());
				ThreadPool threadPool = threadPools.get(task.getThreadPoolName());
				Sanity.nn(threadPool);
				ThreadServiceWorker tsw = new ThreadServiceWorker(this, threadPool);
				tsw.start();
				threads.add(tsw);
				tsw.setTask(task);
				synchronized (tsw) {
					tsw.notify();
				}
				busyWorkers.add(tsw);
			} else {
				while (taskQueue.size() >= this.maxQueueLength) {
					// taskQueue is full.
					// let's just wait a while
					// Log.trace("ThreadService task queue is full. waiting a
					// bit.");

					// TODO: Benchmark difference between Thread.wait(1); and
					// Thread.yield();
					Thread.yield();
				}
				taskQueue.add(task);
			}
		} else {
			ThreadServiceWorker worker = idleWorkers.pop();
			worker.setTask(task);
			synchronized (worker) {
				worker.notify();
			}
			busyWorkers.add(worker);
		}
	}

	@Override
	protected synchronized boolean shutdown() {
		keepThreadsRunning = false;
		this.masterThread.notify();
		
		try {
			this.masterThread.join(1000);
		} catch (InterruptedException e1) {
			Log.warn(e1);
		}
		for (ThreadServiceWorker tsw : threads) {
			try {
				tsw.notify();
				tsw.join(1000);
			} catch (InterruptedException e) {
				Log.warn(e);
			}
		}
		this.busyWorkers.clear();
		this.idleWorkers.clear();
		this.threads.clear();
		return true;
	}

	public void reportException(ThreadServiceWorker threadServiceWorker, ThreadServiceTask task, Throwable e) {
		Log.fatal(task.getClass().getName(), e);
	}

	public void waitOn(ThreadServiceTask task) {
		synchronized (task) {
			while (!task.isComplete()) {
				try {
					task.wait();
				} catch (InterruptedException e) {
					Log.fatal(e);
				}
			}
		}
	}

	public boolean isRunning() {
		return keepThreadsRunning;
	}

	/**
	 * Execute the given task after delay milliseconds at the earliest. The task
	 * will NOT be executed before delay milliseconds.
	 * 
	 * @param task
	 * @param delay
	 */

	public void executeLater(ThreadServiceTask task, long delay) {
		this.executeAtTime(task, System.currentTimeMillis() + delay);
	}

	/**
	 * Execute the given task at the given time (milliseconds since Epoch). Any
	 * positive number will queue the task behind any tasks added by execute().
	 * 
	 * @param task
	 * @param time
	 */

	public void executeAtTime(ThreadServiceTask task, long time) {
		task.setExecuteTime(time);
		this.masterThread.addTask(task);
	}

	/**
	 * PLACEHOLDER METHOD / NOT IMPLEMENTED!
	 * 
	 * Executes the given task immediately, then at least every delay
	 * milliseconds afterwards. It will not execute task again unless the
	 * previous call to task has finished.
	 * 
	 * @param task
	 * @param delay
	 */
	public synchronized void executeRepeat(ThreadServiceTask task, long delay) {
		// TODO: implement me!!
	}


}
