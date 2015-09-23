package org.erc.qmm.monitor;

public final class PollEvent {

	private int enqueued;
	
	private int dequeued;
	
	private int depth;
	
	private int maxDepth;

	public int getEnqueued() {
		return enqueued;
	}

	public void setEnqueued(int enqueued) {
		this.enqueued = enqueued;
	}

	public int getDequeued() {
		return dequeued;
	}

	public void setDequeued(int dequeued) {
		this.dequeued = dequeued;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	
	
	
}
