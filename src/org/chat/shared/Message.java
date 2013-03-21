package org.chat.shared;
/**
 * this is the superclass of messages, it just has a type which is the one thing that should be common among all messages
 * 
 * These classes are just wrappers for the messages, they are intended to be used by beans and made into JOSNObjecs
 * <p>
 * these classes do not parse JSONObjects
 * back into the wrapper classes
 * @author charlie
 *
 */
public abstract class Message {
	protected String type;
	public String getType() {
		return type;
	}
}
