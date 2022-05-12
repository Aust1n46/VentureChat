package venture.Aust1n46.chat.api.events;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureChatPlayer;

/**
 * Event called when a message has been sent to a channel. This event can not be
 * cancelled.
 *
 * @author Aust1n46
 */
@Getter
public class VentureChatEvent extends Event {
	private static final boolean ASYNC = true;
	private static final HandlerList HANDLERS = new HandlerList();

	private final VentureChatPlayer ventureChatPlayer;
	private final String username;
	private final String playerPrimaryGroup;
	private final ChatChannel channel;
	private final Set<Player> recipients;
	private final int recipientCount;
	private final String format;
	private final String chat;
	private final String globalJSON;
	private final int hash;
	private final boolean bungee;

	public VentureChatEvent(final VentureChatPlayer ventureChatPlayer, final String username, final String playerPrimaryGroup, final ChatChannel channel,
			final Set<Player> recipients, final int recipientCount, final String format, final String chat, final String globalJSON, final int hash, final boolean bungee) {
		super(ASYNC);
		this.ventureChatPlayer = ventureChatPlayer;
		this.username = username;
		this.playerPrimaryGroup = playerPrimaryGroup;
		this.channel = channel;
		this.recipients = recipients;
		this.recipientCount = recipientCount;
		this.format = format;
		this.chat = chat;
		this.globalJSON = globalJSON;
		this.hash = hash;
		this.bungee = bungee;
	}

	public String getConsoleChat() {
		return format + chat;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
