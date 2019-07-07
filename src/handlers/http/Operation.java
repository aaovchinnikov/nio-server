package handlers.http;

public enum Operation {
	READ,WRITE;
	
	public boolean isRead() {
		return this.equals(READ);
	}
	
	public boolean isWrite() {
		return this.equals(WRITE);
	}
}
