package de.wagentim.element;

public class Range {
	
	public final static int STATUS_FREE = 0;
	public final static int STATUS_FINISHED = 1;
	public final static int STATUS_REQUIRED = 2;
	
	private long startPoint = -1;
	private long endPoint = -1;
	private int status = STATUS_FREE;
	
	public Range(){ this( 0, 0 ); }
	
	public Range(long startPoint, long endPoint) {
		
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		
	}
	public long getStartPoint() {
		return startPoint;
	}
	public void setStartPoint(long startPoint) {
		this.startPoint = startPoint;
	}
	public long getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(long endPoint) {
		this.endPoint = endPoint;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
