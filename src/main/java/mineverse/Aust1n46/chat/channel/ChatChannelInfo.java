package mineverse.Aust1n46.chat.channel;

import mineverse.Aust1n46.chat.utilities.Format;

public class ChatChannelInfo {
	// DiscordSRV backwards compatibility
	@Deprecated
	public String FilterChat(String msg) {
		return Format.FilterChat(msg);
	}
	
	// DiscordSRV backwards compatibility
	@Deprecated
	public ChatChannel getChannelInfo(String channel) {
		return ChatChannel.getChannel(channel);
	}
}