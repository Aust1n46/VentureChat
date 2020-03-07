package mineverse.Aust1n46.chat.listeners;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.versions.VersionHandler;

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
		if(event.isCancelled() || event.getPacketType() != PacketType.Play.Server.CHAT || event.getPacket().getChatComponents().read(0) == null) {
			return;
		}
		
		try {
			if(VersionHandler.is1_7_10() || VersionHandler.is1_7_9() || VersionHandler.is1_7_2()) {
				if((MineverseChat.posField != null) && !(((boolean) MineverseChat.posField.get(event.getPacket().getHandle())))) {
					return;
				}
			}
			else if(VersionHandler.is1_8()) {
				if((MineverseChat.posField != null) && (((Byte) MineverseChat.posField.get(event.getPacket().getHandle())).intValue() > 1)) {
					return;
				}
			}
			else if(VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11()){
				if((MineverseChat.posField != null) && (((Byte) MineverseChat.posField.get(event.getPacket().getHandle())).intValue() > 1)) {
					return;
				}
			}
			else {
				if((MineverseChat.posField != null) && ((Object) MineverseChat.posField.get(event.getPacket().getHandle())) == MineverseChat.chatMessageType.getEnumConstants()[2]) {
					return;
				}
			}
		}
		catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		WrappedChatComponent chat = event.getPacket().getChatComponents().read(0);
		String message = null;
		int hash = -1;
		message = MineverseChat.toPlainText(chat.getHandle(), chat.getHandleType());
		hash = message != null ? message.hashCode() : -1;
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());
		if((message != null) && (chat.getHandle() != null) && mcp != null) {
			mcp.addMessage(new ChatMessage(chat, null, message, hash));
		}
	}
}