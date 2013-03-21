package org.chat.shared;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * Text messages are the actual text chats that clients send to other clients
 * 
 * @author charlie
 *
 */
public class TextMessage extends Message {
	private String data;
	private String destination;
	private String source;
	private String type;
	/**
	 * construct a text message to transmit text over the network
	 * @param destination the destination user of this message
	 * @param source the source of the message
	 * @param data the data in the message
	 */
	public TextMessage(String destination, String source, String data) {
		this.destination = destination;
		this.source = source;
		this.data = data;
		this.type = "TextMessage";
	}
	/**
	 * crreates a new message object from a JSONObject
	 * @param JSONMessage the JSONObject to supply the data for this message
	 */
	public TextMessage(JSONObject JSONMessage) {
		parseJSON(JSONMessage);
	}
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}
	/**
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * 
	 * @return the type of the message
	 */
	public String getType() {
		return type;
	}
	/**
	 * 
	 * @param type the type of the message
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * parse a json object into this message
	 * @param JSON the json object to be parsed
	 */
	public void parseJSON(JSONObject JSON) {
		try {
		source = JSON.getString("source");
		data = JSON.getString("data");
		destination = JSON.getString("destination");
		type = JSON.getString("type");
		} catch (JSONException ex) {
			System.out.println("error parsing JSON :: 1");
		}
		
	}
}
