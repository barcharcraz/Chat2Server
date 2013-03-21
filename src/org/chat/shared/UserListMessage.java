package org.chat.shared;
import java.util.ArrayList;
import org.chat.server.sql.*;
/**
 * This kind of message is designed to send a list of users in a particular chat room.
 * @author barto
 *
 */
public class UserListMessage extends Message {
	ArrayList<String> users;
	private String type;
	private MySqlConnection sql;
	private String destination;
	public UserListMessage() {
		sql = new MySqlConnection();
		this.type = "UserListMessage";
		this.users = sql.getActiveUsers();
		this.destination = "new chat";
		sql.close();
		
	}
	public UserListMessage(String room) {
		sql = new MySqlConnection();
		this.type = "UserListMessage";
		this.users = sql.getUsersInRoom(room);
		this.destination = room;
		sql.close();
	}
	public String getType() {
		return type;
	}
	public ArrayList<String> getUsers() {
		return users;
	}
	public String getDestination() {
		return destination;
	}
	
}
