package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import handlers.http.Operation;

class OperationTest {

	@Test
	void isReadTest() {
		Operation operation = Operation.READ;
		assertTrue(operation.isRead());
		assertFalse(operation.isWrite());
	}
	
	@Test
	void isWriteTest() {
		Operation operation = Operation.WRITE;
		assertTrue(operation.isWrite());
		assertFalse(operation.isRead());
	}

}
