package mineverse.Aust1n46.chat.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import mineverse.Aust1n46.chat.ChatMessage;

/**
 * Event called when a message has been sent to a channel.
 * This event can not be cancelled.
 * @author Mark Hughes, Aust1n46
 */
public class ChatMessageEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final ChatMessage chatMessage;
	private final String json;
	
	public ChatMessageEvent(ChatMessage chatMessage, String json) {
		this.chatMessage = chatMessage;
		this.json = json;
	}

	public ChatMessage getChatMessage() {
		return this.chatMessage;
	}
	
	public String getJson() {
		return this.json;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}