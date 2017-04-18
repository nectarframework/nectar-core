package org.nectarframework.base.service;


class NectarShutdownHandler extends Thread {
	private Nectar nectar;

	public NectarShutdownHandler(Nectar nectar) {
		this.nectar = nectar;
	}

	public void run() {
		nectar.shutdown();
	}
}