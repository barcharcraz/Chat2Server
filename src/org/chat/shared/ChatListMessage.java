package org.chat.shared;

import java.util.ArrayList;

import org.chat.server.sql.MySqlConnection;
/**
 * this message contains the list of chats that are currently made
 * 
 * the constructor retrieves the list right from the SQL server
 * @author charlie
 *
 */
public class ChatListMessage extends Message {
	ArrayList<String> rooms;
	private String type;
	private MySqlConnection sql;
	public ChatListMessage() {
		sql = new MySqlConnection();
		this.type = "ChatListMessage";
		this.rooms = sql.getRooms();
		sql.close();
	}
	public String getType() {
		return type;
	}
	public ArrayList<String> getRooms() {
		return rooms;
	}
}
