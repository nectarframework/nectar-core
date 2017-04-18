package org.nectarframework.base.service.thread;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;
import org.nectarframework.base.service.Log;

public class ThreadPool {
	private Stack<ThreadServiceWorker> threads = new Stack<ThreadServiceWorker>();
	private Stack<ThreadServiceWorker> idleWorkers = new Stack<ThreadServiceWorker>();
	private HashSet<ThreadServiceWorker> busyWorkers = new HashSet<ThreadServiceWorker>();
	
	private ThreadService threadService;
	private String threadPoolName;
	private PriorityQueue<ThreadServiceTask> taskQueue = new PriorityQueue<>();

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

	public void execute(ThreadServiceTask task) {
		if (idleWorkers.empty()) {
			if (threads.size() < maxWorkerThreads) {
				ThreadServiceWorker tsw = new ThreadServiceWorker(threadService, this);
				tsw.start();
				threads.add(tsw);
				tsw.setTask(task);
				synchronized (tsw) {
					tsw.notify();
				}
				busyWorkers.add(tsw);
			} else {
				while (taskQueue.size() >= this.maxQueueLength) {
					// taskQueue is full. let's just wait a while

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

	public void shutdown() {
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
	}

	public String getThreadPoolName() {
		return threadPoolName;
	}
	
}
