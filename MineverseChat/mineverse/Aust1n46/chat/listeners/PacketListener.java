package mineverse.Aust1n46.chat.listeners;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.utilities.Format;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

//This class listens for chat packets and intercepts them before they are sent to the Player.
//The packets are modified to include advanced json formating and the message remover button if the 
//player has permission to remove messages.
public class PacketListener extends PacketAdapter {
	public PacketListener() {
		super(MineverseChat.getInstance(), ListenerPriority.MONITOR, new PacketType[] { PacketType.Play.Server.CHAT });
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		if(event.isCancelled() || event.getPacketType() != PacketType.Play.Server.CHAT) {
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());
		if(mcp == null) {
			return;
		}
		WrappedChatComponent chat = event.getPacket().getChatComponents().read(0);
		if(chat == null) {
			return;
		}
		String message = Format.toPlainText(chat.getHandle(), chat.getHandleType());
		if(message == null) {
			return;
		}
		int hash = message.hashCode();
		mcp.addMessage(new ChatMessage(chat, message, hash));
	}
}