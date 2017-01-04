package org.nectarframework.base.service.thread;

import java.util.SortedSet;
import java.util.TreeSet;

public class MasterThread extends Thread {

	private ThreadService ts = null;

	private SortedSet<ThreadServiceTask> taskSet = null;

	public MasterThread(ThreadService ts) {
		super("ThreadService-MasterThread");
		this.ts = ts;
		taskSet = new TreeSet<ThreadServiceTask>(new ThreadServiceTaskComparator());
	}

	public synchronized void addTask(ThreadServiceTask task) {
		taskSet.add(task);
		notify();
	}

	public synchronized void run() {
		while (ts.isRunning()) {
			if (taskSet.isEmpty()) {
				// nothing to do, wait endlessly.
				try {
					wait();
				} catch (InterruptedException e) {
					// wake up
				}
			} else {
				// when is the next task due?
				long nowTime = System.currentTimeMillis();
				ThreadServiceTask firstTask = taskSet.first();
				long nextTime = firstTask.getExecuteTime();
				if (nowTime >= nextTime) { 
					// task is overdue, so remove it from to the to do list
					taskSet.remove(firstTask);
					// and run it
					ts.execute(firstTask);
				} else {
					// wait til due date
					long waitTime = nextTime - nowTime;
					try {
						wait(waitTime);
					} catch (InterruptedException e) {
						// wake up
					}
				}
			}
		}
	}

}
