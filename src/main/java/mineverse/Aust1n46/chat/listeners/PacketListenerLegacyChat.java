package mineverse.Aust1n46.chat.listeners;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.versions.VersionHandler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class PacketListenerLegacyChat extends PacketAdapter {
	public PacketListenerLegacyChat() {
		super(MineverseChat.getInstance(), ListenerPriority.MONITOR, new PacketType[] { PacketType.Play.Server.CHAT });
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		if(event.isCancelled()) {
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(event.getPlayer());
		if(mcp == null) {
			return;
		}
		
		PacketContainer packet = event.getPacket();
		WrappedChatComponent chat = packet.getChatComponents().read(0);
		if(chat == null) {
			return;
		}
		try {
			if(VersionHandler.is1_7()) {
				packet.getBooleans().getField(0).setAccessible(true);
				if(!((boolean) packet.getBooleans().getField(0).get(packet.getHandle()))) {
					return;
				}
			}
			else if(VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11()) {
				packet.getBytes().getField(0).setAccessible(true);
				if(((Byte) packet.getBytes().getField(0).get(packet.getHandle())).intValue() > 1) {
					return;
				}
			}
			else {
				packet.getChatTypes().getField(0).setAccessible(true);
				if(packet.getChatTypes().getField(0).get(packet.getHandle()) == packet.getChatTypes().getField(0).getType().getEnumConstants()[2]) {
					return;
				}
			} 
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		String message = Format.toPlainText(chat.getHandle(), chat.getHandleType());
		String coloredMessage = Format.toColoredText(chat.getHandle(), chat.getHandleType());
		if(message == null) {
			return;
		}
		int hash = message.hashCode();
		mcp.addMessage(new ChatMessage(chat, message, coloredMessage, hash));
	}
}
