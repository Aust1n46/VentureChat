package mineverse.Aust1n46.chat;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

//This class is used to create ChatMessage objects, which are used to store information about previous text components
//that were sent to the player.  This is a main component in making the message remover work.
public class ChatMessage {
	private WrappedChatComponent component;
	private String sender;
	private String message;
	private String format;
	private String chat;
	private String channel;
	private int hash;

	public ChatMessage(String sender, String message, int hash) {
		this.sender = sender;
		this.message = message;
		this.hash = hash;
	}

	public ChatMessage(WrappedChatComponent component, String sender, String message, int hash) {
		this.component = component;
		this.sender = sender;
		this.message = message;
		this.hash = hash;
	}

	public WrappedChatComponent getComponent() {
		return this.component;
	}

	public void setComponent(WrappedChatComponent component) {
		this.component = component;
	}

	public String getSender() {
		return this.sender;
	}

	public String getMessage() {
		return this.message;
	}

	public int getHash() {
		return this.hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}
	
	public String getFormat() {
		return this.format;
	}
	
	public String getChat() {
		return this.chat;
	}
	
	public String getChannel() {
		return this.channel;
	}
}