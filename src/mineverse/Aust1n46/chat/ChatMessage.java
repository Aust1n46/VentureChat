package mineverse.Aust1n46.chat;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

//This class is used to create ChatMessage objects, which are used to store information about previous text components
//that were sent to the player.  This is a main component in making the message remover work.
public class ChatMessage {
	private WrappedChatComponent component;
	private String message;
	private int hash;

	public ChatMessage(WrappedChatComponent component, String message, int hash) {
		this.component = component;
		this.message = message;
		this.hash = hash;
	}

	public WrappedChatComponent getComponent() {
		return this.component;
	}

	public void setComponent(WrappedChatComponent component) {
		this.component = component;
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
}