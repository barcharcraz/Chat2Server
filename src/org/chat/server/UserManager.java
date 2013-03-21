package org.chat.server;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import org.chat.server.sql.MySqlConnection;
import org.json.*;
import org.chat.shared.*;
/**
 * Class to manage one user that is connected to the server,
 * Automatically tries to set the name of the user to the
 * first value that is sent over the socket.
 * 
 * <p>
 * 
 * the loop contained in the run method monitors for input from both the
 * client and other users and sends user lists and room lists to the connected client
 * @author BartoC
 *
 */
public class UserManager implements Runnable {
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private String name;
	private TextMessage incomingMessage;
	private MySqlConnection sql;
	private boolean active; //used to control the main loop
	/**
	 * creates the input and output streams and then waits for the name of the connection client to be sent.
	 * @param clientSocket
	 */
	public UserManager(Socket clientSocket) {
		String loginString;
		this.clientSocket = clientSocket;
		this.sql = new MySqlConnection();
		try {
			/*
			 * create the in and out streams
			 */
			out = new PrintWriter(this.clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		} catch (IOException ex) {
			System.out.println("error creating file streams");
			//TODO add more error handling
		}
		/*
		 * trys to get the name of the client
		 * 
		 */
		try {
			System.out.println("looking for name");
			loginString = in.readLine();
			login(loginString);
			active = true;
		} catch (IOException e) {
			System.out.println("Error reading client name");
			//TODO add more error handling
		}
	}
	
	public void run() {
		String JSONString = "";
		JSONObject JSONMessage = null;
		//JSONObject JSONUserList = new JSONObject(new UserListMessage());
		//JSONObject JSONUserListCurrent = new JSONObject(new UserListMessage());
		TextMessage textMessage = null;
		try {
			while(active) {
				/*
				 * while the input stream is ready read a character and append it to a string
				 * This string will be used to make a JSONObject
				 */
				if(in.ready()) {
					JSONString = in.readLine();
					System.out.println(JSONString);

				}
				
				/*
				 * if the input stream is empty and there is something in the JSONString
				 * (meaning the above while loop has retreved a json message)
				 * make a JSONObject out of the string and set the string to empty.
				 */
				if(in.ready() == false && JSONString.equals("") == false) {
					JSONMessage = new JSONObject(JSONString);
					if (JSONMessage.getString("type").equals("SystemMessage")) {
						System.out.println("systemmessage");
						if(JSONMessage.getString("Action").equals("Logout")) {
							active = false;
						} else if(JSONMessage.getString("Action").equals("EnterRoom")) {
							System.out.println("enter room");
							sql.addUserToRoom(name, JSONMessage.getString("data"));
						} else if(JSONMessage.getString("Action").equals("LeaveRoom")) {
							sql.removeUserFromRoom(name, JSONMessage.getString("data"));
						}
						
					/*
					 * if the incoming message is a normal text message then send if off to the client that this
					 * user manager is handling
					 */
						
					} else if(JSONMessage.getString("type").equals("TextMessage")){
						
						textMessage = new TextMessage(JSONMessage);
						sendMessage(textMessage);
						
					}
					JSONString = "";
					textMessage = null;
					JSONMessage = null;
					
				}
				/*
				 * look to see if there is an incoming message from another usermanager
				 * if there is then go ahead and send it to the client
				 */
				if(incomingMessage != null) {
					JSONMessage = new JSONObject(incomingMessage);
					incomingMessage = null;
					out.println(JSONMessage.toString());
					JSONMessage = null;
				}
				
				/*
				 * send user lists for each of the user's chats each time through the loop
				 * 
				 * also send a lsit of the chats that are active
				 */
				sendUserLists();
				sendRoomList();
				
				
			}
			this.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * handles the sending of messages to other user managers
	 * @param message
	 */
	private void sendMessage(TextMessage message) {
		ArrayList<String> DestUsers = sql.getUsersInRoom(message.getDestination());
		for (String currentUser : DestUsers) {
			Server.userManagers.get(currentUser).setIncomingMessage(message);
		}
	}
	/**
	 * sends a user list for each of the rooms the client is in to the client
	 */
	private void sendUserLists() {
		JSONObject JSONUserList;
		ArrayList<String> rooms = sql.getRoomNamesForUser(name);
		for(String room : rooms) {
			JSONUserList = new JSONObject(new UserListMessage(room));
			out.println(JSONUserList.toString());
		}
	}
	/**
	 * sends a list of all the rooms on the server
	 */
	private void sendRoomList() {
		JSONObject JSONChatList;
		JSONChatList = new JSONObject(new ChatListMessage());
		out.println(JSONChatList.toString());
		
	}
	/**
	 * sets the incoming message which is decetd by the manager
	 * @param message
	 */
	public void setIncomingMessage(TextMessage message) {
		incomingMessage = message;
	}
	/**
	 * 
	 * @return name of the connected client
	 */
	public String getName() {
		return name;
	}
	/**
	 * handles login code
	 * </p>
	 * Decides if a new user message or a login message has been sent.
	 * <p>
	 * this is a error prone message as it relys on the user to know what users exist, which they dont. If we 
	 * had more time we would improve this 
	 * @param message
	 */
	private void login(String message) {
		String userName = "";
		String action = "";
		System.out.println(message);
		try {
			/*
			 * get the user information that is contained in the message
			 */
			JSONObject JSONMessage = new JSONObject(message);
			userName = JSONMessage.getString("data");
			action = JSONMessage.getString("Action");
			/*
			 * if the user sent a plain login message
			 * then go ahead and activate the user
			 */
			if(action.equals("Login")) {
				if(sql.doesUserExits(userName)) {
					sql.activateUser(userName);
					name = userName;
					//send a success message if the loign was sucessful
					out.println(new JSONObject(new ErrorMessage("NoError"), true).toString());
				} else {
					out.println(new JSONObject(new ErrorMessage("UserDoesNotExist"), true).toString());
					this.close();
				}
				
			/*
			 * if the user sent an adduser message then add the user then activate that user
			 */
			} else if(action.equals("AddUser")) {
				if(!sql.doesUserExits(userName)) {
					sql.addUser(userName);
					sql.activateUser(userName);
					name = userName;
					//send a success message, actually an error message with no error
					out.println(new JSONObject(new ErrorMessage("NoError"), true).toString());
				} else {
					/*
					 * if the user already exists send an error back to the client.
					 * 
					 * print json object made from a user message with the message user already exists and include superclass,
					 * send a string representation of said json object
					 * 
					 * after printing close this object.
					 */
					out.println(new JSONObject(new ErrorMessage("UserAlreadyExists"), true).toString());
					this.close();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("invalid login");
			e.printStackTrace();
		}
		
	}
	/**
	 * closes all connections and streams
	 * then exits the thread
	 */
	public void close() {
		try {
			sql.deactivateUser(name);
			sql.removeUserFromRoom(name);
			in.close();
			out.close();
			clientSocket.close();
			sql.close();
			Server.userManagers.remove(name);
			active = false;
		} catch(IOException ex) {
			System.out.println("error closing streams");
		}

	}
}
