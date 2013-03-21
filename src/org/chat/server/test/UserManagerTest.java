package org.chat.server.test;

public class UserManagerTest {
	private String name;
	private boolean isAlive = false;
	public UserManagerTest() {
		this.name = "Test Object";
		isAlive = true;
	}
	public String getName() {
		return name;
	}
	public boolean isAlive() {
		return isAlive;
	}
}
