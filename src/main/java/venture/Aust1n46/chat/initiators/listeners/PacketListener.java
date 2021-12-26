package venture.Aust1n46.chat.initiators.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.model.ChatMessage;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;
import venture.Aust1n46.chat.utilities.VersionHandler;
import venture.Aust1n46.chat.service.VentureChatFormatService;

//This class listens for chat packets and intercepts them before they are sent to the Player.
//The packets are modified to include advanced json formating and the message remover button if the 
//player has permission to remove messages.
@Singleton
public class PacketListener extends PacketAdapter {
	@Inject
	private VentureChatFormatService formatter;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	
	@Inject
	public PacketListener(final VentureChat plugin) {
		super(plugin, ListenerPriority.MONITOR, new PacketType[] { PacketType.Play.Server.CHAT });
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		if(event.isCancelled() || event.getPacketType() != PacketType.Play.Server.CHAT) {
			return;
		}
		
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(event.getPlayer());
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
		
		String message = formatter.toPlainText(chat.getHandle(), chat.getHandleType());
		String coloredMessage = formatter.toColoredText(chat.getHandle(), chat.getHandleType());
		if(message == null) {
			return;
		}
		int hash = message.hashCode();
		mcp.addMessage(new ChatMessage(chat, message, coloredMessage, hash));
	}
}
