package mineverse.Aust1n46.chat.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import venture.Aust1n46.chat.model.ChatChannel;

/**
 * Event called when a player attempts to join a valid channel
 */
public class ChannelJoinEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    private ChatChannel channel;
    private String message;

    public ChannelJoinEvent(Player player, ChatChannel channel, String message) {
        this.player = player;
        this.channel = channel;
        this.message = message;
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

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}