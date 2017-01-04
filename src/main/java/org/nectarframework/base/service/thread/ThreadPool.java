package org.nectarframework.base.service.thread;

import java.util.HashSet;
import java.util.Stack;

public class ThreadPool {
	private Stack<ThreadServiceWorker> threads = new Stack<ThreadServiceWorker>();
	private Stack<ThreadServiceWorker> idleWorkers = new Stack<ThreadServiceWorker>();
	private HashSet<ThreadServiceWorker> busyWorkers = new HashSet<ThreadServiceWorker>();
	
	private ThreadService threadService;
	private String threadPoolName;
	
	private int minWorkerThreads = 2;
	private int maxWorkerThreads = 50;
	private int maxQueueLength = 1000;

	public ThreadPool(ThreadService threadService, String threadPoolName, int minWorkerThreads, int maxWorkerThreads, int maxQueueLength) {
		super();
		this.threadService = threadService;
		this.threadPoolName = threadPoolName;
		this.minWorkerThreads = minWorkerThreads;
		this.maxWorkerThreads = maxWorkerThreads;
		this.maxQueueLength = maxQueueLength;
	}

	public boolean init() {
		for (int i = 0; i < minWorkerThreads; i++) {
			ThreadServiceWorker tsw = new ThreadServiceWorker(threadService, this);
			threads.add(tsw);
		}
		for (ThreadServiceWorker tsw : threads) {
			tsw.start();
			idleWorkers.add(tsw);
		}
		return true;
	}

	public void threadReturn(ThreadServiceWorker worker) {
		busyWorkers.remove(worker);
		idleWorkers.add(worker);
		// wake up MasterThread
		threadService.wakeUp();
	}

	
	
}
