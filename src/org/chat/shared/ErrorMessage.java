package org.chat.shared;
/**
 * this is a message to send any errors back to the client if they need to be handled there
 * @author charlie
 *
 */
public class ErrorMessage extends Message {
	/*
	 * the error attribute is a string the specifies the error message
	 * 
	 * it is critical that both the client and server agree on what various errors are and how they are formatted
	 */
	protected String error;
	public ErrorMessage() {
		type = "ErrorMessage";
	}
	public ErrorMessage(String error) {
		this();
		this.error = error;
		
	}
	public String getError() {
		return error;
	}
	public void setError(String errorName) {
		this.error = errorName;
	}
}
