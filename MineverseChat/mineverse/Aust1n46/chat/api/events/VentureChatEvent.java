package mineverse.Aust1n46.chat.api.events;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;

/**
 * Event called when a message has been sent to a channel.
 * This event can not be cancelled.
 * @author Aust1n46
 */
public class VentureChatEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final MineverseChatPlayer mcp;
	private final ChatChannel channel;
	private final Set<Player> recipients;
	private final String format;
	private final String chat;
	private final String globalJSON;
	private final int hash;
	private final boolean bungee;
	
	public VentureChatEvent(MineverseChatPlayer mcp, ChatChannel channel, Set<Player> recipients, String format, String chat, String globalJSON, int hash, boolean bungee) {
		super(MineverseChat.ASYNC);
		this.mcp = mcp;
		this.channel = channel;
		this.recipients = recipients;
		this.format = format;
		this.chat = chat;
		this.globalJSON = globalJSON;
		this.hash = hash;
		this.bungee = bungee;
	}
	
	public MineverseChatPlayer getMineverseChatPlayer() {
		return this.mcp;
	}
	
	public ChatChannel getChannel() {
		return this.channel;
	}
	
	public Set<Player> getRecipients() {
		return this.recipients;
	}
	
	public String getFormat() {
		return this.format;
	}
	
	public String getChat() {
		return this.chat;
	}
	
	public String getConsoleChat() {
		return this.format + this.chat;
	}
	
	public String getGlobalJSON() {
		return this.globalJSON;
	}
	
	public int getHash() {
		return this.hash;
	}
	
	public boolean isBungee() {
		return this.bungee;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}