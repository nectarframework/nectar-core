package org.nectarframework.base.service.thread;

public class ThreadServiceWorker extends Thread {
	private ThreadService ts = null;
	private ThreadPool threadPool = null;
	private ThreadServiceTask task = null;

	public ThreadServiceWorker(ThreadService ts, ThreadPool threadPool) {
		super("ThreadServiceWorker");
		this.ts = ts;
		this.threadPool = threadPool;
	}

	public void run() {
		while (ts.isRunning()) {
			while (task != null) {
				try {
					task.execute();
				} catch (Throwable e) {
					task.setException(e);
					ts.reportException(this, task, e);
				}
				task.setComplete(true);
				synchronized(task) {
					task.notifyAll();
				}
				task = null;
				threadPool.threadReturn(this);
			}
			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {
			}
		}

	}

	public void setTask(ThreadServiceTask task) {
		this.task = task;
	}

	public ThreadPool getThreadPool() {
		return threadPool;
	}
}
