package org.chat.server;
/**
 * server entry point
 * <p>
 * this will wait for connections and create user managers for them
 * 
 * Once a user manager is created it gets information about itself from the client then goes and runs in its own thread
 * until the user logs off.
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import org.json.*;
import org.chat.shared.*;
import org.chat.server.test.*;
public class Server {
	static volatile Hashtable<String, UserManager> userManagers = new Hashtable();
	static org.chat.server.sql.MySqlConnection db = null;
	private static final int port = 4444;
	public Server() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		UserManager tempUserManager = null;
		/*
		 * Make a server socket on the specified port
		 */
		try {
			System.out.println("making a socket on port " + port);
			serverSocket = new ServerSocket(port);
		} catch (java.io.IOException ex) {
			System.out.println("there was an error making the socket");
		}
		while(true) {
			clientSocket = WaitForConnection(serverSocket);
			tempUserManager = new UserManager(clientSocket);
			System.out.println(tempUserManager.getName());
			if(tempUserManager.getName() != null) {
				userManagers.put(tempUserManager.getName(), tempUserManager);
				new Thread(userManagers.get(tempUserManager.getName())).start();
			}
			tempUserManager = null;
			
		}
	}
	public static void main(String[] args) {
		/*
		 * UserListMessage test
		 * 
		 * UserListMessage testMessage = new UserListMessage(); JSONObject
		 * testJSON = new JSONObject(testMessage);
		 * System.out.println(testJSON.toString());
		 */
		
		/*
		 * temp userManager test
		 * 
		 * UserManagerTest tmp = null; tmp = new UserManagerTest();
		 * userManagers.put(tmp.getName(), tmp); tmp = null;
		 * System.out.println(userManagers.get("Test Object").isAlive());
		 */
		new Server();

	}
	private Socket WaitForConnection(ServerSocket serverSocket) {
		/*
		 * Wait for a client to connect and accept the connection
		 */
		try {
			System.out.println("waiting for client connection");
			return serverSocket.accept();
		} catch (IOException ex) {
			System.out.println("error connection to client");
			return null;
		}
	}

}
