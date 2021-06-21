package mineverse.Aust1n46.chat.api.events;

import mineverse.Aust1n46.chat.channel.ChatChannel;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

//This class is a custom event that is part of the plugins API.  It is called when a player executes the mute command.
public class MutePlayerEvent extends Event implements Cancellable {	//unimplemented
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Player player;
	private ChatChannel channel;
	private int time;
	
	public MutePlayerEvent(Player player, ChatChannel channel, int time) {
		this.player = player;
		this.channel = channel;
		this.time = time;
		this.cancelled = false;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void setChannel(ChatChannel channel) {
		this.channel = channel;
	}
	
	public ChatChannel getChannel() {
		return this.channel;
	}
	
	public int getTime() {
		return this.time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
}