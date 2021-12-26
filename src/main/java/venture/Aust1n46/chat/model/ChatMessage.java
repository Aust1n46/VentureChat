package venture.Aust1n46.chat.model;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import lombok.Data;

@Data
public class ChatMessage {
	private WrappedChatComponent component;
	private String message;
	private String coloredMessage;
	private int hash;

	public ChatMessage(WrappedChatComponent component, String message, String coloredMessage, int hash) {
		this.component = component;
		this.message = message;
		this.coloredMessage = coloredMessage;
		this.hash = hash;
	}
}
