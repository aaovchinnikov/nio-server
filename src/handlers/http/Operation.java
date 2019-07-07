package handlers.http;

public enum Operation {
	READ,WRITE,WRITE_AND_CLOSE;
	
	public boolean isRead() {
		return this.equals(READ);
	}
	
	public boolean isWrite() {
		return this.equals(WRITE);
	}
	
	public boolean isWriteAndClose() {
		return this.equals(WRITE_AND_CLOSE);
	}
}
