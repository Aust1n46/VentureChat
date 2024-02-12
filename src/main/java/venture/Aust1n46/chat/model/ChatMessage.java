package venture.Aust1n46.chat.model;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
	private WrappedChatComponent component;
	private String message;
	private String coloredMessage;
	private int hash;
}
