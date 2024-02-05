package mineverse.Aust1n46.chat.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.utilities.Format;

public class PacketListenerSystemChat extends PacketAdapter {
	public PacketListenerSystemChat() {
		super(MineverseChat.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Server.SYSTEM_CHAT);
	}

	@Override
	public void onPacketSending(final PacketEvent event) {
		if (event.isCancelled()) {
			return;
		}
		final MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(event.getPlayer());
		if (mcp == null) {
			return;
		}

		final WrappedChatComponent chat = event.getPacket().getChatComponents().read(0);
		if (chat == null) {
			return;
		}
		final String message = Format.toPlainText(chat.getHandle(), chat.getHandleType());
		final String coloredMessage = Format.toColoredText(chat.getHandle(), chat.getHandleType());
		if (message == null) {
			return;
		}
		mcp.addMessage(new ChatMessage(chat, message, coloredMessage));
	}
}
